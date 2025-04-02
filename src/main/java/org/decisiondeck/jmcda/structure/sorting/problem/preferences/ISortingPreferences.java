package org.decisiondeck.jmcda.structure.sorting.problem.preferences;

import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.thresholds.Thresholds;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decisiondeck.jmcda.structure.sorting.problem.data.ISortingData;

/**
 * <p>
 * Preference parameters for a preference model of the type of <span style="font-variant: small-caps;">Electre
 * Tri</span>, supplementary to the data of a sorting problem.
 * </p>
 * <p>
 * This object is consistent iff all the following holds.
 * </p>
 * <ul>
 * <li>The data is consistent, i.e.:
 * <ul>
 * <li>All the alternatives given by {@link #getAlternatives()} are evaluated on all the criteria given by
 * {@link #getCriteria()}. Thus the evaluation is complete.</li>
 * <li>The categories are all linked to up and down profiles, except the worst and best one having no down and up
 * (respectively) profiles. At least one category is defined.</li>
 * <li>The set of profiles corresponding to the up or down profile of a category equals (independently of the order) the
 * set of profiles given by {@link #getProfiles()}.</li>
 * <li>All criteria have a preference direction set.</li>
 * </ul>
 * </li>
 * <li>Either the profiles evaluations are empty, or all profiles (as given by {@link #getProfiles()}) are evaluated on
 * all criteria (as given by {@link #getCriteria()}).</li>
 * <li>If the profiles evaluations are set, they must be ordered correctly, i.e. for each criterion g and pair of
 * profile p1, p2 such that p1 is worst than p2 (i.e. is associated with a category worst than...), the evaluation e1 on
 * p1, g must be not better than the evaluation e2 on p2, g, worst being defined according to the scale of the criterion
 * g. The profiles evaluations also must be non identical. These two conditions are equivalent to the condition that the
 * profiles, when ordered by dominance, must be in the same order than the one given by the categories order.</li>
 * <li>Either all coalitions are empty, or the coalitions are fully set (weights on all the criteria and majority
 * threshold).</li>
 * <li>If the coalitions are set, the sum of the weights must equal one plus or minus
 * {@link #SUM_OF_WEIGHTS_DEFAULT_TOLERANCE}, and the majority threshold must be between 0.5d and 1d (inclusive, but
 * without tolerance).</li>
 * <li>Either all preference thresholds are empty, or the preference thresholds are set on every criteria.</li>
 * <li>Either all indifference thresholds are empty, or the indifference thresholds are set on every criteria.</li>
 * </ul>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface ISortingPreferences extends ISortingData {



    /**
     * Retrieves a read-only view of the winning coalitions. The set of criteria over which the coalitions are defined
     * is a subset of the criteria returned by {@link #getCriteria()}.
     * 
     * @return not {@code null}.
     */
    public Coalitions getCoalitions();

    /**
     * Retrieves a read-only view of the evaluations of the profiles. The set of criteria on which evaluations are
     * provided is a subset of the set returned by {@link #getCriteria()}.
     * 
     * @return not {@code null}.
     */
    public EvaluationsRead getProfilesEvaluations();

    /**
     * Retrieves a read-only view of the thresholds.
     * 
     * @return not {@code null}.
     */
    public Thresholds getThresholds();

    /**
     * Retrieves the weight of the given criterion. The set of criteria over which the weights are defined is a subset
     * of the criteria returned by {@link #getCriteria()}. These weights are the same as those returned by
     * {@link #getCoalitions()}.
     * 
     * @param criterion
     *            not {@code null}.
     * @return {@code null} iff the criterion, is not in the set of criteria returned by {@link #getCriteria()} or
     *         if no weight has been defined on the given criterion. Otherwise, a positive or zero value.
     */
    public Double getWeight(Criterion criterion);

    /**
     * <p>
     * Sets, replaces, or removes the coalitions informations. If coalitions informations already exist, the given ones
     * are added. Any given weight replaces any existing weight set for the same criterion. Set<Criterion> for which a
     * weight has been defined are added in this object if not already present.
     * </p>
     * <p>
     * Note that setting an empty coalitions object has no effect.
     * </p>
     * 
     * @param coalitions
     *            the information to set. Any criterion having a weight defined and not existing in this object will be
     *            added to this object. {@code null} to remove the coalitions.
     * @return {@code true} iff the call changed the state of this object.
     */
    public boolean setCoalitions(Coalitions coalitions);

    /**
     * Sets, replaces, or removes the evaluation of the given alternative according to the given criterion.
     * 
     * @param alternative
     *            not {@code null}.
     * @param criterion
     *            not {@code null}.
     * @param value
     *            {@code null} to remove a possibly previously associated value.
     * @return {@code true} iff this call changed the data contained in this object.
     */
    // public boolean setProfilesEvaluation(Alternative alternative, Criterion criterion, Double value);

    /**
     * Sets replaces, or remove the evaluations of the profiles. The given information is added to any possibly existing
     * information. The set of profiles and criteria on which the given evaluations are defined are added, if not
     * already existing, to this object.
     * 
     * @param evaluations
     *            if {@code null}, the possibly existing evaluations will be removed.
     * @return {@code true} iff this call changed this object.
     */
    public boolean setProfilesEvaluations(EvaluationsRead evaluations);

    public boolean setThresholds(Thresholds thresholds);

}
