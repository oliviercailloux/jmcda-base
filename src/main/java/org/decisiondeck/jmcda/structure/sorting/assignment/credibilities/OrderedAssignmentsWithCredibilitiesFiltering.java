package org.decisiondeck.jmcda.structure.sorting.assignment.credibilities;

import java.util.NavigableMap;
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
public class OrderedAssignmentsWithCredibilitiesFiltering implements IOrderedAssignmentsWithCredibilitiesRead {

    private final Predicate<Alternative> m_restrictToAlternatives;
    private final IOrderedAssignmentsWithCredibilities m_delegate;

    /**
     * @param delegate
     *            not <code>null</code>.
     * @param filterAlternatives
     *            not <code>null</code>.
     */
    public OrderedAssignmentsWithCredibilitiesFiltering(IOrderedAssignmentsWithCredibilities delegate,
	    Predicate<Alternative> filterAlternatives) {
	Preconditions.checkNotNull(delegate);
	Preconditions.checkNotNull(filterAlternatives);
	m_delegate = delegate;
	m_restrictToAlternatives = filterAlternatives;
    }

    /**
     * Creates a new read-only view of the given delegate.
     * 
     * @param delegate
     *            not <code>null</code>.
     */
    public OrderedAssignmentsWithCredibilitiesFiltering(IOrderedAssignmentsWithCredibilities delegate) {
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
    public NavigableMap<Category, Double> getCredibilities(Alternative alternative) {
	if (!m_restrictToAlternatives.apply(alternative)) {
	    return null;
	}
	return m_delegate.getCredibilities(alternative);
    }

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof IOrderedAssignmentsWithCredibilitiesRead)) {
	    return false;
	}
	IOrderedAssignmentsWithCredibilitiesRead a2 = (IOrderedAssignmentsWithCredibilitiesRead) obj;
	return AssignmentsUtils.equivalentOrderedWithCredibilities(this, a2);
    }

    @Override
    public int hashCode() {
	return AssignmentsUtils.getEquivalenceRelationOrderedWithCredibilities().hash(this);
    }

    protected IOrderedAssignmentsWithCredibilities delegate() {
	return m_delegate;
    }

    @Override
    public NavigableSet<Category> getCategories(Alternative alternative) {
	if (!m_restrictToAlternatives.apply(alternative)) {
	    return null;
	}
	return m_delegate.getCategories(alternative);
    }

    @Override
    public NavigableSet<Category> getCategories() {
	return m_delegate.getCategories();
    }

    @Override
    public String toString() {
        return AssignmentsUtils.getShortDescription(this);
    }

}
