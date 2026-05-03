package de.tu_dresden.inf.st.uvl.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.metamodel.util.ConstantSymbols;

/**
 * Represents a logical implication constraint: {@code left => right}
 *
 * <p>All structural behaviour (left/right children, toString, hashCode, equals,
 * getReferences, replaceConstraintSubPart) is inherited from
 * {@link BinaryConstraint}.</p>
 */
public class ImplicationConstraint extends BinaryConstraint {

    public ImplicationConstraint(Constraint left, Constraint right) {
        super(left, right);
    }

    @Override
    protected String operatorSymbol() {
        return ConstantSymbols.IMPLIES;
    }

    @Override
    public Constraint clone() {
        return new ImplicationConstraint(getLeft().clone(), getRight().clone());
    }
}
