package de.tu_dresden.inf.st.uvl.metamodel.model.constraint;

import de.tu_dresden.inf.st.uvl.metamodel.util.ConstantSymbols;

/**
 * Represents a logical NOT constraint: {@code !content} or {@code !(content)}
 *
 * <p>All structural behaviour (single child, hashCode, equals, getReferences,
 * replaceConstraintSubPart) is inherited from {@link UnaryConstraint}.</p>
 */
public class NotConstraint extends UnaryConstraint {

    public NotConstraint(Constraint content) {
        super(content);
    }

    @Override
    public String toString(boolean withSubmodels, String currentAlias) {
        Constraint content = getContent();
        StringBuilder result = new StringBuilder();
        result.append(ConstantSymbols.NOT);
        if (content instanceof LiteralConstraint || content instanceof ParenthesisConstraint) {
            result.append(content.toString(withSubmodels, currentAlias));
        } else {
            result.append(ConstantSymbols.PAREN_OPEN);
            result.append(content.toString(withSubmodels, currentAlias));
            result.append(ConstantSymbols.PAREN_CLOSE);
        }
        return result.toString();
    }

    @Override
    public Constraint clone() {
        return new NotConstraint(getContent().clone());
    }
}
