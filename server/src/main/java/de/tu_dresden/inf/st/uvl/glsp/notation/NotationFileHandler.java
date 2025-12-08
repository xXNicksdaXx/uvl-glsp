/*
 * Copyright © 2025 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.notation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.vill.model.Feature;
import de.vill.model.FeatureModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static de.tu_dresden.inf.st.uvl.glsp.utils.FeatureUtil.getFeatureId;

public class NotationFileHandler {

    protected static Logger LOGGER = LogManager.getLogger(NotationFileHandler.class.getSimpleName());

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ElementNotation.class, new ElementNotationAdapter())
            .registerTypeAdapter(EdgeNotation.class, new EdgeNotationAdapter())
            .create();

    public static NotationData loadNotationFile(String uvlFilePath) throws IOException {
        Path notationPath = getNotationPath(uvlFilePath);

        if (!Files.exists(notationPath)) {
            LOGGER.info("Notation file does not exist: " + notationPath + ". Creating default notation.");
            return new NotationData();
        }

        String content = new String(Files.readAllBytes(notationPath));
        return GSON.fromJson(content, NotationData.class);
    }

    public static void saveNotationFile(String uvlFilePath, NotationData notationData) throws IOException {
        Path notationPath = getNotationPath(uvlFilePath);
        String json = GSON.toJson(notationData);
        Files.write(notationPath, json.getBytes());
        LOGGER.info("Saved notation file: " + notationPath);
    }

    public static NotationData createDefaultNotation(FeatureModel featureModel) {
        NotationData notationData = new NotationData();

        if (featureModel != null && featureModel.getRootFeature() != null) {
            int yOffset = 0;
            for (Feature feature : featureModel.getFeatureMap().values()) {
                createDefaultNotationForFeature(feature, 0, yOffset, notationData);
                yOffset += 64;
            }
        }

        return notationData;
    }

    private static void createDefaultNotationForFeature(Feature feature, double x, double y, NotationData notationData) {
        if (feature == null) {
            return;
        }
        String featureId = getFeatureId(feature);
        String featureName = feature.getFeatureName();
        notationData.setElementNotation(featureName,
            new ElementNotationImpl(featureId, x, y, 64, 32));
    }

    private static Path getNotationPath(String uvlFilePath) {
        String notationFilePath = uvlFilePath.replaceAll("\\.uvl$", ".notation.json");
        return Paths.get(notationFilePath);
    }

    private static class ElementNotationAdapter extends TypeAdapter<ElementNotation> {
        @Override
        public void write(JsonWriter out, ElementNotation value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            out.name("id").value(value.getId());
            out.name("x").value(value.getX());
            out.name("y").value(value.getY());
            out.name("width").value(value.getWidth());
            out.name("height").value(value.getHeight());
            out.endObject();
        }

        @Override
        public ElementNotation read(JsonReader in) throws IOException {
            String id = null;
            double x = 0, y = 0, width = 0, height = 0;

            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                switch (name) {
                    case "id":
                        id = in.nextString();
                        break;
                    case "x":
                        x = in.nextDouble();
                        break;
                    case "y":
                        y = in.nextDouble();
                        break;
                    case "width":
                        width = in.nextDouble();
                        break;
                    case "height":
                        height = in.nextDouble();
                        break;
                    default:
                        in.skipValue();
                        break;
                }
            }
            in.endObject();

            return new ElementNotationImpl(id, x, y, width, height);
        }
    }

    private static class EdgeNotationAdapter extends TypeAdapter<EdgeNotation> {
        @Override
        public void write(JsonWriter out, EdgeNotation value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            out.name("id").value(value.getId());
            out.name("sourceId").value(value.getSourceId());
            out.name("targetId").value(value.getTargetId());
            out.endObject();
        }

        @Override
        public EdgeNotation read(JsonReader in) throws IOException {
            String id = null, sourceId = null, targetId = null;

            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                switch (name) {
                    case "id":
                        id = in.nextString();
                        break;
                    case "sourceId":
                        sourceId = in.nextString();
                        break;
                    case "targetId":
                        targetId = in.nextString();
                        break;
                    default:
                        in.skipValue();
                        break;
                }
            }
            in.endObject();

            return new EdgeNotationImpl(id, sourceId, targetId);
        }
    }
}
