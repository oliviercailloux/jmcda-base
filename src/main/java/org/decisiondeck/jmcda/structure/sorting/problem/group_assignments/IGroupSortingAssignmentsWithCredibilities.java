package org.decisiondeck.jmcda.structure.sorting.problem.group_assignments;

import java.util.Map;

import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.IGroupSortingData;

/**
 * <p>
 * The objective data part of a sorting problem, a set of decision makers, and ordered assignments of alternatives to
 * categories for each decision makers. Each alternative may be assigned, according to a given decision maker, to a set
 * of categories, possibly a singleton, with an associated credibitily described by a number.
 * </p>
 * <p>
 * The categories orderings associated with the assignments added to this object must be compatible with the order of
 * the categories set in this object. The usual auto-add behavior (e.g. if an evaluation is added on a criterion that is
 * not in this object, that criterion is added as well) is disabled for the categories: adding an assignment requires
 * that the relevant categories be already set in this object. This is because the order of the categories cannot be
 * reliably guessed when adding the assignments one by one. E.g. alternative1 goes to category1, alternative2 goes to
 * category2, then a third assignment gives alternative3 going to category2 and category1 (in that order). If this
 * object had guessed, before the call, that the order was category1 then category2 (adding the categories one by one),
 * it now appears that this order is incorrect and the categories order would change without the user having asked
 * specifically for it, which may be confusing. Moreover, finding a compatible ordering might be complex.
 * </p>
 * <p>
 * Changing the categories order after having set some ordered assignments is permitted and will change, if necessary,
 * the already provided assignments orderings so that they remain compatible with the categories order, as required by
 * the contract of {@link org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilitiesRead}.
 * The user should first ensure that the orderings are compatible before changing it if she does not want this to
 * happen.
 * </p>
 * <p>
 * The set of categories associated to each assignments is a subset of the set returned by {@link #getCatsAndProfs()}. A
 * subset is allowed, instead of an equal set, so that adding a category to this object does not change the categories
 * associated to all the assignments. It also allows having different set of categories for different decision makers.
 * Recall that the categories to which alternatives are indeed assigned inside an assignments object is itself a subset
 * of the categories associated to the assignments, so there are three possibly different, but nested, sets of
 * categories involved.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IGroupSortingAssignmentsWithCredibilities extends IGroupSortingData {

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
    public Map<DecisionMaker, IOrderedAssignmentsWithCredibilities> getAssignments();

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
    public IOrderedAssignmentsWithCredibilities getAssignments(DecisionMaker dm);

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

    /**
     * @return <code>true</code> iff all alternatives contained in this object have been assigned by all the decision
     *         makers.
     */
    public boolean hasCompleteAssignments();

}
