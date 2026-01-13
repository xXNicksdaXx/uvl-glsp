/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.model;

import de.tu_dresden.inf.st.uvl.glsp.utils.UVLIdGenerator;
import de.vill.model.Feature;
import de.vill.model.FeatureModel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.impl.GModelIndexImpl;
import org.eclipse.glsp.graph.util.RootAdapterUtil;
import org.eclipse.glsp.server.utils.BiIndex;

import java.util.Map;
import java.util.Optional;

public class UVLModelIndex extends GModelIndexImpl {

    protected UVLIdGenerator idGenerator;

    protected BiIndex<String, Object> uvlIndex;

    protected UVLModelIndex(final EObject target, final UVLIdGenerator idGenerator) {
        super(target);
        this.uvlIndex = new BiIndex<>();
        this.idGenerator = idGenerator;
    }

    @Override
    public boolean isAdapterForType(final Object type) {
        return super.isAdapterForType(type) || UVLModelIndex.class.equals(type);
    }

    @Override
    public void clear() {
        super.clear();
        this.uvlIndex.clear();
    }

    public void indexFeatureModel(final FeatureModel featureModel) {
        featureModel.getFeatureMap().values().forEach(this::indexFeature);
    }

    public void indexFeature(final Feature feature) {
        String id = getOrCreateId(feature.getFeatureName(), uvlIndex.inverseMap());
        uvlIndex.putIfAbsent(id, feature);
    }

    public Optional<Object> getUVLObject(final String id) {
        return Optional.ofNullable(uvlIndex.get(id));
    }

    public <T> Optional<T> getUVLObject(final String id, final Class<T> clazz) {
        return safeCast(Optional.ofNullable(uvlIndex.get(id)), clazz);
    }

    public Optional<Object> getUVLObject(final GModelElement gModelElement) {
        return getUVLObject(gModelElement.getId());
    }

    public <T> Optional<T> getUVLObject(final GModelElement gModelElement, final Class<T> clazz) {
        return getUVLObject(gModelElement.getId(), clazz);
    }

    public Optional<GModelElement> getGModelElement(final String id) {
        return get(id);
    }

    public <T extends GModelElement> Optional<T> getGModelElement(final String id, final Class<T> clazz) {
        return safeCast(get(id), clazz);
    }

    public Optional<GModelElement> getGModelElement(final Object object) {
        return getGModelElement(uvlIndex.getKey(object));
    }

    public <T extends GModelElement> Optional<T> getGModelElement(final Object object, final Class<T> clazz) {
        return getGModelElement(uvlIndex.getKey(object), clazz);
    }

    protected String getOrCreateId(final Object element, final Map<Object, String > idMap) {
        return idGenerator.getOrCreateId(element, idMap);
    }

    public java.util.Optional<String> getIdFor(final Object object) {
        return java.util.Optional.ofNullable(uvlIndex.getKey(object));
    }

    protected <T> Optional<T> safeCast(final Optional<?> toCast, final Class<T> clazz) {
        return toCast.filter(clazz::isInstance).map(clazz::cast);
    }

    public static UVLModelIndex getOrCreate(final GModelElement element, final UVLIdGenerator idGenerator) {
        return RootAdapterUtil.getOrCreate(element, root -> new UVLModelIndex(root, idGenerator), UVLModelIndex.class);
    }
}
