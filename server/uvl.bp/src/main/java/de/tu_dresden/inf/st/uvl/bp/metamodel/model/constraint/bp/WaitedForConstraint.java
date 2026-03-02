package de.tu_dresden.inf.st.uvl.bp.metamodel.model.constraint.bp;

import de.tu_dresden.inf.st.uvl.bp.metamodel.model.building.VariableReference;
import de.tu_dresden.inf.st.uvl.bp.metamodel.model.constraint.Constraint;
import de.tu_dresden.inf.st.uvl.bp.metamodel.util.ConstantSymbols;

/**
 * Represents a BP event constraint of the form: {@code waited_for(<reference>)}.
 */
public class WaitedForConstraint extends AbstractBPEventConstraint {

    public WaitedForConstraint(VariableReference reference) {
        super(reference, "waited_for");
    }

    @Override
    public Constraint clone() {
        WaitedForConstraint clone = new WaitedForConstraint(getReference());
        clone.setLineNumber(getLineNumber());
        return clone;
    }
}

