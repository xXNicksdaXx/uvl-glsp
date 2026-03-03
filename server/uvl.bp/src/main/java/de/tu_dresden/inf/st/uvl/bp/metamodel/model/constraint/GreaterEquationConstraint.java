package de.tu_dresden.inf.st.uvl.bp.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.bp.metamodel.model.expression.Expression;
import de.tu_dresden.inf.st.uvl.bp.metamodel.util.ConstantSymbols;

public class GreaterEquationConstraint extends ExpressionConstraint {

    public GreaterEquationConstraint(final Expression left, final Expression right) {
        super(left, right);
    }

    @Override
    public String operatorSymbol() { return ConstantSymbols.GREATER; }

    @Override
    protected boolean compareValues(double l, double r) { return l > r; }

    @Override
    public Constraint clone() {
        return new GreaterEquationConstraint(getLeft().clone(), getRight().clone());
    }
}
