package org.decisiondeck.jmcda.structure.sorting.problem.group_results;

import java.util.Map;

import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesForwarder;
import org.decisiondeck.jmcda.structure.sorting.problem.results.ISortingResults;

public class GroupSortingResultsForwarder extends GroupSortingPreferencesForwarder implements IGroupSortingResults {

    public GroupSortingResultsForwarder(IGroupSortingResults delegate) {
	super(delegate);
    }

    @Override
    public Map<DecisionMaker, IOrderedAssignments> getAssignments() {
	return delegate().getAssignments();
    }

    @Override
    public IOrderedAssignments getAssignments(DecisionMaker dm) {
	return delegate().getAssignments(dm);
    }

    @Override
    public boolean hasCompleteAssignments() {
	return delegate().hasCompleteAssignments();
    }

    @Override
    public ISortingResults getResults(DecisionMaker dm) {
	return delegate().getResults(dm);
    }

    @Override
    protected IGroupSortingResults delegate() {
	return (IGroupSortingResults) super.delegate();
    }

}
