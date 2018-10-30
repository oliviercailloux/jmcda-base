package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;

public class OrderedAssignments implements IOrderedAssignments {

    @Override
    public String toString() {
	return AssignmentsUtils.getShortDescription(this);
    }

    private final VersatileOrderedAssignments m_assignments;

    public OrderedAssignments() {
	m_assignments = new VersatileOrderedAssignments();
    }

    @Override
    public Category getCategory(Alternative alternative) {
	return m_assignments.getCategory(alternative);
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return m_assignments.getAlternatives();
    }

    @Override
    public NavigableSet<Category> getCategories() {
	return m_assignments.getCategoriesSorted();
    }

    @Override
    public NavigableSet<Category> getCategories(Alternative alternative) {
	return m_assignments.getCategoriesSorted(alternative);
    }

    @Override
    public boolean setCategories(SortedSet<Category> categories) {
	return m_assignments.setCategories(categories);
    }

    @Override
    public boolean setCategory(Alternative alternative, Category category) {
	return m_assignments.setCategory(alternative, category);
    }

    @Override
    public Set<Alternative> getAlternatives(Category category) {
	return m_assignments.getAlternatives(category);
    }

    @Override
    public boolean clear() {
	return m_assignments.clear();
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

}
