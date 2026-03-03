package de.tu_dresden.inf.st.uvl.bp.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.bp.metamodel.util.ConstantSymbols;

/**
 * Represents a logical OR constraint: {@code left | right [| ...]}
 *
 * <p>All structural behaviour is inherited from {@link NaryLogicConstraint}.</p>
 */
public class OrConstraint extends NaryLogicConstraint {

    public OrConstraint(Constraint... constraints) {
        super(constraints);
    }

    public OrConstraint(Constraint left, Constraint right) {
        super(left, right);
    }

    public OrConstraint() {
        super();
    }

    @Override
    protected String operatorSymbol() {
        return ConstantSymbols.OR;
    }

    @Override
    public Constraint clone() {
        OrConstraint clone = new OrConstraint();
        for (Constraint c : getChildren()) {
            clone.addChild(c.clone());
        }
        return clone;
    }
}
