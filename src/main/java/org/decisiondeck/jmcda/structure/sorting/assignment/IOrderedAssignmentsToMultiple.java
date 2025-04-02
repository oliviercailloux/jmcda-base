package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.Set;
import java.util.SortedSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;

/**
 * <p>
 * Equivalent to {@link IAssignmentsToMultiple} to use when the categories the alternatives are assigned to are provided
 * with a total ordering, thus can be sorted from worst to best without ex-æquo.
 * </p>
 * <p>
 * <em>Contrary</em> to {@link IAssignmentsToMultiple} however, this object mandates that when setting an assignment of
 * an alternative to some categories, these categories be pre-defined. This can be done using the
 * {@link #setCategories(SortedSet)} method and is necessary for this object to know the ordering on the categories, to
 * know how to interpret the assignments in terms of ordered categories. This interface does not extend
 * {@link IAssignmentsToMultiple} because of this restriction it has on the categories to which an alternative may be
 * assigned, which must be predefined. The other interface does not have such a requirement.
 * </p>
 * <p>
 * An ordered assignments to multiple categories object equals an other one iff they contain the same alternatives
 * assigned to the same categories and contain the same set of overall categories in the same order.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IOrderedAssignmentsToMultiple extends IOrderedAssignmentsToMultipleRead, IOrderedAssignmentsWriteable {
    /**
     * Sets, replaces, or removes the assignment of an alternative. The given categories ordering must have been
     * defined.
     * 
     * @param alternative
     *            not {@code null}.
     * @param categories
     *            {@code null} or empty to assign the alternative to no category, i.e., to remove the assignment of
     *            the given alternative. Otherwise, must be a subset of the categories returned by
     *            {@link #getCategories()}.
     * @return {@code true} iff the call changed the assignments, i.e. true if the given alternative was assigned
     *         and the assignment has been removed, or was assigned to a not identical set of categories, or was not
     *         assigned and has been.
     */
    public boolean setCategories(Alternative alternative, Set<Category> categories);

}
