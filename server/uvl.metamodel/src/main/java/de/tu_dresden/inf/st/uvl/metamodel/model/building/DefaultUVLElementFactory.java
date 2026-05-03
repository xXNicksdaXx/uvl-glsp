package de.tu_dresden.inf.st.uvl.metamodel.model.building;

import de.tu_dresden.inf.st.uvl.metamodel.model.Attribute;
import de.tu_dresden.inf.st.uvl.metamodel.model.Feature;
import de.tu_dresden.inf.st.uvl.metamodel.model.FeatureModel;
import de.tu_dresden.inf.st.uvl.metamodel.model.GlobalAttribute;

public class DefaultUVLElementFactory extends AbstractUVLElementFactory {

    @Override
    public Feature createFeature(String name) {
        return new Feature(name);
    }

    @Override
    public <T> Attribute<T> createAttribute(String name, T value, Feature correspondingFeature) {
        return new Attribute<>(name, value, correspondingFeature);
    }

    @Override
    public GlobalAttribute createGlobalAttribute(String identifier, FeatureModel featureModel) {
        return new GlobalAttribute(identifier, featureModel);
    }
}
