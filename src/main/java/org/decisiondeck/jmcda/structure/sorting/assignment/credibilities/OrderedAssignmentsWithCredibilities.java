package org.decisiondeck.jmcda.structure.sorting.assignment.credibilities;

import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.VersatileOrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;

import com.google.common.base.Preconditions;

public class OrderedAssignmentsWithCredibilities implements IOrderedAssignmentsWithCredibilities {
    private final VersatileOrderedAssignments m_delegate;

    private OrderedAssignmentsWithCredibilities(VersatileOrderedAssignments delegate) {
	Preconditions.checkNotNull(delegate);
	m_delegate = delegate;
    }

    public OrderedAssignmentsWithCredibilities() {
	this(new VersatileOrderedAssignments());
    }

    /**
     * Copy-constructor by value. No reference is kept to the given object.
     * 
     * @param source
     *            not {@code null}.
     */
    public OrderedAssignmentsWithCredibilities(IOrderedAssignmentsWithCredibilitiesRead source) {
	this(new VersatileOrderedAssignments(source));
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
    public NavigableMap<Category, Double> getCredibilities(Alternative alternative) {
	return m_delegate.getCredibilitiesSorted(alternative);
    }

    @Override
    public boolean setCredibilities(Alternative alternative, Map<Category, Double> credibilities) {
	return m_delegate.setCredibilities(alternative, credibilities);
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

    @Override
    public String toString() {
	return AssignmentsUtils.getShortDescription(this);
    }

    @Override
    public NavigableSet<Category> getCategories(Alternative alternative) {
	return m_delegate.getCategoriesSorted(alternative);
    }
}