package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.SortedSet;

import org.decision_deck.jmcda.structure.sorting.category.Category;

/**
 * <p>
 * A set of assignments that can be cleared and associated with an ordered set of categories. This is the base interface
 * of the writeable interfaces defining the ordered assignment objects.
 * </p>
 * <p>
 * An assignments to multiple categories object equals an other one iff they contain the same alternatives assigned to
 * the same categories and the same set of overall categories. The last condition is not redundant as the set of
 * categories may be a superset of the set of categories to which alternatives are assigned.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IOrderedAssignmentsWriteable extends IOrderedAssignmentsToMultipleRead {
    /**
     * <p>
     * Sets the categories and the order of the categories in this object.
     * </p>
     * <p>
     * Caution should be used when using this method on a non empty object. The simple case is when the given categories
     * ordering is compatible with the ordering already used. If on the countrary the given categories order is not
     * compatible with the already given ordered assignments, the assignments will be <em>changed</em> to reflect the
     * new order. The user should check whether the order is compatible before using this method if she does not want
     * this to happen.
     * </p>
     * 
     * @param categories
     *            {@code null} to remove the associated categories (all the orderings are lost), authorized only
     *            when no assignments are contained in this object. Must be a superset of the categories already used.
     * @return {@code true} iff the categories changed.
     */
    public boolean setCategories(SortedSet<Category> categories);

    public boolean clear();
}
