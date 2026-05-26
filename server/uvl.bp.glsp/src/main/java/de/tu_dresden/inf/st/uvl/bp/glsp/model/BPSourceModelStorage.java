/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */

package de.tu_dresden.inf.st.uvl.bp.glsp.model;

import de.tu_dresden.inf.st.uvl.glsp.model.UVLSourceModelStorage;
import de.tu_dresden.inf.st.uvl.metamodel.main.ModelType;
import de.tu_dresden.inf.st.uvl.metamodel.main.UVLModelFactory;
import de.tu_dresden.inf.st.uvl.metamodel.model.BPFeatureModel;
import de.tu_dresden.inf.st.uvl.metamodel.model.FeatureModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.glsp.server.types.GLSPServerException;

public class BPSourceModelStorage extends UVLSourceModelStorage {

  @Override
  protected FeatureModel parseFeatureModel(final Path path) {
    try {
      String content = Files.readString(path);
      boolean isEmpty = content.trim().isEmpty();
      if (isEmpty) {
        return new BPFeatureModel();
      }
    } catch (IOException e) {
      LOGGER.error(e);
      throw new GLSPServerException("Could not load FeatureModel from file: " + path.toUri(), e);
    }

    UVLModelFactory uvlModelFactory = new UVLModelFactory();
    return uvlModelFactory.parse(path, ModelType.BP);
  }
}
