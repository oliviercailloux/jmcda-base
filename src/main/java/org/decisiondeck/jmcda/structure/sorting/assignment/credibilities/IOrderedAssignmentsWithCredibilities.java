package org.decisiondeck.jmcda.structure.sorting.assignment.credibilities;

import java.util.Map;
import java.util.SortedSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultiple;

import com.google.common.collect.ImmutableMap;

/**
 * <p>
 * A set of assignments where one assignment is a mapping of an alternative to a set of ordered categories, with
 * associated credibilities, meaning that each category to which an alternative is mapped has an associated degree of
 * credibility (a real number greater than zero) indicating how strong the alternative belongs to that category. An
 * alternative is said to be assigned if it is assigned to at least one category (i.e. the set of categories to which it
 * is mapped may not be empty).
 * </p>
 * <p>
 * This interface is an equivalent to {@link IAssignmentsWithCredibilities}, to use when the categories the alternatives
 * are assigned to are provided with a total ordering, thus can be sorted (the categories) from worst to best without
 * ex-æquo.
 * </p>
 * <p>
 * <em>Contrary</em> to {@link IAssignmentsWithCredibilities} however, this object mandates that when setting an
 * assignment of an alternative to some categories, these categories be pre-defined. This can be done using the
 * {@link #setCategories(SortedSet)} method and is necessary for this object to know the ordering on the categories, to
 * know how to interpret the assignments in terms of ordered categories. This interface does not extend
 * {@link IAssignmentsWithCredibilities} because of this very restriction it has on the categories to which an
 * alternative may be assigned, which must be predefined. The other interface does not have such a requirement.
 * </p>
 * <p>
 * This interface does <em>not</em> extend {@link IOrderedAssignmentsToMultiple} because that would require it to guess
 * some credibilities when an alternative is simply affected to a set of categories. See
 * {@link IOrderedAssignmentsToMultiple} for more informations.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IOrderedAssignmentsWithCredibilities extends IOrderedAssignmentsWithCredibilitiesRead {
    /**
     * <p>
     * Sets, replaces, or removes the assignment of an alternative and the associated degrees of credibility. The used
     * ordering is the one given by {@link #getCategories()}. A zero value as a degree of credibility is considered as
     * equivalent to a missing entry: it is interpreted as meaning that the given alternative is not assigned to the
     * corresponding category.
     * </p>
     * <p>
     * The method {@link ImmutableMap#of} may be used to create a map of credibilities from a limited set of values.
     * </p>
     * 
     * @param alternative
     *            not <code>null</code>.
     * @param credibilities
     *            <code>null</code> or empty to assign the alternative to no category, i.e., to remove the assignment of
     *            the given alternative. The map entries may not contain a <code>null</code> key or value, the values
     *            must be positive or zero, the categories must be contained in {@link #getCategories()}. If the map
     *            contains only zeroes, it is considered empty.
     * @return <code>true</code> iff the call changed the assignments, i.e., iff the assignment existed and has been
     *         removed, or existed and has changed (be it a change in some credibility degrees or a change of category),
     *         or did not exist and has been added.
     */
    public boolean setCredibilities(Alternative alternative, Map<Category, Double> credibilities);

    public boolean clear();

    /**
     * <p>
     * Sets the categories and the order of the categories in this object.
     * </p>
     * <p>
     * Caution should be execised when using this method on a non empty object. The simple case is when the given
     * categories ordering is compatible with the ordering already used. If on the countrary the given categories order
     * is not compatible with the already given ordered assignments, the assignments will be <em>changed</em> to reflect
     * the new order. The user should check whether the order is compatible before using this method if she does not
     * want this to happen.
     * </p>
     * 
     * @param categories
     *            <code>null</code> to remove the associated categories (all the orderings are lost), authorized only
     *            when no assignments are contained in this object. Must be a superset of the categories already used.
     * @return <code>true</code> iff the categories changed.
     */
    public boolean setCategories(SortedSet<Category> categories);
}
