package de.tu_dresden.inf.st.uvl.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.metamodel.model.building.VariableReference;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for single-operand (unary) constraint wrappers, i.e.
 * {@link NotConstraint} and {@link ParenthesisConstraint}.
 *
 * <p>Both classes wrap exactly one child {@link Constraint} and share the same
 * structural contract. Extracting the common logic here removes duplication.</p>
 */
public abstract class UnaryConstraint extends Constraint {

    private Constraint content;

    // ── constructor ───────────────────────────────────────────────────────────

    protected UnaryConstraint(Constraint content) {
        this.content = content;
    }

    // ── accessor API ─────────────────────────────────────────────────────────

    public Constraint getContent() {
        return content;
    }

    public void setContent(Constraint content) {
        this.content = content;
    }

    // ── Constraint implementation ─────────────────────────────────────────────

    @Override
    public List<Constraint> getConstraintSubParts() {
        return Collections.singletonList(content);
    }

    @Override
    public void replaceConstraintSubPart(Constraint oldSubConstraint, Constraint newSubConstraint) {
        if (content == oldSubConstraint) {
            content = newSubConstraint;
        }
    }

    @Override
    public int hashCode(int level) {
        return 31 * level + (content == null ? 0 : content.hashCode(1 + level));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UnaryConstraint other = (UnaryConstraint) obj;
        return Objects.equals(content, other.content);
    }

    @Override
    public List<VariableReference> getReferences() {
        return content.getReferences();
    }
}

