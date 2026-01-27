/*
 * Copyright © 2026 Nick Ruider. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>.
 */
package de.tu_dresden.inf.st.uvl.glsp.model;

import de.vill.model.Feature;
import de.vill.model.FeatureModel;
import de.vill.model.Group;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.glsp.graph.*;
import org.eclipse.glsp.graph.impl.GModelIndexImpl;
import org.eclipse.glsp.graph.util.RootAdapterUtil;
import org.eclipse.glsp.server.utils.BiIndex;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static de.tu_dresden.inf.st.uvl.glsp.utils.GroupUtil.getGroupName;

public class UVLModelIndex extends GModelIndexImpl {

    protected BiIndex<String, Object> uvlIndex;

    protected UVLModelIndex(final EObject target) {
        super(target);
        this.uvlIndex = new BiIndex<>();
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

    protected void indexFeatureModel(final FeatureModel featureModel) {
        featureModel.getFeatureMap().values().forEach(this::indexFeature);
        featureModel.getFeatureMap().values().stream()
                .map(Feature::getParentGroup)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(this::indexGroup);
    }

    private void indexFeature(final Feature feature) {
        String id = getOrCreateId(feature.getFeatureName());
        uvlIndex.putIfAbsent(id, feature);
    }

    private void indexGroup(final Group group) {
        String id = getOrCreateId(getGroupName(group));
        uvlIndex.putIfAbsent(id, group);
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

    protected String getOrCreateId(final Object element) {
        if (element instanceof Feature feature) {
            return feature.getFeatureName();
        } else if (element instanceof Group group) {
            return getGroupName(group);
        } else {
            return Objects.toString(element);
        }
    }

    public String getIdFor(final Object object) {
        return uvlIndex.getKey(object);
    }

    public Set<String> allFeatureModelIds() {
        return this.uvlIndex.map().keySet();
    }

    public Set<String> allGModelIds() {
        return allIds();
    }

    protected <T> Optional<T> safeCast(final Optional<?> toCast, final Class<T> clazz) {
        return toCast.filter(clazz::isInstance).map(clazz::cast);
    }

    public static UVLModelIndex getOrCreate(final GModelElement element) {
        return RootAdapterUtil.getOrCreate(element, UVLModelIndex::new, UVLModelIndex.class);
    }
}
