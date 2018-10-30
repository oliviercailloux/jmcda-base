package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.SortedSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;

/**
 * <p>
 * Equivalent to {@link IAssignments} to use when the categories the alternatives are assigned to are provided with a
 * total ordering, thus can be sorted from worst to best without ex-æquo.
 * </p>
 * <p>
 * <em>Contrary</em> to {@link IAssignments} however, this object mandates that when setting an assignment of an
 * alternative to some category, these categories be pre-defined. This can be done using the
 * {@link #setCategories(SortedSet)} method and is necessary for this object to know the ordering on the categories, to
 * know how to interpret the assignments in terms of ordered categories and ensure that the method
 * {@link #getCategories()} returns a superset of the used categories. This interface does not extend
 * {@link IAssignments} because of this restriction it has on the categories to which an alternative may be assigned,
 * which must be predefined. The other interface does not have such a requirement.
 * </p>
 * <p>
 * An ordered assignments object equals an other one iff they contain the same alternatives assigned to the same
 * categories and contain the same set of overall categories in the same order. This definition is compatible with the
 * definition of equality over ordered assignments to multiple categories objects.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IOrderedAssignments extends IOrderedAssignmentsRead, IOrderedAssignmentsWriteable {
    /**
     * Sets, replaces, or removes the assignment of an alternative. The given category position in the ordering must
     * have been defined.
     * 
     * @param alternative
     *            not <code>null</code>.
     * @param category
     *            <code>null</code> or empty to assign the alternative to no category, i.e., to remove the assignment of
     *            the given alternative. Otherwise, must be contained in the categories returned by
     *            {@link #getCategories()}.
     * @return <code>true</code> iff the call changed the assignments, i.e. true if the given alternative was assigned
     *         and the assignment has been removed, or was assigned to a different category, or was not assigned and has
     *         been.
     */
    public boolean setCategory(Alternative alternative, Category category);

}
