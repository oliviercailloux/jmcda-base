package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;

/**
 * <p>
 * A read interface for a set of assignments where one assignment is a mapping of an alternative to exactly one
 * category.
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
 * An assignments object equals an other one iff they contain the same alternatives assigned to the same categories and
 * the same set of overall categories. The last condition is not redundant as the set of categories may be a superset of
 * the set of categories to which alternatives are assigned. This definition is compatible with the definition of the
 * assignments to multiple categories object.
 * </p>
 * TODO could also define a method that provides a simple map Map<Alt, Cat> and define the multiple case to extend
 * Map<Alt, Set<Cat>>?
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IAssignmentsRead extends IAssignmentsToMultipleRead {
    /**
     * @param alternative
     *            not <code>null</code>.
     * @return the category to which this alternative is assigned, or <code>null</code> iff this alternative is not
     *         assigned.
     */
    public Category getCategory(Alternative alternative);

    /**
     * Retrieves, if it exists, the category to which an alternative is assigned as a singleton set. This method
     * provides compatibility with the {@link IAssignmentsToMultipleRead} interface. The method
     * {@link #getCategory(Alternative)} is functionally equivalent and should be preferred over this one as it is
     * clearer.
     * 
     * @param alternative
     *            not <code>null</code>.
     * @return a set containing exactly one element, namely the category to which the given alternative is assigned, or
     *         <code>null</code> iff the alternative is not assigned.
     */
    @Override
    public Set<Category> getCategories(Alternative alternative);

}
