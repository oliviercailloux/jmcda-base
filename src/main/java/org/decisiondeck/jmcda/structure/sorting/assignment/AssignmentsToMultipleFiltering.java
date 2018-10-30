package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

/**
 * A read-only view of an assignments object. Can be restricted.
 * 
 * @author Olivier Cailloux
 * 
 */
public class AssignmentsToMultipleFiltering implements IAssignmentsToMultipleRead {

    private final Predicate<Alternative> m_restrictToAlternatives;
    private final IAssignmentsToMultipleRead m_delegate;

    /**
     * @param delegate
     *            not <code>null</code>.
     * @param filterAlternatives
     *            not <code>null</code>.
     */
    public AssignmentsToMultipleFiltering(IAssignmentsToMultipleRead delegate, Predicate<Alternative> filterAlternatives) {
	Preconditions.checkNotNull(delegate);
	Preconditions.checkNotNull(filterAlternatives);
	m_delegate = delegate;
	m_restrictToAlternatives = filterAlternatives;
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
    public Set<Category> getCategories(Alternative alternative) {
	if (!m_restrictToAlternatives.apply(alternative)) {
	    return null;
	}
	return m_delegate.getCategories(alternative);
    }

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof IAssignmentsToMultipleRead)) {
	    return false;
	}
	IAssignmentsToMultipleRead a2 = (IAssignmentsToMultipleRead) obj;
	return AssignmentsUtils.equivalentToMultiple(this, a2);
    }

    @Override
    public int hashCode() {
	return AssignmentsUtils.getEquivalenceRelationToMultiple().hash(this);
    }

    protected IAssignmentsToMultipleRead delegate() {
	return m_delegate;
    }

    @Override
    public Set<Category> getCategories() {
	return m_delegate.getCategories();
    }

}
