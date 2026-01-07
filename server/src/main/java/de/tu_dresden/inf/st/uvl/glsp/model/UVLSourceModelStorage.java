/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.model;

import com.google.inject.Inject;
import de.tu_dresden.inf.st.uvl.glsp.notation.NotationData;
import de.tu_dresden.inf.st.uvl.glsp.notation.NotationFileHandler;
import de.vill.main.UVLModelFactory;
import de.vill.model.FeatureModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.glsp.server.actions.SaveModelAction;
import org.eclipse.glsp.server.features.core.model.RequestModelAction;
import org.eclipse.glsp.server.features.core.model.SourceModelStorage;
import org.eclipse.glsp.server.types.GLSPServerException;
import org.eclipse.glsp.server.utils.ClientOptionsUtil;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.eclipse.glsp.server.types.GLSPServerException.getOrThrow;

public class UVLSourceModelStorage implements SourceModelStorage {

    protected static Logger LOGGER = LogManager.getLogger(UVLSourceModelStorage.class.getSimpleName());

    @Inject
    protected UVLModelState modelState;

    @Override
    public void loadSourceModel(RequestModelAction action) {
        File file = convertToFile(action.getOptions());
        String filePath = file.getAbsolutePath();

        try {
            FeatureModel featureModel = loadUVLFeatureModelFromFile(filePath);
            modelState.setUVLModel(featureModel);

            NotationData notationData = loadNotationDataFromFile(filePath, featureModel);
            modelState.setNotationData(notationData);
        }  catch (IndexOutOfBoundsException e) {
            FeatureModel featureModel = new FeatureModel();
            modelState.setUVLModel(featureModel);

            try {
                NotationData notationData = loadNotationDataFromFile(filePath, featureModel);
                modelState.setNotationData(notationData);
            } catch (IOException ex) {
                throw new GLSPServerException(e.getMessage());
            }
        } catch (IOException e) {
            throw new GLSPServerException(e.getMessage());
        }
    }

    @Override
    public void saveSourceModel(SaveModelAction action) {
        final FeatureModel featureModel = modelState.getUVLModel();
        final NotationData notationData = modelState.getNotationData();
        final String fileUri = action.getFileUri()
                .orElseThrow(() -> new GLSPServerException("No file URI given!"));
        final URI uri = URI.create(fileUri);

        try {
            String uvlModel = featureModel.toString();
            Path filePath = Paths.get(uri);
            Files.write(filePath, uvlModel.getBytes());

            // Save notation data
            if (notationData != null) {
                NotationFileHandler.saveNotationFile(filePath.toString(), notationData);
            }
        } catch (IOException e) {
            LOGGER.error(e);
            throw new GLSPServerException("An error occurred while saving the model.", e);
        }
    }

    private static FeatureModel loadUVLFeatureModelFromFile(String path) throws IOException {
        Path filePath = Paths.get(path);
        String content = new String(Files.readAllBytes(filePath));
        UVLModelFactory uvlModelFactory = new UVLModelFactory();
        return uvlModelFactory.parse(content);
    }

    private static NotationData loadNotationDataFromFile(String path, FeatureModel featureModel) throws IOException {
        NotationData notationData;
        try {
            notationData = NotationFileHandler.loadNotationFile(path);
            if (notationData.getElements().isEmpty()) {
                LOGGER.info("Notation file is empty. Creating default notation.");
                notationData = NotationFileHandler.createDefaultNotation(featureModel);
                NotationFileHandler.saveNotationFile(path, notationData);
            }
        } catch (IOException e) {
            LOGGER.warn("Could not load notation file. Creating default notation.", e);
            notationData = NotationFileHandler.createDefaultNotation(featureModel);
            NotationFileHandler.saveNotationFile(path, notationData);
        }
        return notationData;
    }

    protected File convertToFile(final Map<String, String> clientOptions) {
        return getOrThrow(ClientOptionsUtil.getSourceUriAsFile(clientOptions),
                "Invalid file URI:" + ClientOptionsUtil.getSourceUri(clientOptions));
    }
}
