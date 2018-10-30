package org.decisiondeck.jmcda.structure.sorting.problem.group_assignments;

import java.util.Map;

import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.IGroupSortingData;

public interface IGroupSortingAssignmentsToMultipleRead extends IGroupSortingData {
    public Map<DecisionMaker, ? extends IOrderedAssignmentsToMultipleRead> getAssignments();

    public IOrderedAssignmentsToMultipleRead getAssignments(DecisionMaker dm);

    /**
     * @return <code>true</code> iff all alternatives contained in this object have been assigned by all the decision
     *         makers.
     */
    public boolean hasCompleteAssignments();

    @Override
    public CatsAndProfs getCatsAndProfs();

}
