package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.NavigableSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilitiesRead;

/**
 * <p>
 * A read-only interface for a set of assignments where one assignment is a mapping of an alternative to exactly one
 * category among a set of ordered categories.
 * </p>
 * <p>
 * Equivalent to {@link IAssignmentsRead} to use when the categories the alternatives are assigned to are provided with
 * a total ordering, thus can be sorted from worst to best without ex-æquo.
 * </p>
 * <p>
 * A set of such assignments can also be considered as a degenerate case of a set of assignments to multiple categories,
 * where each alternative is assigned to exactly one category (which is a legal assignment to ''multiple'' categories),
 * hence the inheritance relation. Objects implementing this interface however may only implement the degenerate case of
 * the multiple categories case: it is impossible to satisfy both the contract for this interface (ensuring that every
 * alternative is assigned to exactly one category) and the general case of the multiple categories interface (allowing
 * an alternative to be assigned to more than one category). Having the inheritance relation permits to use objects
 * implementing the single category case (this interface) for algorithms dealing with multiple categories assignments.
 * </p>
 * <p>
 * An ordered assignments object equals an other one iff they contain the same alternatives assigned to the same
 * categories and contain the same set of overall categories in the same order. This definition is compatible with the
 * definition of equality over ordered assignments to multiple categories objects.
 * </p>
 * <p>
 * This interface does not extend {@link IOrderedAssignmentsWithCredibilitiesRead} because it would involve automatic
 * guess of an arbitrary constant credibility degree, e.g. one. This would then forbids an algorithm to clearly specify
 * if it really uses credibility degrees as input or not. The user could think that an algorithm will not use the
 * credibility degrees whereas it will use them, therefore making use of credibility numbers which have no real meaning.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IOrderedAssignmentsRead extends IAssignmentsRead, IOrderedAssignmentsToMultipleRead {

    /**
     * Retrieves, if it exists, the category to which an alternative is assigned as a singleton set. This method
     * provides compatibility with the {@link IOrderedAssignmentsToMultipleRead} interface. The method
     * {@link #getCategory(Alternative)} is functionally equivalent and should be preferred over this one as it is
     * clearer.
     * 
     * @param alternative
     *            not <code>null</code>.
     * @return a set containing exactly one element, namely the category to which the given alternative is assigned, or
     *         <code>null</code> iff the alternative is not assigned. The returned set is immutable (if the assignment
     *         related to the given alternative later change, this change is not reflected to the object this method
     *         returns).
     */
    @Override
    public NavigableSet<Category> getCategories(Alternative alternative);
}
