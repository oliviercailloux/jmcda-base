package org.decision_deck.jmcda.structure.internal;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;

public class NavigableSetBackedOrderedIterator<T> implements Iterator<T> {
    private T m_last;
    private final NavigableSet<T> m_navigable;

    public NavigableSetBackedOrderedIterator(final NavigableSet<T> navigable) {
	if (navigable == null) {
	    throw new NullPointerException();
	}
	m_navigable = navigable;
	m_last = null;
    }

    @Override
    public boolean hasNext() {
	if (m_last == null) {
	    return !m_navigable.isEmpty();
	}
	return !m_navigable.tailSet(m_last, false).isEmpty();
    }

    public boolean hasPrevious() {
	if (m_last == null) {
	    return false;
	}
	return !m_navigable.headSet(m_last).isEmpty();
    }

    @Override
    public T next() {
	if (m_last == null) {
	    m_last = m_navigable.first();
	} else {
	    m_last = m_navigable.tailSet(m_last, false).first();
	}
	return m_last;
    }

    public T previous() {
	if (m_last == null) {
	    throw new NoSuchElementException("Iterator is at beginning.");
	}
	m_last = m_navigable.headSet(m_last).last();
	return m_last;
    }

    @Override
    public void remove() {
	if (m_last == null) {
	    throw new IllegalStateException("Next never called.");
	}
	final boolean removed = m_navigable.remove(m_last);
	if (!removed) {
	    throw new IllegalStateException("No current value (already removed).");
	}
    }
}
