package de.tu_dresden.inf.st.uvl.metamodel.conversion;

import de.tu_dresden.inf.st.uvl.metamodel.model.Feature;
import de.tu_dresden.inf.st.uvl.metamodel.model.FeatureModel;
import de.tu_dresden.inf.st.uvl.metamodel.model.Group;
import de.tu_dresden.inf.st.uvl.metamodel.model.LanguageLevel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DropFeatureCardinality implements IConversionStrategy {
    @Override
    public Set<LanguageLevel> getLevelsToBeRemoved() {
        return new HashSet<>(List.of(LanguageLevel.FEATURE_CARDINALITY));
    }

    @Override
    public Set<LanguageLevel> getTargetLevelsOfConversion() {
        return new HashSet<>();
    }

    @Override
    public void convertFeatureModel(FeatureModel rootFeatureModel, FeatureModel featureModel) {
        removeFeatureCardinalityRecursively(featureModel.getRootFeature());
    }

    private void removeFeatureCardinalityRecursively(Feature feature) {
        feature.setCardinality(null);
        for (Group group : feature.getChildren()) {
            for (Feature childFeature : group.getFeatures()) {
                //stop when feature is submodelroot to only consider this featuremodel and no submodels
                if (!feature.isSubmodelRoot()) {
                    removeFeatureCardinalityRecursively(childFeature);
                }
            }
        }
    }
}
