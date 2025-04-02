package org.decisiondeck.jmcda.structure.sorting.assignment;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;

public class OrderedAssignmentsToMultiple implements IOrderedAssignmentsToMultiple {
    private final VersatileOrderedAssignments m_delegate;

    public OrderedAssignmentsToMultiple() {
	this(new VersatileOrderedAssignments());
    }

    /**
     * Copy-constructor by value. No reference is kept to the given object.
     * 
     * @param source
     *            not {@code null}.
     */
    public OrderedAssignmentsToMultiple(IOrderedAssignmentsToMultipleRead source) {
	this(new VersatileOrderedAssignments(source));
    }

    private OrderedAssignmentsToMultiple(VersatileOrderedAssignments delegate) {
	checkNotNull(delegate);
	m_delegate = delegate;
    }

    @Override
    public NavigableSet<Category> getCategories() {
	return m_delegate.getCategoriesSorted();
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
    public NavigableSet<Category> getCategories(Alternative alternative) {
	return m_delegate.getCategoriesSorted(alternative);
    }

    @Override
    public boolean setCategories(SortedSet<Category> categories) {
	return m_delegate.setCategories(categories);
    }

    protected VersatileOrderedAssignments delegate() {
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

    @Override
    public String toString() {
	return AssignmentsUtils.getShortDescription(this);
    }
}