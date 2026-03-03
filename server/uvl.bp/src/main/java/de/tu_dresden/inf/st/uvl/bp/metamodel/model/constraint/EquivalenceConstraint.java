package de.tu_dresden.inf.st.uvl.bp.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.bp.metamodel.util.ConstantSymbols;

/**
 * Represents a logical equivalence constraint: {@code left <=> right}
 *
 * <p>All structural behaviour is inherited from {@link BinaryConstraint}.</p>
 */
public class EquivalenceConstraint extends BinaryConstraint {

    public EquivalenceConstraint(Constraint left, Constraint right) {
        super(left, right);
    }

    @Override
    protected String operatorSymbol() {
        return ConstantSymbols.EQUIVALENT;
    }

    @Override
    public Constraint clone() {
        return new EquivalenceConstraint(getLeft().clone(), getRight().clone());
    }
}
