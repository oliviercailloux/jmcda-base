package org.decisiondeck.jmcda.structure.sorting.problem.group_results;

import java.util.Map;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.utils.collection.SetBackedMap;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.OrderedAssignmentsWithCredibilitiesFiltering;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesForwarder;
import org.decisiondeck.jmcda.structure.sorting.problem.results.ISortingResultsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.problem.results.SortingResultsWithCredibilitiesViewGroupBacked;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * <p>
 * A read-only view. Restricting to a subset of alternatives is possible when the object is created. The filtering only
 * (possibly) impacts the alternatives. Profiles, proviles evaluations, etc., are untouched.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class GroupSortingResultsWithCredibilitiesFiltering extends GroupSortingPreferencesForwarder implements
	IGroupSortingResultsWithCredibilities {

    private final IGroupSortingResultsWithCredibilities m_delegate;

    /**
     * Creates a read-only view of the given data.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public GroupSortingResultsWithCredibilitiesFiltering(IGroupSortingResultsWithCredibilities delegate) {
	this(delegate, Predicates.<Alternative> alwaysTrue());
    }

    /**
     * Creates a view that only view the alternatives admitted by the given filter, or equivalently, that filters out
     * the alternatives that do not pass the given filter. This only concerns the real alternatives, the profiles are
     * untouched. To filter a constant set of alternatives, use {@link Predicates#in(java.util.Collection)}.
     * 
     * @param delegate
     *            not {@code null}.
     * @param filterAlternatives
     *            {@code null} to allow everything (equivalent to {@link Predicates#alwaysTrue()}.
     */
    public GroupSortingResultsWithCredibilitiesFiltering(IGroupSortingResultsWithCredibilities delegate,
	    Predicate<Alternative> filterAlternatives) {
	super(new GroupSortingPreferencesFiltering(delegate, filterAlternatives));
	m_delegate = delegate;
    }

    @Override
    protected GroupSortingPreferencesFiltering delegate() {
	return (GroupSortingPreferencesFiltering) super.delegate();
    }

    @Override
    public Map<DecisionMaker, IOrderedAssignmentsWithCredibilities> getAssignments() {
	return SetBackedMap.create(super.getDms(), new Function<DecisionMaker, IOrderedAssignmentsWithCredibilities>() {
	    @Override
	    public IOrderedAssignmentsWithCredibilities apply(DecisionMaker input) {
		return getAssignments(input);
	    }
	});
    }

    @Override
    public IOrderedAssignmentsWithCredibilities getAssignments(DecisionMaker dm) {
	final Predicate<Alternative> alternativesFilter = delegate().getAlternativesFilter();
	if (alternativesFilter == null) {
	    return AssignmentsUtils.getFakeWriteableWithCredibilities(new OrderedAssignmentsWithCredibilitiesFiltering(
		    m_delegate.getAssignments(dm)));
	}
	return AssignmentsUtils.getFakeWriteableWithCredibilities(new OrderedAssignmentsWithCredibilitiesFiltering(
		m_delegate.getAssignments(dm), alternativesFilter));
    }

    @Override
    public boolean hasCompleteAssignments() {
	for (DecisionMaker dm : getDms()) {
	    final IOrderedAssignmentsWithCredibilities assignments = getAssignments(dm);
	    if (assignments.getAlternatives().size() < getAlternatives().size()) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public ISortingResultsWithCredibilities getResults(DecisionMaker dm) {
	Preconditions.checkNotNull(dm);
	if (!getDms().contains(dm)) {
	    return null;
	}
	return new SortingResultsWithCredibilitiesViewGroupBacked(this, dm);
    }

    public Predicate<Alternative> getAlternativesFilter() {
	return delegate().getAlternativesFilter();
    }

}
