package org.decisiondeck.jmcda.structure.sorting.assignment;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;

/**
 * <p>
 * A set of assignments, where an assignment is a mapping of an alternative to exactly one category.
 * </p>
 * <p>
 * Also implements degenerate cases of multiple categories assignments (where multiple reduces to exactly one), see the
 * note about inheritance in {@link IAssignmentsRead}.
 * </p>
 * <p>
 * An assignments object equals an other one iff they contain the same alternatives assigned to the same categories and
 * the same set of overall categories. The last condition is not redundant as the set of categories may be a superset of
 * the set of categories to which alternatives are assigned. This definition is compatible with the definition of the
 * assignments to multiple categories object.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IAssignments extends IAssignmentsRead {
    /**
     * Sets, replaces, or removes the assignment of an alternative.
     * 
     * @param alternative
     *            not {@code null}.
     * @param category
     *            {@code null} to remove the assignment.
     * @return {@code true} iff the call changed the assignments, i.e. {@code true} iff the given alternative
     *         was assigned and the assignment has been removed, or was assigned to a different category, or was not
     *         assigned and has been assigned to a category.
     */
    public boolean setCategory(Alternative alternative, Category category);

    public boolean clear();
}
