package org.decisiondeck.jmcda.structure.sorting.problem.group_results;

import java.util.Map;

import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesForwarder;
import org.decisiondeck.jmcda.structure.sorting.problem.results.ISortingResultsWithCredibilities;

public class GroupSortingResultsWithCredibilitiesForwarder extends GroupSortingPreferencesForwarder implements
	IGroupSortingResultsWithCredibilities {

    public GroupSortingResultsWithCredibilitiesForwarder(IGroupSortingResultsWithCredibilities delegate) {
	super(delegate);
    }

    @Override
    public Map<DecisionMaker, IOrderedAssignmentsWithCredibilities> getAssignments() {
	return delegate().getAssignments();
    }

    @Override
    public IOrderedAssignmentsWithCredibilities getAssignments(DecisionMaker dm) {
	return delegate().getAssignments(dm);
    }

    @Override
    public boolean hasCompleteAssignments() {
	return delegate().hasCompleteAssignments();
    }

    @Override
    protected IGroupSortingResultsWithCredibilities delegate() {
	return (IGroupSortingResultsWithCredibilities) super.delegate();
    }

    @Override
    public ISortingResultsWithCredibilities getResults(DecisionMaker dm) {
	return delegate().getResults(dm);
    }

}
