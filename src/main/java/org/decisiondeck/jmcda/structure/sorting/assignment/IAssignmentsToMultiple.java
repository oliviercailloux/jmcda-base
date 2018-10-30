package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;

/**
 * <p>
 * Objects of this type contain a set of assignments, where an assignment is a mapping of an alternative to a set of
 * categories, i.e., to at least one category.
 * </p>
 * <p>
 * An assignments to multiple categories object equals an other one iff they contain the same alternatives assigned to
 * the same categories and the same set of overall categories. The last condition is not redundant as the set of
 * categories may be a superset of the set of categories to which alternatives are assigned.
 * </p>
 * <p>
 * Note that this object does not implement {@link IAssignmentsRead} because it can't guarantee that an alternative is
 * assigned to exactly one category, which is a required property for the crisp case.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IAssignmentsToMultiple extends IAssignmentsToMultipleRead {
    /**
     * Sets, replaces, or removes the assignment of an alternative.
     * 
     * @param alternative
     *            not <code>null</code>.
     * @param categories
     *            <code>null</code> or empty to assign the alternative to no category, i.e., to remove the assignment of
     *            the given alternative.
     * @return <code>true</code> iff the call changed the assignments, i.e. true if the given alternative was assigned
     *         and the assignment has been removed, or was assigned to a not identical set of categories, or was not
     *         assigned and has been.
     */
    public boolean setCategories(Alternative alternative, Set<Category> categories);

    public boolean clear();
}
