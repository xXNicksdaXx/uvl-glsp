package de.tu_dresden.inf.st.uvl.bp.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.bp.metamodel.model.building.AutomaticBrackets;
import de.tu_dresden.inf.st.uvl.bp.metamodel.model.building.VariableReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for strictly binary (two-operand) logical constraints, i.e.
 * {@link ImplicationConstraint} and {@link EquivalenceConstraint}.
 *
 * <p>Both classes were previously identical in structure and differed only in their
 * operator symbol. Extracting the shared logic here removes that duplication.</p>
 */
public abstract class BinaryConstraint extends Constraint {

    private Constraint left;
    private Constraint right;

    // ── constructors ─────────────────────────────────────────────────────────

    protected BinaryConstraint(Constraint left, Constraint right) {
        this.left = left;
        this.right = right;
    }

    // ── accessor API ─────────────────────────────────────────────────────────

    public Constraint getLeft() {
        return left;
    }

    public Constraint getRight() {
        return right;
    }

    public void setLeft(Constraint left) {
        this.left = left;
    }

    public void setRight(Constraint right) {
        this.right = right;
    }

    // ── operator symbol ─────────────────────────────────────

    /**
     * Returns the infix operator symbol used when printing,
     * e.g. {@code "=>"} or {@code "<=>"}.
     */
    protected abstract String operatorSymbol();

    // ── Constraint implementation ─────────────────────────────────────────────

    @Override
    public String toString(boolean withSubmodels, String currentAlias) {
        return AutomaticBrackets.enforceConstraintBracketsIfNecessary(this, left, withSubmodels, currentAlias)
                + " " + operatorSymbol() + " "
                + AutomaticBrackets.enforceConstraintBracketsIfNecessary(this, right, withSubmodels, currentAlias);
    }

    @Override
    public List<Constraint> getConstraintSubParts() {
        return Arrays.asList(left, right);
    }

    @Override
    public void replaceConstraintSubPart(Constraint oldSubConstraint, Constraint newSubConstraint) {
        if (left == oldSubConstraint) {
            left = newSubConstraint;
        } else if (right == oldSubConstraint) {
            right = newSubConstraint;
        }
    }

    @Override
    public int hashCode(int level) {
        final int prime = 31;
        int result = prime * level + (left == null ? 0 : left.hashCode(1 + level));
        result = prime * result + (right == null ? 0 : right.hashCode(1 + level));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BinaryConstraint other = (BinaryConstraint) obj;
        return Objects.equals(left, other.left) && Objects.equals(right, other.right);
    }

    @Override
    public List<VariableReference> getReferences() {
        List<VariableReference> references = new ArrayList<>();
        references.addAll(left.getReferences());
        references.addAll(right.getReferences());
        return references;
    }
}

