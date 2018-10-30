package org.decisiondeck.jmcda.structure.sorting.assignment.utils;

import org.decision_deck.utils.PredicateUtils;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilitiesRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.OrderedAssignmentsWithCredibilitiesFilteringOnCredibilities;

public class AssignmentsViewFactory {

    static public IOrderedAssignmentsWithCredibilitiesRead getAssignmentsGEQ(
	    IOrderedAssignmentsWithCredibilitiesRead delegate, double toKeep) {
	return new OrderedAssignmentsWithCredibilitiesFilteringOnCredibilities(delegate, PredicateUtils.atLeast(toKeep));
    }

    static public IOrderedAssignmentsWithCredibilitiesRead getAssignmentsLEQ(
	    IOrderedAssignmentsWithCredibilitiesRead delegate, double degree) {
	return new OrderedAssignmentsWithCredibilitiesFilteringOnCredibilities(delegate, PredicateUtils.atMost(degree));
    }

}
