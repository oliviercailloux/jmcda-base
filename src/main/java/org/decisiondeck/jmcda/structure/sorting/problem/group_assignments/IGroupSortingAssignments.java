package org.decisiondeck.jmcda.structure.sorting.problem.group_assignments;

import java.util.Map;

import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.IGroupSortingData;

/**
 * <p>
 * The objective data part of a sorting problem, a set of decision makers, and ordered assignments of alternatives to
 * categories for each decision makers. Each alternative is assigned, according to a given decision maker, to at most
 * one category.
 * </p>
 * <p>
 * The set of categories associated to each assignments object is a subset of the set returned by
 * {@link #getCatsAndProfs()}. A subset is allowed, instead of an equal set, so that adding a category to this object
 * does not change the categories associated to all the assignments. It also allows having different set of categories
 * for different decision makers. Recall that the categories to which alternatives are effectively assigned inside an
 * assignments object is itself a subset of the categories associated to the assignments, so there are three possibly
 * different, but nested, sets of categories involved.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IGroupSortingAssignments extends IGroupSortingData, IGroupSortingAssignmentsToMultipleRead {
    /**
     * <p>
     * Retrieves the assignments of alternatives to categories associated to each decision maker. The returned map is a
     * read-only view, but the values are writeable.
     * </p>
     * <p>
     * The set of decision makers equals the set returned by {@link #getDms()}.
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
     * 
     * @return not <code>null</code>. No <code>null</code> key, no <code>null</code> values.
     */
    @Override
    public Map<DecisionMaker, IOrderedAssignments> getAssignments();

    /**
     * <p>
     * Retrieves a writeable view of the assignments associated to the given decision maker.
     * </p>
     * <p>
     * The set of assigned alternatives is a subset of the alternatives given by {@link #getAlternatives()}. This
     * condition implies that no profiles may be assigned (as no profiles may be contained in the set returned by the
     * latter method).
     * </p>
     * <p>
     * The set of categories associated to the returned assignments is a subset of the set returned by
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
     * @param dm
     *            not <code>null</code>.
     * @return <code>null</code> iff the given decision maker is not in the set of decision makers returned by
     *         {@link #getDms()}.
     */
    @Override
    public IOrderedAssignments getAssignments(DecisionMaker dm);

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
     * {@link #getAssignments(DecisionMaker)} and {@link IOrderedAssignments#getCategories()}). Similarily, renaming a
     * category through the returned object renames the category for each decision maker assignments that use that
     * category. Adding a category through the returned object does <em>not</em> add the category to the decision makers
     * assignments, as these may use a subset of categories that this object uses.
     * </p>
     * 
     * @return not <code>null</code>.
     */
    @Override
    public CatsAndProfs getCatsAndProfs();

}
