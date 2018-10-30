package org.decisiondeck.jmcda.structure.sorting.problem.results;

import org.decisiondeck.jmcda.structure.sorting.problem.assignments.ISortingAssignmentsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.problem.data.ISortingData;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.ISortingPreferences;

public interface ISortingResultsWithCredibilities extends ISortingData, ISortingPreferences,
	ISortingAssignmentsWithCredibilities {
    /** Nothing more. */
}
