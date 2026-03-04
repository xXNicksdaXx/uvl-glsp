package de.tu_dresden.inf.st.uvl.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.metamodel.model.expression.Expression;
import de.tu_dresden.inf.st.uvl.metamodel.util.ConstantSymbols;

public class LowerEquationConstraint extends ExpressionConstraint {

    public LowerEquationConstraint(final Expression left, final Expression right) {
        super(left, right);
    }

    @Override
    public String operatorSymbol() { return ConstantSymbols.LOWER; }

    @Override
    protected boolean compareValues(double l, double r) { return l < r; }

    @Override
    public Constraint clone() {
        return new LowerEquationConstraint(getLeft().clone(), getRight().clone());
    }
}
