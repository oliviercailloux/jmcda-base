package org.decisiondeck.jmcda.structure.sorting.problem.group_results;

import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decisiondeck.jmcda.structure.sorting.problem.group_assignments.IGroupSortingAssignments;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.IGroupSortingData;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.IGroupSortingPreferences;
import org.decisiondeck.jmcda.structure.sorting.problem.results.ISortingResults;

/**
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IGroupSortingResults extends IGroupSortingData, IGroupSortingPreferences, IGroupSortingAssignments {

    /**
     * <p>
     * Retrieves a read-only view of the results for the given decision maker, that is, a sorting object containing the
     * data in this object, the alternatives that are assigned by this decision maker, and his preferences. Note that
     * the set of alternatives in the data may be larger than the set of alternatives assigned by the given decision
     * maker.
     * </p>
     * <p>
     * The returned object reads the data (criteria, alternatives etc.) in the order provided by this object.
     * </p>
     * 
     * @param dm
     *            not <code>null</code>.
     * @return <code>null</code> iff the given decision maker does not exist in this object.
     */
    public ISortingResults getResults(DecisionMaker dm);
}
