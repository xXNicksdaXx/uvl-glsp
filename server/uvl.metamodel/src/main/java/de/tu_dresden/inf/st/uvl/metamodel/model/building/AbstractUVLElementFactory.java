package de.tu_dresden.inf.st.uvl.metamodel.model.building;

import de.tu_dresden.inf.st.uvl.metamodel.model.Attribute;
import de.tu_dresden.inf.st.uvl.metamodel.model.Feature;
import de.tu_dresden.inf.st.uvl.metamodel.model.FeatureModel;
import de.tu_dresden.inf.st.uvl.metamodel.model.GlobalAttribute;

public abstract class AbstractUVLElementFactory {

    public abstract Feature createFeature(String name);

    public abstract <T> Attribute<T> createAttribute(String name, T value, Feature correspondingFeature);

    public abstract GlobalAttribute createGlobalAttribute(String identifier, FeatureModel featureModel);

}