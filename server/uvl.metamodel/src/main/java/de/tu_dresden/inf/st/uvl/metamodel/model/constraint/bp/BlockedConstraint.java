package de.tu_dresden.inf.st.uvl.metamodel.model.constraint.bp;

import de.tu_dresden.inf.st.uvl.metamodel.model.building.VariableReference;
import de.tu_dresden.inf.st.uvl.metamodel.model.constraint.Constraint;

/**
 * Represents a BP event constraint of the form: {@code blocked(<reference>)}.
 */
public class BlockedConstraint extends AbstractBPEventConstraint {

    public BlockedConstraint(VariableReference reference) {
        super(reference, "blocked");
    }

    @Override
    public Constraint clone() {
        BlockedConstraint clone = new BlockedConstraint(getReference());
        clone.setLineNumber(getLineNumber());
        return clone;
    }
}
