package de.tu_dresden.inf.st.uvl.glsp.utils;

import de.vill.model.Group;

public class GroupUtil {

    public static String getGroupName(Group group) {
        String parentName = group.getParentFeature().getFeatureName();
        String groupType = group.GROUPTYPE.toString().toLowerCase();
        return parentName + "_" + groupType;
    }

}
