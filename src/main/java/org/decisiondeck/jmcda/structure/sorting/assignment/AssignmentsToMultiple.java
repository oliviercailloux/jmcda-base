package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;

public class AssignmentsToMultiple implements IAssignmentsToMultiple {
    private final VersatileAssignments m_delegate;

    public AssignmentsToMultiple() {
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
    public Set<Category> getCategories(Alternative alternative) {
	return m_delegate.getCategories(alternative);
    }

    protected VersatileAssignments delegate() {
	return m_delegate;
    }

    @Override
    public boolean clear() {
	return m_delegate.clear();
    }

    @Override
    public boolean setCategories(Alternative alternative, Set<Category> categories) {
	return m_delegate.setCategories(alternative, categories);
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
    public String toString() {
        return AssignmentsUtils.getShortDescription(this);
    }
}