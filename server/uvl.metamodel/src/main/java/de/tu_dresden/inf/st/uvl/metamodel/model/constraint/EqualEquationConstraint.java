package de.tu_dresden.inf.st.uvl.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.metamodel.model.expression.Expression;
import de.tu_dresden.inf.st.uvl.metamodel.util.ConstantSymbols;

public class EqualEquationConstraint extends ExpressionConstraint {

    public EqualEquationConstraint(final Expression left, final Expression right) {
        super(left, right);
    }

    @Override
    public String operatorSymbol() { return ConstantSymbols.EQUALS; }

    @Override
    protected boolean compareValues(double l, double r) { return l == r; }

    @Override
    public Constraint clone() {
        return new EqualEquationConstraint(getLeft().clone(), getRight().clone());
    }
}
