package org.decisiondeck.jmcda.structure.sorting.problem.results;

import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.SortingPreferencesForwarder;

import com.google.common.base.Preconditions;

public class SortingResultsToMultipleForwarder extends SortingPreferencesForwarder implements ISortingResultsToMultiple {

    public SortingResultsToMultipleForwarder(ISortingResultsToMultiple delegate) {
	super(delegate);
	Preconditions.checkNotNull(delegate);
    }

    @Override
    protected ISortingResultsToMultiple delegate() {
	return (ISortingResultsToMultiple) super.delegate();
    }

    @Override
    public IOrderedAssignmentsToMultiple getAssignments() {
	return delegate().getAssignments();
    }

    @Override
    public boolean hasCompleteAssignments() {
	return delegate().hasCompleteAssignments();
    }

}
