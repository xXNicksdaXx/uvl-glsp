package de.tu_dresden.inf.st.uvl.bp.metamodel.model;

import de.tu_dresden.inf.st.uvl.bp.metamodel.util.Util;

/**
 * Extension of the base FeatureModel to include additional top-level features specific to Behavioral Programming (BP).
 * This class allows for the inclusion of 'Env' and 'Config' features, which are common in BP contexts.
 */
public class BPFeatureModel extends FeatureModel {

    /**
     * Additional top-level features for BP ('Env' and 'Config').
     * These are stored separately from the feature tree because they represent
     * special configuration/environment features in the Behavioral Programming context.
     */
    private Feature env;
    private Feature config;

    /**
     * Get the 'Env' feature of the feature model (BP extension)
     *
     * @return 'Env' feature
     */
    public Feature getEnv() {
        return env;
    }

    /**
     * Set the 'Env' feature of the feature model (BP extension)
     *
     * @param env 'Env' feature
     */
    public void setEnv(Feature env) {
        this.env = env;
    }

    /**
     * Get the 'Config' feature of the feature model (BP extension)
     *
     * @return 'Config' feature
     */
    public Feature getConfig() {
        return config;
    }

    /**
     * Set the 'Config' feature of the feature model (BP extension)
     *
     * @param config 'Config' feature
     */
    public void setConfig(Feature config) {
        this.config = config;
    }

    @Override
    protected void appendAdditionalTopLevelFeatures(StringBuilder result, boolean withSubmodels, String currentAlias) {
        if (env != null) {
            result.append(Util.indentEachLine(env.toString(withSubmodels, currentAlias)));
        }
        if (config != null) {
            result.append(Util.indentEachLine(config.toString(withSubmodels, currentAlias)));
        }
    }
}
