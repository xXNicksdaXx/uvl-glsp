package de.tu_dresden.inf.st.uvl.bp.metamodel.model.constraint.bp;

import de.tu_dresden.inf.st.uvl.bp.metamodel.model.building.VariableReference;
import de.tu_dresden.inf.st.uvl.bp.metamodel.model.constraint.Constraint;
import de.tu_dresden.inf.st.uvl.bp.metamodel.util.ConstantSymbols;

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

