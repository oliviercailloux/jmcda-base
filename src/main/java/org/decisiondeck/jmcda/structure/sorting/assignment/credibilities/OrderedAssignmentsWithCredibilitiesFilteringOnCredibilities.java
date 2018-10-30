package org.decisiondeck.jmcda.structure.sorting.assignment.credibilities;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

/**
 * A read-only view of an ordered assignments object. Can be restricted. The predicate must be such that each
 * alternative is still assigned to at least one category. This object has the same set of alternatives as the delegate,
 * and the same set of categories, though some may have no alternatives inside.
 * 
 * @author Olivier Cailloux
 * 
 */
public class OrderedAssignmentsWithCredibilitiesFilteringOnCredibilities implements
	IOrderedAssignmentsWithCredibilitiesRead {

    private final IOrderedAssignmentsWithCredibilitiesRead m_delegate;
    private final Predicate<Double> m_predicate;

    /**
     * The predicate must be such that each alternative is still assigned to at least one category. This object has the
     * same set of assigned alternatives than the given delegate.
     * 
     * @param delegate
     *            not <code>null</code>.
     * @param predicate
     *            not <code>null</code>.
     */
    public OrderedAssignmentsWithCredibilitiesFilteringOnCredibilities(
	    IOrderedAssignmentsWithCredibilitiesRead delegate, Predicate<Double> predicate) {
	checkNotNull(delegate);
	checkNotNull(predicate);
	m_delegate = delegate;
	m_predicate = predicate;
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return m_delegate.getAlternatives();
    }

    @Override
    public NavigableMap<Category, Double> getCredibilities(Alternative alternative) {
	NavigableMap<Category, Double> source = m_delegate.getCredibilities(alternative);
	if (source == null) {
	    return null;
	}
	final SortedMap<Category, Double> remaining = Maps.filterValues(source, m_predicate);
	checkState(!remaining.isEmpty(), "Filtering " + source + ", remaining is empty.");
	return new TreeMap<Category, Double>(remaining);
    }

    @Override
    public NavigableSet<Category> getCategories() {
	return m_delegate.getCategories();
    }

    @Override
    public Set<Alternative> getAlternatives(Category category) {
	return m_delegate.getAlternatives(category);
    }

    @Override
    public NavigableSet<Category> getCategories(Alternative alternative) {
	final NavigableMap<Category, Double> credibilities = getCredibilities(alternative);
	return credibilities == null ? null : credibilities.navigableKeySet();
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
	return AssignmentsUtils.getEquivalenceRelationOrderedToMultiple().hash(this);
    }

    @Override
    public String toString() {
        return AssignmentsUtils.getShortDescription(this);
    }

}
