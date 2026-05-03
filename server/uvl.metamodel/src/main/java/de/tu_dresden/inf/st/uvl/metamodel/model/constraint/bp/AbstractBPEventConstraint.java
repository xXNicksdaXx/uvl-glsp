package de.tu_dresden.inf.st.uvl.metamodel.model.constraint.bp;

import de.tu_dresden.inf.st.uvl.metamodel.model.building.VariableReference;
import de.tu_dresden.inf.st.uvl.metamodel.model.constraint.Constraint;
import de.tu_dresden.inf.st.uvl.metamodel.util.ConstantSymbols;
import de.tu_dresden.inf.st.uvl.metamodel.util.Util;

import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for single-reference BP event constraints
 * (requested, blocked, waited_for, selected).
 * Each takes a single {@link VariableReference} as its operand.
 */
public abstract class AbstractBPEventConstraint extends Constraint {

    private VariableReference reference;
    private final String keyword;

    protected AbstractBPEventConstraint(VariableReference reference, String keyword) {
        this.reference = Objects.requireNonNull(reference, "reference must not be null");
        this.keyword = Objects.requireNonNull(keyword, "keyword must not be null");
    }

    public VariableReference getReference() {
        return reference;
    }

    public void setReference(VariableReference reference) {
        this.reference = reference;
    }

    public String getKeyword() {
        return keyword;
    }

    @Override
    public String toString(boolean withSubmodels, String currentAlias) {
        return keyword + ConstantSymbols.PAREN_OPEN
                + Util.addNecessaryQuotes(reference.getIdentifier())
                + ConstantSymbols.PAREN_CLOSE;
    }

    @Override
    public List<Constraint> getConstraintSubParts() {
        return List.of();
    }

    @Override
    public void replaceConstraintSubPart(Constraint oldSubConstraint, Constraint newSubConstraint) {
        // no sub-constraints
    }

    @Override
    public int hashCode(int level) {
        final int prime = 31;
        int result = prime * level + keyword.hashCode();
        result = prime * result + (reference == null ? 0 : reference.hashCode());
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
        AbstractBPEventConstraint other = (AbstractBPEventConstraint) obj;
        return Objects.equals(keyword, other.keyword) && Objects.equals(reference, other.reference);
    }

    @Override
    public List<VariableReference> getReferences() {
        return List.of(reference);
    }
}
