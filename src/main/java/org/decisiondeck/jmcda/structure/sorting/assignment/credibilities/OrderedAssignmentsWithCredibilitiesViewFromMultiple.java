package org.decisiondeck.jmcda.structure.sorting.assignment.credibilities;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decision_deck.utils.collection.extensional_order.ExtensionalComparator;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;

public class OrderedAssignmentsWithCredibilitiesViewFromMultiple implements IOrderedAssignmentsWithCredibilitiesRead {

    private final IOrderedAssignmentsToMultipleRead m_delegate;
    private final double m_credibilityValue;

    /**
     * @param delegate
     *            not <code>null</code>.
     * @param credibilityValue
     *            greater than zero. The value to use as credibility.
     */
    public OrderedAssignmentsWithCredibilitiesViewFromMultiple(IOrderedAssignmentsToMultipleRead delegate,
	    double credibilityValue) {
	if (delegate == null) {
	    throw new NullPointerException();
	}
	m_delegate = delegate;
	m_credibilityValue = credibilityValue;
    }

    @Override
    public NavigableSet<Category> getCategories() {
	return m_delegate.getCategories();
    }

    @Override
    public NavigableMap<Category, Double> getCredibilities(Alternative alternative) {
	final NavigableSet<Category> categories = m_delegate.getCategories(alternative);
	final ExtensionalComparator<Category> comparator = ExtensionalComparator.create(categories);
	final TreeMap<Category, Double> treeMap = new TreeMap<Category, Double>(comparator);
	for (Category category : categories) {
	    treeMap.put(category, Double.valueOf(m_credibilityValue));
	}
	return treeMap;
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return m_delegate.getAlternatives();
    }

    @Override
    public Set<Alternative> getAlternatives(Category category) {
	return m_delegate.getAlternatives(category);
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
	return m_delegate.getCategories(alternative);
    }

}
