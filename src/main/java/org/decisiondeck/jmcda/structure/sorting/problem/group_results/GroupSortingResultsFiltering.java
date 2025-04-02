package org.decisiondeck.jmcda.structure.sorting.problem.group_results;

import java.util.Map;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.utils.collection.SetBackedMap;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsFiltering;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesForwarder;
import org.decisiondeck.jmcda.structure.sorting.problem.results.ISortingResults;
import org.decisiondeck.jmcda.structure.sorting.problem.results.SortingResultsViewGroupBacked;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * <p>
 * A read-only view. Restricting to a subset of alternatives is possible when the object is created. The filtering only
 * (possibly) impacts the alternatives. Profiles, profiles evaluations, etc., are untouched.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class GroupSortingResultsFiltering extends GroupSortingPreferencesForwarder implements IGroupSortingResults {

    private final IGroupSortingResults m_delegate;

    /**
     * Creates a read-only view of the given data.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public GroupSortingResultsFiltering(IGroupSortingResults delegate) {
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
    public GroupSortingResultsFiltering(IGroupSortingResults delegate, Predicate<Alternative> filterAlternatives) {
	super(new GroupSortingPreferencesFiltering(delegate, filterAlternatives));
	m_delegate = delegate;
    }

    @Override
    protected GroupSortingPreferencesFiltering delegate() {
	return (GroupSortingPreferencesFiltering) super.delegate();
    }

    @Override
    public Map<DecisionMaker, IOrderedAssignments> getAssignments() {
	return new SetBackedMap<DecisionMaker, IOrderedAssignments>(super.getDms(),
		new Function<DecisionMaker, IOrderedAssignments>() {
		    @Override
		    public IOrderedAssignments apply(DecisionMaker input) {
			return getAssignments(input);
		    }
		});
    }

    @Override
    public IOrderedAssignments getAssignments(DecisionMaker dm) {
	final Predicate<Alternative> alternativesFilter = delegate().getAlternativesFilter();
	if (alternativesFilter == null) {
	    return AssignmentsUtils.getFakeWritable(new OrderedAssignmentsFiltering(m_delegate.getAssignments(dm)));
	}
	return AssignmentsUtils.getFakeWritable(new OrderedAssignmentsFiltering(m_delegate.getAssignments(dm),
		alternativesFilter));
    }

    @Override
    public boolean hasCompleteAssignments() {
	for (DecisionMaker dm : getDms()) {
	    final IOrderedAssignments assignments = getAssignments(dm);
	    if (assignments.getAlternatives().size() < getAlternatives().size()) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public ISortingResults getResults(DecisionMaker dm) {
	Preconditions.checkNotNull(dm);
	if (!getDms().contains(dm)) {
	    return null;
	}
	return new SortingResultsViewGroupBacked(this, dm);
    }

    /**
     * @return {@code null} for everything allowed.
     */
    public Predicate<Alternative> getAlternativesFilter() {
	return delegate().getAlternativesFilter();
    }

}
