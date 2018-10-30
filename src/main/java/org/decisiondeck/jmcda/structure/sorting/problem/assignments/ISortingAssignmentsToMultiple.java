package org.decisiondeck.jmcda.structure.sorting.problem.assignments;

import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultiple;

/**
 * <p>
 * The objective data part of a sorting problem together with ordered assignments of alternatives to categories. Each
 * alternative is assigned to a set of categories.
 * </p>
 * <p>
 * The set of categories associated to each assignments is a subset of the set returned by {@link #getCatsAndProfs()}. A
 * subset is allowed, instead of an equal set, so that adding a category to this object does not change the categories
 * associated to all the assignments. Recall that the categories to which alternatives are indeed assigned inside an
 * assignments object is itself a subset of the categories associated to the assignments, so there are three possibly
 * different, but nested, sets of categories involved.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface ISortingAssignmentsToMultiple extends ISortingAssignmentsToMultipleRead {
    /**
     * <p>
     * Retrieves a writeable view of the assignments of alternatives to categories.
     * </p>
     * <p>
     * The set of assigned alternatives is a subset of the alternatives given by {@link #getAlternatives()}. This
     * condition implies that no profiles may be assigned (as no profiles may be contained in the set returned by the
     * latter method).
     * </p>
     * <p>
     * The set of categories associated to each assignments is a subset of the set returned by
     * {@link #getCatsAndProfs()}.
     * </p>
     * <p>
     * When writing to the returned assignments, only categories contained in this object (at the time of the writing,
     * not at the time this method returns) may be used. Setting the categories bound to the returned assignments is
     * legal only with categories that are a subset of the categories in this object <em>and</em> that are in a
     * compatible order, i.e. the order of the assignments must not contradict the order of the categories in this
     * object. Changing the categories used in the returned assignments object does not change the categories in this
     * object: no categories are added or removed through setting the returned object. Adding an assignment to the
     * returned object adds the alternative to this object so that the set of alternatives in this object is still a
     * superset of the assigned alternatives.
     * </p>
     * 
     * @return not <code>null</code>.
     */
    @Override
    public IOrderedAssignmentsToMultiple getAssignments();

    /**
     * @return <code>true</code> iff all alternatives contained in this object are assigned.
     */
    @Override
    public boolean hasCompleteAssignments();

    /**
     * <p>
     * Retrieves a writeable view of the categories and profiles in this object. The profiles associated with the
     * returned categories (if defined) constitute a subset of the profiles given by {@link #getProfiles()}. Adding a
     * profile to the returned object adds it to this object as well. Removing a profile from the returned object does
     * not remove the profile from this object.
     * </p>
     * <p>
     * Removing a category from this object through the returned object removes all the assignments to that category and
     * removes the category, where it is present, from the set bound to the assignment objects (see
     * {@link #getAssignments()} and {@link IOrderedAssignmentsToMultiple#getCategories()}). Similarily, renaming a
     * category through the returned object renames the category for the assignments if they use that category. Adding a
     * category through the returned object does <em>not</em> add the category to the assignments, as these may use a
     * subset of the categories defined in this object.
     * </p>
     * 
     * @return not <code>null</code>.
     */
    @Override
    public CatsAndProfs getCatsAndProfs();

}
