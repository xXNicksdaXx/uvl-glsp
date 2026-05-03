package de.tu_dresden.inf.st.uvl.metamodel.model.constraint.bp;

import de.tu_dresden.inf.st.uvl.metamodel.model.building.VariableReference;
import de.tu_dresden.inf.st.uvl.metamodel.model.constraint.Constraint;
import de.tu_dresden.inf.st.uvl.metamodel.util.ConstantSymbols;
import de.tu_dresden.inf.st.uvl.metamodel.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a BP event constraint of the form: {@code conflicting(<ref1>, <ref2>, ...)}.
 * Unlike the other single-reference BP event constraints, this one takes a variable
 * number of references.
 */
public class ConflictingConstraint extends Constraint {

    private final List<VariableReference> references;

    public ConflictingConstraint(List<VariableReference> references) {
        Objects.requireNonNull(references, "references must not be null");
        if (references.isEmpty()) {
            throw new IllegalArgumentException("ConflictingConstraint requires at least one reference");
        }
        this.references = new ArrayList<>(references);
    }

    public List<VariableReference> getReferenceList() {
        return Collections.unmodifiableList(references);
    }

    public void setReference(int index, VariableReference reference) {
        this.references.set(index, reference);
    }

    @Override
    public String toString(boolean withSubmodels, String currentAlias) {
        String refsString = references.stream()
                .map(ref -> Util.addNecessaryQuotes(ref.getIdentifier()))
                .collect(Collectors.joining(", "));
        return "conflicting" + ConstantSymbols.PAREN_OPEN
                + refsString
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
    public Constraint clone() {
        ConflictingConstraint clone = new ConflictingConstraint(new ArrayList<>(references));
        clone.setLineNumber(getLineNumber());
        return clone;
    }

    @Override
    public int hashCode(int level) {
        final int prime = 31;
        int result = prime * level + "conflicting".hashCode();
        for (VariableReference ref : references) {
            result = prime * result + (ref == null ? 0 : ref.hashCode());
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
        ConflictingConstraint other = (ConflictingConstraint) obj;
        return Objects.equals(references, other.references);
    }

    @Override
    public List<VariableReference> getReferences() {
        return Collections.unmodifiableList(references);
    }
}
