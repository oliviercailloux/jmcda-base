package org.decisiondeck.jmcda.structure.sorting.problem.results;

import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsToMultipleExtended;

/**
 * <p>
 * A read-only view. Restricting to a subset of alternatives is possible when the object is created.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class SortingResultsToMultipleComplete extends SortingResultsToMultipleForwarder implements
	ISortingResultsToMultiple {

    /**
     * @param delegate
     *            not {@code null}.
     */
    public SortingResultsToMultipleComplete(ISortingResultsToMultiple delegate) {
	super(delegate);
    }

    @Override
    public IOrderedAssignmentsToMultiple getAssignments() {
	return new OrderedAssignmentsToMultipleExtended(delegate().getAssignments(), getAlternatives());
    }

    @Override
    public boolean hasCompleteAssignments() {
	return (getAssignments().getAlternatives().size() == getAlternatives().size());
    }

}
