/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.model;

import com.google.inject.Inject;
import de.vill.main.UVLModelFactory;
import de.vill.model.FeatureModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.glsp.graph.GGraph;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.server.actions.SaveModelAction;
import org.eclipse.glsp.server.features.core.model.RequestModelAction;
import org.eclipse.glsp.server.features.core.model.SourceModelStorage;
import org.eclipse.glsp.server.gmodel.GModelStorage;
import org.eclipse.glsp.server.types.GLSPServerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UVLSourceModelStorage extends GModelStorage implements SourceModelStorage {

    protected static Logger LOGGER = LogManager.getLogger(UVLSourceModelStorage.class.getSimpleName());

    @Inject
    protected UVLModelState modelState;

    @Override
    public void loadSourceModel(RequestModelAction action) {
        File featureModelFile = convertToFile(action.getOptions());
        loadFeatureModel(featureModelFile);

        String filePath = featureModelFile.getAbsolutePath();
        File notationFile = getNotationFile(filePath);
        loadGModel(notationFile);
    }

    protected void loadFeatureModel(final File featureModelFile) {
        String filePath = featureModelFile.getAbsolutePath();

        try {
            String content = Files.readString(Paths.get(filePath));
            boolean isEmpty = content.trim().isEmpty();
            if (isEmpty) {
                modelState.setFeatureModel(new FeatureModel());
                return;
            }

            UVLModelFactory uvlModelFactory = new UVLModelFactory();
            FeatureModel featureModel = uvlModelFactory.parse(content);
            modelState.setFeatureModel(featureModel);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new GLSPServerException("Could not load FeatureModel from file: " + featureModelFile.toURI(), e);
        }
    }

    protected void loadGModel(final File notationFile) {
        try (Reader reader = new InputStreamReader(new FileInputStream(notationFile), StandardCharsets.UTF_8)) {
            GGraph root = gson.fromJson(reader, GGraph.class);
            if (root == null) {
                boolean isEmpty = notationFile.length() == 0;
                if (isEmpty) {
                    GModelRoot newRoot = createNewEmptyRoot(modelState);
                    modelState.updateRoot(newRoot);
                    modelState.getRoot().setRevision(-1);
                    return;
                }
                throw new IOException("Could not deserialize GModel file contents of: " + notationFile.toURI());
            }
            modelState.updateRoot(root);
            modelState.getRoot().setRevision(-1);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new GLSPServerException("Could not load GModel from file: " + notationFile.toURI(), e);
        }
    }

    @Override
    public void saveSourceModel(SaveModelAction action) {
        File featureModelFile = convertToFile(action);
        saveFeatureModel(featureModelFile);

        String filePath = featureModelFile.getAbsolutePath();
        File notationFile = getNotationFile(filePath);
        saveGModel(notationFile);
    }

    protected void saveFeatureModel(final File featureModelFile) {
        final FeatureModel featureModel = modelState.getFeatureModel();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(featureModelFile), StandardCharsets.UTF_8)) {
            String uvlModel = featureModel.toString();
            writer.write(uvlModel);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new GLSPServerException("An error occurred during save process of the FeatureModel file.", e);
        }

    }

    protected void saveGModel(final File notationFile) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(notationFile), StandardCharsets.UTF_8)) {
            gson.toJson(modelState.getRoot(), GGraph.class, writer);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new GLSPServerException("An error occurred during save process of the GModel file.", e);
        }
    }

    private static File getNotationFile(String featureModelFilePath) {
        String notationFilePath = featureModelFilePath.replaceAll("\\.uvl$", ".notation.json");
        Path path = Paths.get(notationFilePath);
        if (!Files.exists(path)) {
            LOGGER.info("GModel file does not exist: {}. Creating default notation.", path.toUri());
            try {
                Files.createFile(path);
            } catch (IOException e) {
                LOGGER.error(e);
                throw new GLSPServerException("Could not create new GModel file: " + path.toUri(), e);
            }
        }
        return path.toFile();
    }
}
