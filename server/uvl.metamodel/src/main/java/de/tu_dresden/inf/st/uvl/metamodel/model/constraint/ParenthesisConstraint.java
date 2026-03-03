package de.tu_dresden.inf.st.uvl.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.metamodel.util.ConstantSymbols;

/**
 * Represents a parenthesised constraint: {@code (content)}
 *
 * <p>All structural behaviour (single child, hashCode, equals, getReferences,
 * replaceConstraintSubPart) is inherited from {@link UnaryConstraint}.</p>
 */
public class ParenthesisConstraint extends UnaryConstraint {

    public ParenthesisConstraint(Constraint content) {
        super(content);
    }

    @Override
    public String toString(boolean withSubmodels, String currentAlias) {
        return ConstantSymbols.PAREN_OPEN
                + getContent().toString(withSubmodels, currentAlias)
                + ConstantSymbols.PAREN_CLOSE;
    }

    @Override
    public Constraint clone() {
        return new ParenthesisConstraint(getContent().clone());
    }
}
