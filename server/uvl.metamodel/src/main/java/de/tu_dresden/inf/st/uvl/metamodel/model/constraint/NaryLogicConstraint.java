package de.tu_dresden.inf.st.uvl.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.metamodel.exception.ParseError;
import de.tu_dresden.inf.st.uvl.metamodel.model.building.AutomaticBrackets;
import de.tu_dresden.inf.st.uvl.metamodel.model.building.VariableReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Abstract base class for n-ary logical connective constraints ({@link AndConstraint} and
 * {@link OrConstraint}).
 *
 * <p>Both connectives share the same internal list-based representation and only
 * differ in the operator symbol used when printing. Extracting this behaviour here
 * eliminates the near-complete code duplication that existed between the two
 * concrete classes.</p>
 */
public abstract class NaryLogicConstraint extends Constraint {

    private final List<Constraint> children = new ArrayList<>();

    // ── constructors ─────────────────────────────────────────────────────────

    protected NaryLogicConstraint() {}

    protected NaryLogicConstraint(Constraint... constraints) {
        for (Constraint c : constraints) {
            if (c != null) {
                children.add(c);
            }
        }
    }

    protected NaryLogicConstraint(Constraint left, Constraint right) {
        children.add(left);
        children.add(right);
    }

    // ── accessor API ─────────────────────────────────────────────────────────

    /**
     * Returns the first (leftmost) child of this connective.
     *
     * @throws ParseError if there are no children
     */
    public Constraint getLeft() {
        if (children.isEmpty()) {
            throw new ParseError("Left child cannot be returned because there are no children.");
        }
        return children.getFirst();
    }

    /**
     * Returns the last (rightmost) child of this connective.
     *
     * @throws ParseError if there are fewer than two children
     */
    public Constraint getRight() {
        if (children.size() < 2) {
            throw new ParseError("Right child cannot be returned because there are fewer than two children.");
        }
        return children.getLast();
    }

    /** Returns the full list of children (mutable; not a defensive copy). */
    public List<Constraint> getChildren() {
        return children;
    }

    public void setLeft(Constraint left) {
        if (children.isEmpty()) {
            children.add(left);
        } else {
            children.set(0, left);
        }
    }

    public void setRight(Constraint right) {
        if (children.size() < 2) {
            if (children.isEmpty()) {
                children.add(null);
            }
            children.add(right);
        } else {
            children.set(children.size() - 1, right);
        }
    }

    public void addChild(Constraint constraint) {
        if (constraint != null) {
            children.add(constraint);
        }
    }

    // ── operator symbol ─────────────────────────────────────

    /**
     * Returns the infix operator symbol used when printing, e.g. {@code "&"} or {@code "|"}.
     */
    protected abstract String operatorSymbol();

    // ── Constraint implementation ─────────────────────────────────────────────

    @Override
    public String toString(boolean withSubmodels, String currentAlias) {
        return children.stream()
                .map(c -> AutomaticBrackets.enforceConstraintBracketsIfNecessary(
                        this, c, withSubmodels, currentAlias))
                .collect(Collectors.joining(" " + operatorSymbol() + " "));
    }

    @Override
    public List<Constraint> getConstraintSubParts() {
        return children;
    }

    @Override
    public void replaceConstraintSubPart(Constraint oldSubConstraint, Constraint newSubConstraint) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) == oldSubConstraint) {
                children.set(i, newSubConstraint);
            }
        }
    }

    @Override
    public int hashCode(int level) {
        final int prime = 31;
        int result = prime * level;
        for (Constraint c : children) {
            result = prime * result + (c == null ? 0 : c.hashCode(1 + level));
        }
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
        NaryLogicConstraint other = (NaryLogicConstraint) obj;
        return Objects.equals(children, other.children);
    }

    @Override
    public List<VariableReference> getReferences() {
        List<VariableReference> references = new ArrayList<>();
        for (Constraint c : children) {
            references.addAll(c.getReferences());
        }
        return references;
    }
}

