package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;

public class Assignments implements IAssignments {
    private final VersatileAssignments m_delegate;

    public Assignments() {
	m_delegate = new VersatileAssignments();
    }

    @Override
    public Set<Category> getCategories() {
	return m_delegate.getCategories();
    }

    @Override
    public Set<Alternative> getAlternatives(Category category) {
	return m_delegate.getAlternatives(category);
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return m_delegate.getAlternatives();
    }

    @Override
    public Category getCategory(Alternative alternative) {
	return m_delegate.getCategory(alternative);
    }

    protected VersatileAssignments delegate() {
	return m_delegate;
    }

    @Override
    public boolean clear() {
	return m_delegate.clear();
    }

    @Override
    public boolean setCategory(Alternative alternative, Category category) {
	return m_delegate.setCategory(alternative, category);
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

    @Override
    public Set<Category> getCategories(Alternative alternative) {
	return m_delegate.getCategories(alternative);
    }
}