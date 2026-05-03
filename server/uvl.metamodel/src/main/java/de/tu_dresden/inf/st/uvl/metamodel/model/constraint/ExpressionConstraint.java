package de.tu_dresden.inf.st.uvl.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.metamodel.model.Feature;
import de.tu_dresden.inf.st.uvl.metamodel.model.building.VariableReference;
import de.tu_dresden.inf.st.uvl.metamodel.model.expression.Expression;

import java.util.*;

/**
 * Abstract base class for numeric equation constraints (e.g. {@code a = b}, {@code a > b}).
 *
 * <p>Each concrete subclass represents a single comparison operator. The
 * operator-specific string used for printing is provided via
 * {@link #operatorSymbol()}, and the actual numeric comparison is delegated to
 * {@link #compareValues(double, double)} — both follow the <em>Template Method</em>
 * pattern so that neither the symbol string nor the branching logic need to live
 * in this base class.</p>
 */
public abstract class ExpressionConstraint extends Constraint {

    private Expression left;
    private Expression right;

    protected ExpressionConstraint(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    // ── accessor API ──────────────────────────────────────────────────────────

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public void setLeft(Expression expression) {
        left = expression;
    }

    public void setRight(Expression expression) {
        right = expression;
    }

    // ── template methods ──────────────────────────────────────────────────────

    /**
     * Returns the infix operator symbol used when printing, e.g. {@code "="} or {@code ">"}.
     */
    public abstract String operatorSymbol();

    /**
     * Performs the actual numeric comparison for {@link #evaluate(Set)}.
     *
     * @param leftValue  evaluated numeric result of the left expression
     * @param rightValue evaluated numeric result of the right expression
     * @return {@code true} if the constraint is satisfied
     */
    protected abstract boolean compareValues(double leftValue, double rightValue);

    // ── Constraint implementation ─────────────────────────────────────────────

    @Override
    public String toString(boolean withSubmodels, String currentAlias) {
        return left.toString(withSubmodels, currentAlias)
                + " " + operatorSymbol() + " "
                + right.toString(withSubmodels, currentAlias);
    }

    @Override
    public List<Constraint> getConstraintSubParts() {
        return Collections.emptyList();
    }

    @Override
    public void replaceConstraintSubPart(Constraint oldSubConstraint, Constraint newSubConstraint) {
        // no sub-constraints
    }

    // ── expression-level operations ───────────────────────────────────────────

    public List<Expression> getExpressionSubParts() {
        return Arrays.asList(left, right);
    }

    public void replaceExpressionSubPart(Expression oldSubExpression, Expression newSubExpression) {
        if (left == oldSubExpression) {
            left = newSubExpression;
        } else if (right == oldSubExpression) {
            right = newSubExpression;
        }
    }

    /**
     * Evaluates whether this constraint holds for the given set of selected features.
     * Returns {@code false} if either operand evaluates to NaN or infinity.
     */
    public boolean evaluate(Set<Feature> selectedFeatures) {
        double leftResult = left.evaluate(selectedFeatures);
        double rightResult = right.evaluate(selectedFeatures);
        if (Double.isNaN(leftResult) || Double.isNaN(rightResult)
                || Double.isInfinite(leftResult) || Double.isInfinite(rightResult)) {
            return false;
        }
        return compareValues(leftResult, rightResult);
    }

    @Override
    public int hashCode(int level) {
        final int prime = 31;
        int result = prime * level + (left == null ? 0 : left.hashCode());
        result = prime * result + (right == null ? 0 : right.hashCode());
        result = prime * result + operatorSymbol().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ExpressionConstraint other = (ExpressionConstraint) obj;
        return Objects.equals(operatorSymbol(), other.operatorSymbol())
                && Objects.equals(left, other.left)
                && Objects.equals(right, other.right);
    }

    @Override
    public List<VariableReference> getReferences() {
        List<VariableReference> references = new ArrayList<>();
        references.addAll(left.getReferences());
        references.addAll(right.getReferences());
        return references;
    }
}
