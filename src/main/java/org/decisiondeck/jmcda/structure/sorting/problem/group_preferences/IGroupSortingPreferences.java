package org.decisiondeck.jmcda.structure.sorting.problem.group_preferences;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.thresholds.Thresholds;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.IGroupSortingData;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.ISortingPreferences;

/**
 * <p>
 * Setting a preferential parameter for a decision maker (even an empty one such as a {@link Thresholds} containing no
 * thresholds) adds that decision maker to the set of decision makers if it was not already present. Setting a
 * preferential parameter for a decision maker to <code>null</code> removes the associated preference information, but
 * does not remove the decision maker (because of the no auto-remove behavior, see {@link IGroupSortingData}).
 * </p>
 * <p>
 * When a preferential parameter has not been set for a given decision maker, but that decision maker is in the set
 * returned by {@link #getDms()} (e.g. because an other preferential parameter has been set for that decision maker),
 * implementing objects may not return <code>null</code>. Obviously an empty object can be returned (e.g., a
 * {@link Thresholds} object containing no thresholds), but other possibilities exist, e.g. an implementing object may
 * choose to provide default values for some parameters.
 * </p>
 * <p>
 * This object is consistent iff all the following holds.
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
 * <li>This object contains at least one decision maker.</li>
 * <li>Either the profiles evaluations are empty for every decision maker, or all profiles (as given by
 * {@link #getProfiles()}) are evaluated on all criteria (as given by {@link #getCriteria()}).</li>
 * <li>If the profiles evaluations are set, they must be ordered correctly, i.e. for each criterion g and pair of
 * profile p1, p2 such that p1 is worst than p2 (i.e. is associated with a category worst than...), the evaluation e1 on
 * p1, g must be not better than the evaluation e2 on p2, g, worst being defined according to the scale of the criterion
 * g. The profiles evaluations also must be non identical. These two conditions are equivalent to the condition that the
 * profiles, when ordered by dominance, must be in the same order than the one given by the categories order.</li>
 * <li>Either all coalitions are empty for everybody, or the coalitions are fully set (weights on all the criteria and
 * majority threshold) for every decision maker.</li>
 * <li>If the coalitions are set, the sum of the weights must equal one plus or minus
 * {@link #SUM_OF_WEIGHTS_DEFAULT_TOLERANCE}, and the majority threshold must be between 0.5d and 1d (inclusive, but
 * without tolerance).</li>
 * <li>Either all preference thresholds are empty for everybody, or the preference thresholds are set on every criteria
 * for every decision maker.</li>
 * <li>Either all indifference thresholds are empty for everybody, or the indifference thresholds are set on every
 * criteria for every decision maker.</li>
 * <li>Note that it could be required that if the profiles evaluations are set, the categories are exactly ordered
 * according to the profiles dominance relation, but that would be restrictive as not every sorting method might demand
 * that.</li>
 * </ul>
 * </p>
 * <p>
 * The coalition objects this object returns all have a tolerance (used when computing the sum of the weights when
 * normalizing) that reflect this object's tolerance (see {@link #SUM_OF_WEIGHTS_DEFAULT_TOLERANCE}). When setting this
 * object from an other coalition object, the given coalition object's tolerance is ignored and this object's tolerance
 * is used instead.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IGroupSortingPreferences extends IGroupSortingData {


    /**
     * <p>
     * Retrieves a writeable view of the decision makers.
     * </p>
     * <p>
     * When a decision maker is removed from the given set, all informations bound to the given decision maker in this
     * object is removed as well. If shared information exists and the set is not empty after the removal, the shared
     * information will be kept. If shared information exists and the removed decision maker is the last one, the shared
     * information will be kept iff it has been specified to be kept when the last decision maker is removed (see e.g.
     * {@link #setKeepSharedCoalitions(boolean)}).
     * </p>
     * <p>
     * When a decision maker is added to the given set, if shared information existed, it is not shared any more
     * (because the new decision maker comes with empty associated informations).
     * </p>
     * 
     * @return not <code>null</code>.
     */
    @Override
    public Set<DecisionMaker> getDms();

    /**
     * Retrieves a read-only view of the winning coalitions. The set of decision makers equals the set returned by
     * {@link #getDms()}. The set of criteria over which the coalitions are defined is a subset of the criteria returned
     * by {@link #getCriteria()}.
     * 
     * @return not <code>null</code>. No <code>null</code> key, no <code>null</code> values.
     */
    public Map<DecisionMaker, Coalitions> getCoalitions();

    /**
     * Retrieves a writeable view of all preferences related to a given decision maker (together with the data in this
     * object). If that decision maker is later removed from this object, the view contents becomes empty. The returned
     * object reads the data (criteria, alternatives etc.) in the order provided by this object.
     * 
     * @param dm
     *            not <code>null</code>.
     * @return <code>null</code> iff the given decision maker does not exist in this object.
     */
    public ISortingPreferences getPreferences(DecisionMaker dm);

    /**
     * <p>
     * Retrieves a read-only view of the winning coalitions of the given decision maker. The set of criteria over which
     * the coalitions are defined is a subset of the criteria returned by {@link #getCriteria()}.
     * </p>
     * 
     * @param dm
     *            not <code>null</code>.
     * @return <code>null</code> iff the given decision maker is not in the set of decision makers returned by
     *         {@link #getDms()}.
     */
    public Coalitions getCoalitions(DecisionMaker dm);

    /**
     * Retrieves the weight of the given criterion bound to the given decision maker. The set of criteria over which the
     * weights are defined is a subset of the criteria returned by {@link #getCriteria()}. These weights are the same as
     * those returned by {@link #getCoalitions()}.
     * 
     * @param dm
     *            not <code>null</code>.
     * @param criterion
     *            not <code>null</code>.
     * @return <code>null</code> iff the given decision maker, or criterion, is not in the set of decision makers, or
     *         criteria, returned by {@link #getDms()}, or {@link #getCriteria()}, or if the weight on that criterion
     *         for this decision maker has not been defined. Otherwise, a positive or zero value.
     */
    public Double getWeight(DecisionMaker dm, Criterion criterion);

    public boolean setThresholds(DecisionMaker dm, Thresholds thresholds);

    /**
     * Retrieves a read-only view of the thresholds. The set of decision makers equals the set returned by
     * {@link #getDms()}.
     * 
     * @return not <code>null</code>, no <code>null</code> key, no <code>null</code> values.
     */
    public Map<DecisionMaker, Thresholds> getThresholds();

    /**
     * Retrieves a read-only view of the thresholds of the given decision maker.
     * 
     * @param dm
     *            not <code>null</code>.
     * @return <code>null</code> iff the given decision maker is not in the set of decision makers returned by
     *         {@link #getDms()}.
     */
    public Thresholds getThresholds(DecisionMaker dm);

    /**
     * Retrieves a read-only view of the evaluations of the profiles. The set of criteria on which evaluations are
     * provided is a subset of the set returned by {@link #getCriteria()}. The set of decision makers equals the set
     * returned by {@link #getDms()}.
     * 
     * @return not <code>null</code>, no <code>null</code> key, no <code>null</code> values.
     */
    public Map<DecisionMaker, EvaluationsRead> getProfilesEvaluations();



    /**
     * Retrieves a read-only view of the evaluations of the profiles of the given decision maker. The set of criteria on
     * which evaluations are provided is a subset of the set returned by {@link #getCriteria()}.
     * 
     * @param dm
     *            not <code>null</code>.
     * @return <code>null</code> iff the given decision maker is not in the set of decision makers returned by
     *         {@link #getDms()}.
     */
    public EvaluationsRead getProfilesEvaluations(DecisionMaker dm);

    /**
     * <p>
     * Retrieves a read-only view of the evaluations of the profiles iff they are shared by every decision makers. If
     * not, the view is empty. The set of criteria on which evaluations are provided is a subset of the set returned by
     * {@link #getCriteria()}. Note that a view which is initially non empty, because this object contains identical
     * profiles evaluations for every decision makers, may become empty, e.g. because different profile evaluations have
     * been set for a new decision maker. When a decision maker has no associated profiles evaluations and others have,
     * the profiles evaluations are <em>not</em> considered shared.
     * </p>
     * <p>
     * When this view is non empty, the {@link #getProfilesEvaluations()} method returns the same evaluations for every
     * decision makers (meaning: same contents, not necessarily the same object).
     * </p>
     * 
     * @return not <code>null</code>.
     */
    public EvaluationsRead getSharedProfilesEvaluations();

    public void setKeepSharedThresholds(boolean keepShared);

    public void setKeepSharedCoalitions(boolean keepShared);

    /**
     * Sets the behavior to adopt when profiles evaluations are set and the decision makers set changed. When the last
     * DM is removed, should these informations be removed together with the last DM or should the preferential
     * information be kept? When a DM is added, should the shared informations be assigned to him or should he be
     * considered as being added with empty preferential informations?
     * 
     * @param keepShared
     *            <code>true</code> to keep the shared informations even when no DMs exist any more and to assign the
     *            shared informations to any newly added DM. If <code>false</code>, the default behavior, removing the
     *            last DM and adding a DM removes the shared information.
     */
    public void setKeepSharedProfilesEvaluations(boolean keepShared);

    /**
     * <p>
     * Sets, replaces, or removes the shared profiles evaluations. When they are set it applies to every decision makers
     * in this set. Possibly existing non shared profiles evaluations for any given decision maker are removed and
     * replaced by the given ones.
     * </p>
     * <p>
     * May also be used when there is no decision makers in this object. This means that no decision maker does not
     * necessarily imply empty shared profiles.
     * </p>
     * 
     * @param profilesEvaluations
     *            not <code>null</code>. If empty, every profiles evaluations are removed.
     * @return <code>true</code> iff the call changed the state of this object.
     */
    public boolean setSharedProfilesEvaluations(EvaluationsRead profilesEvaluations);

    /**
     * <p>
     * Sets, replaces, or removes the coalitions informations bound to the given decision maker. If coalitions
     * informations already exist for the given decision maker, the given ones are added. Any given weight replaces any
     * existing weight set for the same criterion. Set<Criterion> for which a weight has been defined are added in this
     * object if not already present.
     * </p>
     * <p>
     * Note that setting an empty coalitions object for an existing decision maker has no effect.
     * </p>
     * 
     * @param dm
     *            not <code>null</code>. If the given coalitions are <code>null</code>, must be in this object.
     * @param coalitions
     *            the information to set. Any criterion having a weight defined and not existing in this object will be
     *            added to this object. <code>null</code> to remove the coalitions.
     * @return <code>true</code> iff the call changed the state of this object.
     */
    public boolean setCoalitions(DecisionMaker dm, Coalitions coalitions);

    /**
     * Sets, replaces, or removes the evaluation of the given alternative according to the given criterion.
     * 
     * @param dm
     *            not <code>null</code>. Must exist if the given value is <code>null</code>, otherwise, is added to this
     *            object if does not already exist.
     * @param alternative
     *            not <code>null</code>. Must exist if the given value is <code>null</code>, otherwise, is added to this
     *            object if does not already exist.
     * @param criterion
     *            not <code>null</code>. Must exist if the given value is <code>null</code>, otherwise, is added to this
     *            object if does not already exist.
     * @param value
     *            <code>null</code> to remove a possibly previously associated value.
     * @return <code>true</code> iff this call changed the data contained in this object.
     */
    public boolean setProfilesEvaluation(DecisionMaker dm, Alternative alternative, Criterion criterion, Double value);

    /**
     * Sets replaces, or remove the evaluations of the profiles bound to the given decision maker. The given information
     * is added to any possibly existing information for the same decision maker. The set of alternatives and criteria
     * on which the given evaluations are defined are added, if not already existing, to this object.
     * 
     * @param dm
     *            not <code>null</code>. Must exist if the given evaluations are <code>null</code>, otherwise, is added
     *            to this object if not already exist.
     * @param evaluations
     *            if <code>null</code>, the evaluations possibly associated with the given decision maker will be
     *            removed.
     * @return <code>true</code> iff this call changed this object.
     */
    public boolean setProfilesEvaluations(DecisionMaker dm, EvaluationsRead evaluations);



    /**
     * <p>
     * Sets, replaces, or removes the shared thresholds. When they are set it applies to every decision makers in this
     * set. Possibly existing non shared thresholds for any given decision maker are removed and replaced by the given
     * ones.
     * </p>
     * <p>
     * May also be used when there is no decision makers in this object. This means that no decision maker does not
     * necessarily imply empty thresholds.
     * </p>
     * 
     * @param thresholds
     *            not <code>null</code>. If empty, every thresholds are removed.
     * @return <code>true</code> iff the call changed the state of this object.
     */
    public boolean setSharedThresholds(Thresholds thresholds);

    /**
     * <p>
     * Retrieves a read-only view of the coalitions shared by all decision makers. If they are not shared, the view is
     * empty. The set of criteria on which coalitions are provided is a subset of the set returned by
     * {@link #getCriteria()}. Note that a view which is initially non empty, because this object contains identical
     * coalitions for every decision makers, may become empty, e.g. because different coalitions have been set for a new
     * decision maker. When a decision maker has no associated coalitions and others have, the coalitions are
     * <em>not</em> considered shared.
     * </p>
     * <p>
     * When this view is non empty, the {@link #getCoalitions()} method returns the same coalitions for every decision
     * makers (meaning: same contents, not necessarily the same object).
     * </p>
     * 
     * @return not <code>null</code>.
     */
    public Coalitions getSharedCoalitions();

    /**
     * Retrieves a writeable view of all shared preferences (together with the data in this object). When some
     * preferential informations are not shared, the related view contents are empty. The returned object reads the data
     * (criteria, alternatives etc.) in the order provided by this object.
     * 
     * @return not <code>null</code>.
     */
    public ISortingPreferences getSharedPreferences();

    /**
     * <p>
     * Sets, replaces, or removes the shared coalitions. When they are set it applies to every decision makers in this
     * set. Possibly existing non shared coalitions for any given decision maker are removed and replaced by the given
     * ones.
     * </p>
     * <p>
     * May also be used when there is no decision makers in this object. This means that no decision maker does not
     * necessarily imply empty coalitions.
     * </p>
     * 
     * @param coalitions
     *            not <code>null</code>. If empty, every coalitions are removed.
     * @return <code>true</code> iff the call changed the state of this object.
     */
    public boolean setSharedCoalitions(Coalitions coalitions);

    /**
     * <p>
     * Retrieves a read-only view of the thresholds shared by all decision makers. If they are not shared, the view is
     * empty. The set of criteria on which thresholds are provided is a subset of the set returned by
     * {@link #getCriteria()}. Note that a view which is initially non empty, because this object contains identical
     * thresholds for every decision makers, may become empty, e.g. because different thresholds have been set for a new
     * decision maker. When a decision maker has no associated thresholds and others have, the thresholds are
     * <em>not</em> considered shared.
     * </p>
     * <p>
     * When this view is non empty, the {@link #getThresholds()} method of this object returns the same thresholds for
     * every decision makers (meaning: same contents, not necessarily the same object).
     * </p>
     * 
     * @return not <code>null</code>.
     */
    public Thresholds getSharedThresholds();
}
