package org.decisiondeck.jmcda.structure.sorting.assignment.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.SortedSet;

import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.exc.InvalidInputException;
import org.decisiondeck.jmcda.structure.sorting.assignment.Assignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.AssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.assignment.IAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.IAssignmentsRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.IAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.assignment.IAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.AssignmentsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IAssignmentsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilitiesRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.OrderedAssignmentsWithCredibilities;

import com.google.common.base.Preconditions;

public class AssignmentsFactory {
    static public IOrderedAssignmentsWithCredibilities newOrderedAssignmentsWithCredibilities() {
	return new OrderedAssignmentsWithCredibilities();
    }

    static public IOrderedAssignments newOrderedAssignments() {
	return new OrderedAssignments();
    }

    static public IAssignments newAssignments() {
	return new Assignments();
    }

    static public IOrderedAssignmentsWithCredibilities newOrderedAssignmentsWithCredibilities(
	    IOrderedAssignmentsWithCredibilitiesRead source) {
	Preconditions.checkNotNull(source);
	return new OrderedAssignmentsWithCredibilities(source);
    }

    static public IOrderedAssignmentsToMultiple newOrderedAssignmentsToMultiple() {
	return new OrderedAssignmentsToMultiple();
    }

    static public IAssignmentsToMultiple newAssignmentsToMultiple() {
	return new AssignmentsToMultiple();
    }

    static public IAssignmentsWithCredibilities newAssignmentsWithCredibilities() {
	return new AssignmentsWithCredibilities();
    }

    /**
     * <p>
     * The source must have each alternative assigned to only one category.
     * </p>
     * <p>
     * Retrieves a new assignments object of type {@link IAssignments}, thus guaranteeing that each alternative is
     * assigned to exactly one category, containing the same assignments than those in the given source.
     * </p>
     * 
     * @param source
     *            not <code>null</code>.
     * @return not <code>null</code>.
     * @throws InvalidInputException
     *             if the source contains at least one alternative assigned to more than one category.
     */
    static public IAssignments newAssignmentsFromMultiple(IAssignmentsToMultipleRead source)
	    throws InvalidInputException {
	checkNotNull(source);
	final IAssignments target = AssignmentsFactory.newAssignments();
	AssignmentsUtils.copyAssignmentsToMultipleToTargetSingle(source, target);
	return target;
    }

    static public IOrderedAssignments newOrderedAssignments(IOrderedAssignmentsRead source) {
	checkNotNull(source);
	final IOrderedAssignments target = AssignmentsFactory.newOrderedAssignments();
	AssignmentsUtils.copyOrderedAssignmentsToTarget(source, target);
	return target;
    }

    static public IOrderedAssignmentsToMultiple newOrderedAssignmentsToMultiple(IOrderedAssignmentsToMultipleRead source) {
	Preconditions.checkNotNull(source);
	return new OrderedAssignmentsToMultiple(source);
    }

    public static IAssignments newAssignments(IAssignmentsRead source) {
	checkNotNull(source);
	final IAssignments target = AssignmentsFactory.newAssignments();
	AssignmentsUtils.copyAssignmentsToTarget(source, target);
	return target;
    }

    /**
     * <p>
     * The source must have each alternative assigned to only one category.
     * </p>
     * <p>
     * Retrieves a new assignments object of type {@link IOrderedAssignments}, thus guaranteeing that each alternative
     * is assigned to exactly one category, containing the same assignments than those in the given source.
     * </p>
     * 
     * @param source
     *            not <code>null</code>.
     * @return not <code>null</code>.
     * @throws InvalidInputException
     *             if the source contains at least one alternative assigned to more than one category.
     */
    static public IOrderedAssignments newOrderedAssignmentsFromMultiple(IOrderedAssignmentsToMultipleRead source)
	    throws InvalidInputException {
	checkNotNull(source);
	final IOrderedAssignments target = AssignmentsFactory.newOrderedAssignments();
	AssignmentsUtils.copyOrderedAssignmentsToMultipleToTargetSingle(source, target);
	return target;
    }

    /**
     * <p>
     * Creates a new ordered assignments object that contain all the given source assignments. The given set of
     * categories must contain all the categories used in the source assignments.
     * </p>
     * <p>
     * If the given set of categories is not a superset of the set of categories used in the source, an exception is
     * thrown.
     * </p>
     * 
     * @param source
     *            not <code>null</code>.
     * @param categories
     *            not <code>null</code>.
     * @return not <code>null</code>.
     * @throws InvalidInputException
     *             iff at least one alternative is assigned in source to a category not contained in the given
     *             categories.
     */
    static public IOrderedAssignmentsToMultiple newOrderedAssignmentsToMultiple(IAssignmentsToMultipleRead source,
	    SortedSet<Category> categories) throws InvalidInputException {
	Preconditions.checkNotNull(source);
	Preconditions.checkNotNull(categories);
	final IOrderedAssignmentsToMultiple target = newOrderedAssignmentsToMultiple();
	target.setCategories(categories);
	AssignmentsUtils.copyAssignmentsToMultipleToOrderedTarget(source, target);
	return target;
    }

    /**
     * <p>
     * Creates a new assignments object that contain all the given source assignments.
     * </p>
     * 
     * @param source
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public IAssignmentsToMultiple newAssignmentsToMultiple(IAssignmentsToMultipleRead source) {
	checkNotNull(source);
	final IAssignmentsToMultiple target = newAssignmentsToMultiple();
	AssignmentsUtils.copyAssignmentsToMultipleToTarget(source, target);
	return target;
    }
}
