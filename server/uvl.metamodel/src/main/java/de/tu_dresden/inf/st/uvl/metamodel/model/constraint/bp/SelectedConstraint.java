package de.tu_dresden.inf.st.uvl.metamodel.model.constraint.bp;

import de.tu_dresden.inf.st.uvl.metamodel.model.building.VariableReference;
import de.tu_dresden.inf.st.uvl.metamodel.model.constraint.Constraint;

/**
 * Represents a BP event constraint of the form: {@code selected(<reference>)}.
 */
public class SelectedConstraint extends AbstractBPEventConstraint {

    public SelectedConstraint(VariableReference reference) {
        super(reference, "selected");
    }

    @Override
    public Constraint clone() {
        SelectedConstraint clone = new SelectedConstraint(getReference());
        clone.setLineNumber(getLineNumber());
        return clone;
    }
}
