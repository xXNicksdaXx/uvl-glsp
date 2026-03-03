package de.tu_dresden.inf.st.uvl.bp.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.bp.metamodel.model.expression.Expression;
import de.tu_dresden.inf.st.uvl.bp.metamodel.util.ConstantSymbols;

public class NotEqualsEquationConstraint extends ExpressionConstraint {

    public NotEqualsEquationConstraint(final Expression left, final Expression right) {
        super(left, right);
    }

    @Override
    public String operatorSymbol() { return ConstantSymbols.NOT_EQUALS; }

    @Override
    protected boolean compareValues(double l, double r) { return l != r; }

    @Override
    public Constraint clone() {
        return new NotEqualsEquationConstraint(getLeft().clone(), getRight().clone());
    }
}
