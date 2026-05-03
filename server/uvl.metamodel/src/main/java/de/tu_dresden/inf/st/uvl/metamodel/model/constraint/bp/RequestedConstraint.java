package de.tu_dresden.inf.st.uvl.metamodel.model.constraint.bp;

import de.tu_dresden.inf.st.uvl.metamodel.model.building.VariableReference;
import de.tu_dresden.inf.st.uvl.metamodel.model.constraint.Constraint;

/**
 * Represents a BP event constraint of the form: {@code requested(<reference>)}.
 */
public class RequestedConstraint extends AbstractBPEventConstraint {

    public RequestedConstraint(VariableReference reference) {
        super(reference, "requested");
    }

    @Override
    public Constraint clone() {
        RequestedConstraint clone = new RequestedConstraint(getReference());
        clone.setLineNumber(getLineNumber());
        return clone;
    }
}
