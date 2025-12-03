/*
 * Copyright © 2025 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.model;

import com.google.inject.Inject;
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
        } catch (IOException e) {
            throw new GLSPServerException(e.getMessage());
        }
    }

    @Override
    public void saveSourceModel(SaveModelAction action) {
        final FeatureModel featureModel = modelState.getUVLModel();
        final String fileUri = action.getFileUri()
                .orElseThrow(() -> new GLSPServerException("No file URI given!"));
        final URI uri = URI.create(fileUri);

        try {
            String uvlModel = featureModel.toString();
            Path filePath = Paths.get(uri);
            Files.write(filePath, uvlModel.getBytes());
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

    protected File convertToFile(final Map<String, String> clientOptions) {
        return getOrThrow(ClientOptionsUtil.getSourceUriAsFile(clientOptions),
                "Invalid file URI:" + ClientOptionsUtil.getSourceUri(clientOptions));
    }
}
