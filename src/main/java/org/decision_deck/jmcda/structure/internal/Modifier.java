package org.decision_deck.jmcda.structure.internal;

public interface Modifier<T> {
    /**
     * @param target
     *            the object whose state is to be possibly modified. Not {@code null}.
     * @return {@code true} iff the target object changed as a result of this method call.
     */
    public boolean modify(T target);
}
