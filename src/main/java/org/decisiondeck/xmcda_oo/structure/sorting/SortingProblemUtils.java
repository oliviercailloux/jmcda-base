package org.decisiondeck.xmcda_oo.structure.sorting;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.interval.Interval;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.thresholds.Thresholds;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decisiondeck.jmcda.exc.InvalidInputException;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilitiesRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;
import org.decisiondeck.jmcda.structure.sorting.problem.ProblemFactory;
import org.decisiondeck.jmcda.structure.sorting.problem.assignments.ISortingAssignments;
import org.decisiondeck.jmcda.structure.sorting.problem.assignments.ISortingAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.problem.assignments.SortingAssignmentsFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.assignments.SortingAssignmentsToMultipleFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.data.IProblemData;
import org.decisiondeck.jmcda.structure.sorting.problem.data.ISortingData;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.group_assignments.GroupSortingAssignmentsFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.group_assignments.IGroupSortingAssignments;
import org.decisiondeck.jmcda.structure.sorting.problem.group_assignments.IGroupSortingAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.problem.group_assignments.IGroupSortingAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.problem.group_assignments.IGroupSortingAssignmentsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.GroupSortingDataFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.IGroupSortingData;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.IGroupSortingPreferences;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.GroupSortingResultsFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.GroupSortingResultsImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.GroupSortingResultsToMultipleImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.GroupSortingResultsWithCredibilitiesFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.GroupSortingResultsWithCredibilitiesImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.GroupSortingResultsWithOrder;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.IGroupSortingResults;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.IGroupSortingResultsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.ISortingPreferences;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.SortingPreferencesFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.results.ISortingResults;
import org.decisiondeck.jmcda.structure.sorting.problem.results.ISortingResultsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.problem.results.ISortingResultsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.problem.results.SortingResultsFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.results.SortingResultsToMultipleComplete;
import org.decisiondeck.jmcda.structure.sorting.problem.results.SortingResultsToMultipleFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.results.SortingResultsViewGroupBacked;
import org.decisiondeck.jmcda.structure.sorting.problem.results.SortingResultsWithCredibilitiesViewGroupBacked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

/**
 * Helper methods to deal with sorting structures.
 * 
 * @author Olivier Cailloux
 * 
 */
public class SortingProblemUtils {
    private static final Logger s_logger = LoggerFactory.getLogger(SortingProblemUtils.class);

    /**
     * Replaces the contents in the given target by the given data.
     * 
     * @param data
     *            not {@code null}.
     * @param target
     *            not {@code null}.
     */
    public static void copyDataToTarget(ISortingData data, ISortingData target) {
	target.getAlternatives().clear();
	target.getAlternatives().addAll(data.getAlternatives());
	target.getCriteria().clear();
	target.getCriteria().addAll(data.getCriteria());
	target.getProfiles().clear();
	target.getProfiles().addAll(data.getProfiles());
	target.getCatsAndProfs().clear();
	target.getCatsAndProfs().addAll(data.getCatsAndProfs());
	target.setEvaluations(data.getAlternativesEvaluations());
	final Map<Criterion, Interval> scales = data.getScales();
	for (Criterion criterion : scales.keySet()) {
	    target.setScale(criterion, scales.get(criterion));
	}
    }

    /**
     * Copy constructor by value. No reference is held to the given object.
     * 
     * @param source
     *            not {@code null}.
     * @return not {@code null}.
     */
    static public GroupSortingResultsWithOrder newGroupAssignmentsWithOrder(IGroupSortingAssignments source) {
	final GroupSortingResultsImpl target = new GroupSortingResultsImpl();
	copyGroupAssignmentsToTarget(source, target);
	return new GroupSortingResultsWithOrder(target);
    }

    static public IGroupSortingAssignments newGroupAssignments() {
	return new GroupSortingResultsImpl();
    }

    /**
     * Replaces the contents in the given target by the given data.
     * 
     * @param data
     *            not {@code null}.
     * @param target
     *            not {@code null}.
     */
    public static void copyGroupDataToTarget(IGroupSortingData data, IGroupSortingData target) {
	copyDataToTarget(data, target);
	target.getDms().clear();
	target.getDms().addAll(data.getDms());
    }

    /**
     * Copy constructor by value. No reference is held to the given object.
     * 
     * @param source
     *            not {@code null}.
     * @return not {@code null}.
     */
    public static IGroupSortingPreferences newGroupPreferences(IGroupSortingPreferences source) {
	IGroupSortingPreferences target = newGroupPreferences();
	copyGroupPreferencesToTarget(source, target);
	return target;
    }

    /**
     * Replaces the contents in the given target by the given data. The target is set to not keep shared informations.
     * 
     * @param preferences
     *            not {@code null}.
     * @param target
     *            not {@code null}.
     */
    static public void copyGroupPreferencesToTarget(IGroupSortingPreferences preferences,
	    IGroupSortingPreferences target) {
	/** Necessary if the source dms is empty and the target has shared information, otherwize they are not erased. */
	target.setKeepSharedCoalitions(false);
	target.setKeepSharedProfilesEvaluations(false);
	target.setKeepSharedThresholds(false);
	copyGroupDataToTarget(preferences, target);

	if (!preferences.getSharedProfilesEvaluations().isEmpty()) {
	    target.setSharedProfilesEvaluations(preferences.getSharedProfilesEvaluations());
	} else {
	    final Map<DecisionMaker, EvaluationsRead> evaluations = preferences.getProfilesEvaluations();
	    for (DecisionMaker dm : evaluations.keySet()) {
		target.setProfilesEvaluations(dm, evaluations.get(dm));
	    }
	}
	/** TODO problem with shared things, when preferences have no dms but have shared info. */
	final Map<DecisionMaker, Coalitions> coalitions = preferences.getCoalitions();
	for (DecisionMaker dm : coalitions.keySet()) {
	    target.setCoalitions(dm, coalitions.get(dm));
	}
	final Map<DecisionMaker, Thresholds> thresholds = preferences.getThresholds();
	for (DecisionMaker dm : thresholds.keySet()) {
	    target.setThresholds(dm, thresholds.get(dm));
	}
	s_logger.debug("Copied group preferences, dimension " + getDimensionStr(preferences) + ".");
    }

    public static IGroupSortingPreferences newGroupPreferences() {
	return new GroupSortingPreferencesImpl();
    }

    static public String getDimensionStr(ISortingData data) {
	final int altsNb = data.getAlternatives().size();
	final int critsNb = data.getCriteria().size();
	return "" + altsNb + " alts, " + critsNb + " crits, "
		+ (data.getAlternativesEvaluations().getValueCount() == altsNb * critsNb) + " full evaluations, "
		+ data.getProfiles().size() + " profs, ";
    }

    static public void copyPreferencesToTarget(ISortingPreferences source, final ISortingPreferences target) {
	copyDataToTarget(source, target);
	target.setProfilesEvaluations(source.getProfilesEvaluations());
	target.setCoalitions(source.getCoalitions());
	target.setThresholds(source.getThresholds());
    }

    static public IGroupSortingResults newGroupResults(IGroupSortingResults source) {
	IGroupSortingResults target = newGroupResults();
	copyGroupPreferencesToTarget(source, target);
	for (DecisionMaker dm : source.getDms()) {
	    final IOrderedAssignmentsRead assignments = source.getAssignments(dm);
	    AssignmentsUtils.copyOrderedAssignmentsToTarget(assignments, target.getAssignments(dm));
	}
	return target;
    }

    static public IGroupSortingResultsWithCredibilities newGroupResultsWithCredibilities(
	    IGroupSortingResultsWithCredibilities source) {
	IGroupSortingResultsWithCredibilities target = newGroupResultsWithCredibilities();
	copyGroupPreferencesToTarget(source, target);
	for (DecisionMaker dm : source.getDms()) {
	    final IOrderedAssignmentsWithCredibilitiesRead assignments = source.getAssignments(dm);
	    AssignmentsUtils.copyOrderedAssignmentsWithCredibilitiesToTarget(assignments, target.getAssignments(dm));
	}
	return target;
    }

    static public IGroupSortingResults newGroupResults() {
	return new GroupSortingResultsImpl();
    }

    static public IGroupSortingResultsWithCredibilities newGroupResultsWithCredibilities() {
	return new GroupSortingResultsWithCredibilitiesImpl();
    }

    static public void copyGroupResultsToTarget(IGroupSortingResults source, IGroupSortingResults target) {
	copyGroupPreferencesToTarget(source, target);
	for (DecisionMaker dm : source.getDms()) {
	    final IOrderedAssignmentsRead assignments = source.getAssignments(dm);
	    AssignmentsUtils.copyOrderedAssignmentsToTarget(assignments, target.getAssignments(dm));
	}
    }

    static public ISortingResults getResultsWithOnlyAssignedAlternatives(IGroupSortingResults group, DecisionMaker dm) {
	final SortingResultsViewGroupBacked thisDm = new SortingResultsViewGroupBacked(group, dm);
	return getResultsWithOnlyAssignedAlternatives(thisDm);
    }

    static public ISortingAssignmentsToMultiple newAssignmentsToMultiple(ISortingAssignments source) {
	final ISortingAssignmentsToMultiple target = newAssignmentsToMultiple();
	copyDataToTarget(source, target);
	AssignmentsUtils.copyOrderedAssignmentsToMultipleToTarget(source.getAssignments(), target.getAssignments());
	return target;
    }

    static public ISortingResults newResults() {
	final IGroupSortingResults newGroupResults = newGroupResults();
	final DecisionMaker dm = new DecisionMaker("dm");
	newGroupResults.getDms().add(dm);
	final ISortingResults results = new SortingResultsViewGroupBacked(newGroupResults, dm);
	return results;
    }

    static public void copyAssignmentsToMultipleToTarget(ISortingAssignmentsToMultiple assignments,
	    ISortingAssignmentsToMultiple target) {
	copyDataToTarget(assignments, target);
	AssignmentsUtils
		.copyOrderedAssignmentsToMultipleToTarget(assignments.getAssignments(), target.getAssignments());
    }

    public static ISortingResults newResults(ISortingResults source) {
	final ISortingResults results = newResults();
	copyResultsToTarget(source, results);
	return results;
    }

    public static void copyResultsToTarget(ISortingResults results, ISortingResults target) {
	copyPreferencesToTarget(results, target);
	AssignmentsUtils.copyOrderedAssignmentsToTarget(results.getAssignments(), target.getAssignments());
    }

    static public ISortingAssignments newAssignments() {
	return newResults();
    }

    static public void copyGroupResultsWithCredibilitiesToTargetSingle(IGroupSortingResultsWithCredibilities source,
	    IGroupSortingResults target) throws InvalidInputException {
	copyGroupPreferencesToTarget(source, target);
	copyAllAssignmentsToTargetSingle(source, target);
    }

    private static void copyAllAssignmentsToTargetSingle(IGroupSortingAssignmentsWithCredibilities source,
	    IGroupSortingAssignments target) throws InvalidInputException {
	for (DecisionMaker dm : source.getDms()) {
	    final IOrderedAssignmentsWithCredibilitiesRead assignments = source.getAssignments(dm);
	    AssignmentsUtils.ensuresSingle(assignments);
	    AssignmentsUtils.copyOrderedAssignmentsWithCredibilitiesToTargetSingle(assignments,
		    target.getAssignments(dm));
	}
    }

    static public IGroupSortingResultsWithCredibilities getReadView(IGroupSortingResultsWithCredibilities source) {
	return new GroupSortingResultsWithCredibilitiesFiltering(source);
    }

    static public IGroupSortingPreferences getReadView(IGroupSortingPreferences source) {
	return new GroupSortingPreferencesFiltering(source);
    }

    static public ISortingPreferences getReadView(ISortingPreferences source) {
	return new SortingPreferencesFiltering(source);
    }

    static public IGroupSortingData getReadView(IGroupSortingData source) {
	return new GroupSortingDataFiltering(source);
    }

    static public IGroupSortingAssignments getReadView(IGroupSortingAssignments source) {
	return new GroupSortingAssignmentsFiltering(source);
    }

    static public IGroupSortingResults getReadView(IGroupSortingResults source) {
	return new GroupSortingResultsFiltering(source);
    }

    static public ISortingData getReadView(ISortingData source) {
	return new SortingDataFiltering(source);
    }

    static public void copyGroupAssignmentsWithCredibilitiesToTargetSingle(
	    IGroupSortingAssignmentsWithCredibilities source, IGroupSortingAssignments target)
	    throws InvalidInputException {
	copyGroupDataToTarget(source, target);
	copyAllAssignmentsToTargetSingle(source, target);
    }

    /**
     * Copy constructor by value. No reference is held to the given object.
     * 
     * @param source
     *            not {@code null}.
     * @return not {@code null}.
     */
    public static IGroupSortingAssignmentsToMultiple newGroupAssignmentsToMultiple(
	    IGroupSortingAssignmentsToMultipleRead source) {
	IGroupSortingAssignmentsToMultiple target = newGroupAssignmentsToMultiple();
	copyGroupDataToTarget(source, target);
	for (DecisionMaker dm : source.getDms()) {
	    final IOrderedAssignmentsToMultipleRead assignments = source.getAssignments(dm);
	    AssignmentsUtils.copyOrderedAssignmentsToMultipleToTarget(assignments, target.getAssignments(dm));
	}
	return target;
    }

    /**
     * Retrieves a read-only view of all the alternatives assigned by the dms contained in the given problem
     * <em>at the time this method is invoked</em>. If the set of assignments change for a given dm in that set after
     * this method returns, the change will be reflected in the returned set, but if a decision maker is added, the
     * change is not reflected.
     * 
     * @param groupSortingAssignments
     *            not {@code null}.
     * @return not {@code null}.
     */
    static public Set<Alternative> getAssignedAlternatives(
	    IGroupSortingAssignmentsToMultipleRead groupSortingAssignments) {
	final Iterable<? extends IOrderedAssignmentsToMultipleRead> allAssignments = groupSortingAssignments
		.getAssignments().values();
	return AssignmentsUtils.getUnionAssignedAlternatives(allAssignments);
    }

    static public ISortingAssignmentsToMultiple newAssignmentsToMultiple() {
	return ProblemFactory.newSortingResultsToMultiple();
    }

    static public IGroupSortingAssignmentsWithCredibilities newGroupAssignmentsWithCredibilities() {
	return new GroupSortingResultsWithCredibilitiesImpl();
    }

    static public ISortingResultsWithCredibilities newResultsWithCredibilities() {
	final GroupSortingResultsWithCredibilitiesImpl group = new GroupSortingResultsWithCredibilitiesImpl();
	final DecisionMaker dm = new DecisionMaker("dm");
	group.getDms().add(dm);
	return new SortingResultsWithCredibilitiesViewGroupBacked(group, dm);
    }

    static public ISortingResultsToMultiple getResultsWithOnlyAssignedAlternatives(
	    final ISortingResultsToMultiple results) {
	return new SortingResultsToMultipleFiltering(results, new Predicate<Alternative>() {
	    @Override
	    public boolean apply(Alternative input) {
		return results.getAssignments().getAlternatives().contains(input);
	    }
	}, false);
    }

    static public ISortingResults getResultsWithOnlyAssignedAlternatives(final ISortingResults results) {
	return new SortingResultsFiltering(results, new Predicate<Alternative>() {
	    @Override
	    public boolean apply(Alternative input) {
		return results.getAssignments().getAlternatives().contains(input);
	    }
	}, false);
    }

    static public ISortingResultsToMultiple getResultsWithAllAlternativesAssigned(ISortingResultsToMultiple input) {
	return new SortingResultsToMultipleComplete(input);
    }

    static public ISortingResultsToMultiple getReadViewToMultiple(ISortingResultsToMultiple source) {
	return new SortingResultsToMultipleFiltering(source);
    }

    static public ISortingResultsToMultiple getReadViewToMultiple(ISortingResults source) {
	return new SortingResultsToMultipleFiltering(source);
    }

    static public ISortingResults getReadView(ISortingResults source) {
	return new SortingResultsFiltering(source);
    }

    static public IGroupSortingAssignmentsToMultipleRead getReadView(IGroupSortingAssignmentsToMultipleRead source) {
	/** should wrap into a read-only object. */
	return source;
    }

    /**
     * Copy constructor by value. No reference is held to the given object.
     * 
     * @param source
     *            not {@code null}.
     * @return not {@code null}.
     */
    public static IGroupSortingAssignments newGroupAssignments(IGroupSortingAssignments source) {
	IGroupSortingAssignments target = newGroupAssignments();
	copyGroupDataToTarget(source, target);
	for (DecisionMaker dm : source.getDms()) {
	    final IOrderedAssignmentsRead assignments = source.getAssignments(dm);
	    AssignmentsUtils.copyOrderedAssignmentsToTarget(assignments, target.getAssignments(dm));
	}
	return target;
    }

    static public IGroupSortingAssignmentsToMultiple newGroupAssignmentsToMultiple() {
	return new GroupSortingResultsToMultipleImpl();
    }

    /**
     * Copy constructor by value. No reference is held to the given object.
     * 
     * @param source
     *            not {@code null}.
     * @return not {@code null}.
     */
    public static IGroupSortingAssignmentsToMultiple newGroupAssignmentsToMultiple(IGroupSortingAssignments source) {
	IGroupSortingAssignmentsToMultiple target = newGroupAssignmentsToMultiple();
	copyGroupDataToTarget(source, target);
	for (DecisionMaker dm : source.getDms()) {
	    final IOrderedAssignmentsRead assignments = source.getAssignments(dm);
	    AssignmentsUtils.copyOrderedAssignmentsToMultipleToTarget(assignments, target.getAssignments(dm));
	}
	return target;
    }

    static public ISortingAssignments newAssignments(ISortingAssignments source) {
	final ISortingResults results = newResults();
	copyAssignmentsToTarget(source, results);
	return results;
    }

    static public void copyAssignmentsToTarget(ISortingAssignments assignments, ISortingAssignments target) {
	copyDataToTarget(assignments, target);
	AssignmentsUtils.copyOrderedAssignmentsToTarget(assignments.getAssignments(), target.getAssignments());
    }

    static public ISortingAssignmentsToMultiple newAssignmentsToMultiple(ISortingAssignmentsToMultiple source) {
	final ISortingAssignmentsToMultiple target = newAssignmentsToMultiple();
	copyAssignmentsToMultipleToTarget(source, target);
	return target;
    }

    static public ISortingAssignments getReadView(ISortingAssignments source) {
	return new SortingAssignmentsFiltering(source);
    }

    public static void copyResultsToMultipleToTarget(ISortingResultsToMultiple results, ISortingResultsToMultiple target) {
	copyPreferencesToTarget(results, target);
	AssignmentsUtils.copyOrderedAssignmentsToMultipleToTarget(results.getAssignments(), target.getAssignments());
    }

    /**
     * Copy constructor by value. No reference is held to the given object.
     * 
     * @param source
     *            not {@code null}.
     * @return not {@code null}.
     */
    static public GroupSortingResultsWithOrder newGroupResultsWithOrder(IGroupSortingResults source) {
	final GroupSortingResultsImpl target = new GroupSortingResultsImpl();
	copyGroupResultsToTarget(source, target);
	return new GroupSortingResultsWithOrder(target);
    }

    static public void copyGroupAssignmentsToTarget(IGroupSortingAssignments source, IGroupSortingAssignments target) {
	copyGroupDataToTarget(source, target);
	for (DecisionMaker dm : source.getDms()) {
	    final IOrderedAssignmentsRead assignments = source.getAssignments(dm);
	    AssignmentsUtils.copyOrderedAssignmentsToTarget(assignments, target.getAssignments(dm));
	}
    }

    public static Map<DecisionMaker, Set<Alternative>> getAssignedAlternativesByDm(IGroupSortingResults group) {
	final Map<DecisionMaker, Set<Alternative>> ownAlternatives = Maps.newHashMap();
	for (DecisionMaker dm : group.getDms()) {
	    final Set<Alternative> alternatives = group.getAssignments(dm).getAlternatives();
	    ownAlternatives.put(dm, alternatives);
	}
	return ownAlternatives;
    }

    /**
     * Associates the given scale to each criterion stored in the given problem.
     * 
     * @param scale
     *            not {@code null}.
     */
    static public void setScales(IProblemData target, Interval scale) {
	checkNotNull(scale);
	for (Criterion criterion : target.getCriteria()) {
	    target.setScale(criterion, scale);
	}
    }

    /**
     * Adds the given evaluations to the given data. Alternatives must match the set of evaluated alternatives and
     * serves only to define iteration order.
     * 
     * @param alternatives
     *            not {@code null}.
     * @param evaluations
     *            not {@code null}.
     */
    void addEvaluationsTo(Set<Alternative> alternatives, EvaluationsRead evaluations, ISortingData targetData) {
	checkNotNull(alternatives);
	checkNotNull(evaluations);
	checkArgument(alternatives.equals(evaluations.getRows()));
	checkArgument(targetData.getCriteria().equals(evaluations.getColumns()));
	for (Alternative alternative : alternatives) {
	    final boolean newAlt = targetData.getAlternatives().add(alternative);
	    if (!newAlt) {
		for (Criterion criterion : targetData.getCriteria()) {
		    final Double existing = targetData.getAlternativesEvaluations().getEntry(alternative, criterion);
		    final Double newEvaluation = evaluations.getEntry(alternative, criterion);
		    checkArgument(Objects.equal(existing, newEvaluation));
		}
	    }
	}
    }

    static public ISortingAssignmentsToMultiple getReadView(ISortingAssignmentsToMultiple source) {
	return new SortingAssignmentsToMultipleFiltering(source);
    }

    static public ISortingResults getResultsWithOnlyAlternatives(ISortingResults results, Set<Alternative> alternatives) {
	return new SortingResultsFiltering(results, Predicates.in(alternatives), false);
    }

}
