/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.utils;

import de.tu_dresden.inf.st.uvl.glsp.UVLModelTypes;
import de.vill.model.Feature;
import de.vill.model.FeatureModel;
import de.vill.model.constraint.Constraint;
import de.vill.model.constraint.EquivalenceConstraint;
import de.vill.model.constraint.ImplicationConstraint;
import de.vill.model.constraint.LiteralConstraint;

import java.util.List;

public class ConstraintUtil {

    public static boolean featureIsInConstraint(Feature feature, FeatureModel featureModel) {
        List<LiteralConstraint> literalConstraints = featureModel.getLiteralConstraints();
        for (LiteralConstraint constraint : literalConstraints) {
            if (constraint.getFeature() == feature) {
                return true;
            }
        }
        return false;
    }

    public static LiteralConstraint getLiteralConstraint(Feature feature, FeatureModel featureModel) {
        List<LiteralConstraint> literalConstraints = featureModel.getLiteralConstraints();
        for (LiteralConstraint constraint : literalConstraints) {
            if (constraint.getFeature() == feature) {
                return constraint;
            }
        }
        return null;
    }

    public static List<Constraint> getConstraintsForFeature(Feature feature, FeatureModel featureModel) {
        return featureModel.getConstraints().stream()
                .filter(constraint -> constraint.getConstraintSubParts().contains(getLiteralConstraint(feature, featureModel)))
                .toList();
    }

    public static String convertConstraintTypeToModelType(Constraint constraint) {
        return switch (constraint) {
            case ImplicationConstraint ignored -> UVLModelTypes.IMPLICATION;
            case EquivalenceConstraint ignored -> UVLModelTypes.EQUIVALENCE;
            default -> "";
        };
    }
}
