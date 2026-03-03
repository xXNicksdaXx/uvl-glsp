package de.tu_dresden.inf.st.uvl.bp.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.bp.metamodel.util.ConstantSymbols;

/**
 * Represents a logical AND constraint: {@code left & right [& ...]}
 *
 * <p>All structural behaviour (children list, toString, hashCode, equals,
 * getReferences, replaceConstraintSubPart) is inherited from
 * {@link NaryLogicConstraint}.</p>
 */
public class AndConstraint extends NaryLogicConstraint {

    public AndConstraint(Constraint... constraints) {
        super(constraints);
    }

    public AndConstraint(Constraint left, Constraint right) {
        super(left, right);
    }

    public AndConstraint() {
        super();
    }

    @Override
    protected String operatorSymbol() {
        return ConstantSymbols.AND;
    }

    @Override
    public Constraint clone() {
        AndConstraint clone = new AndConstraint();
        for (Constraint c : getChildren()) {
            clone.addChild(c.clone());
        }
        return clone;
    }
}
