package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.NavigableSet;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

/**
 * A read-only view of an ordered assignments object. Can be restricted.
 * 
 * @author Olivier Cailloux
 * 
 */
public class OrderedAssignmentsFiltering implements IOrderedAssignmentsRead {

    private final Predicate<Alternative> m_restrictToAlternatives;
    private final IOrderedAssignmentsRead m_delegate;

    /**
     * @param delegate
     *            not {@code null}.
     * @param predicate
     *            not {@code null}.
     */
    public OrderedAssignmentsFiltering(IOrderedAssignmentsRead delegate, Predicate<Alternative> predicate) {
	Preconditions.checkNotNull(delegate);
	Preconditions.checkNotNull(predicate);
	m_delegate = delegate;
	m_restrictToAlternatives = predicate;
    }

    /**
     * Creates a read-only view of the given delegate.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public OrderedAssignmentsFiltering(IOrderedAssignmentsRead delegate) {
	Preconditions.checkNotNull(delegate);
	m_delegate = delegate;
	m_restrictToAlternatives = Predicates.<Alternative> alwaysTrue();
    }

    @Override
    public Set<Alternative> getAlternatives(Category category) {
	return Sets.filter(m_delegate.getAlternatives(category), m_restrictToAlternatives);
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return Sets.filter(m_delegate.getAlternatives(), m_restrictToAlternatives);
    }

    @Override
    public Category getCategory(Alternative alternative) {
	if (!m_restrictToAlternatives.apply(alternative)) {
	    return null;
	}
	return m_delegate.getCategory(alternative);
    }

    @Override
    public NavigableSet<Category> getCategories(Alternative alternative) {
	if (!m_restrictToAlternatives.apply(alternative)) {
	    return null;
	}
	return m_delegate.getCategories(alternative);
    }

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof IOrderedAssignmentsToMultipleRead)) {
	    return false;
	}
	IOrderedAssignmentsToMultipleRead a2 = (IOrderedAssignmentsToMultipleRead) obj;
	return AssignmentsUtils.equivalentOrderedToMultiple(this, a2);
    }

    @Override
    public int hashCode() {
	return AssignmentsUtils.getEquivalenceRelationOrderedToMultiple().hash(this);
    }

    protected IOrderedAssignmentsRead delegate() {
	return m_delegate;
    }

    @Override
    public NavigableSet<Category> getCategories() {
	return m_delegate.getCategories();
    }

}
