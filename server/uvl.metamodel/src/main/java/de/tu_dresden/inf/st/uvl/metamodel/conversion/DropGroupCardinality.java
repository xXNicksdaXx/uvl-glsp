package de.tu_dresden.inf.st.uvl.metamodel.conversion;

import de.tu_dresden.inf.st.uvl.metamodel.model.Feature;
import de.tu_dresden.inf.st.uvl.metamodel.model.FeatureModel;
import de.tu_dresden.inf.st.uvl.metamodel.model.Group;
import de.tu_dresden.inf.st.uvl.metamodel.model.LanguageLevel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DropGroupCardinality implements IConversionStrategy {
    @Override
    public Set<LanguageLevel> getLevelsToBeRemoved() {
        return new HashSet<>(List.of(LanguageLevel.GROUP_CARDINALITY));
    }

    @Override
    public Set<LanguageLevel> getTargetLevelsOfConversion() {
        return new HashSet<>();
    }

    @Override
    public void convertFeatureModel(FeatureModel rootFeatureModel, FeatureModel featureModel) {
        removeGroupCardinalityRecursively(featureModel.getRootFeature());
    }

    private void removeGroupCardinalityRecursively(Feature feature) {
        for (Group group : feature.getChildren()) {
            if (group.GROUPTYPE.equals(Group.GroupType.GROUP_CARDINALITY)) {
                Group newGroup = new Group(Group.GroupType.OPTIONAL);
                newGroup.getFeatures().addAll(group.getFeatures());
                feature.getChildren().remove(group);
                feature.getChildren().add(newGroup);
                //replace with other group
            }
            //don't traverse the whole featuremodel. Stop when a new submodel starts
            if (!feature.isSubmodelRoot()) {
                for (Feature childFeature : group.getFeatures()) {
                    removeGroupCardinalityRecursively(childFeature);
                }
            }
        }
    }
}
