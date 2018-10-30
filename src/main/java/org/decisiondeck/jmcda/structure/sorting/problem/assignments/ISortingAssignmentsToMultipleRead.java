package org.decisiondeck.jmcda.structure.sorting.problem.assignments;

import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.problem.data.ISortingData;

public interface ISortingAssignmentsToMultipleRead extends ISortingData {
    public IOrderedAssignmentsToMultipleRead getAssignments();

    public boolean hasCompleteAssignments();

    @Override
    public CatsAndProfs getCatsAndProfs();

}
