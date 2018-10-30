package org.decisiondeck.jmcda.structure.sorting.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.interval.Interval;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decision_deck.jmcda.structure.thresholds.Thresholds;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decisiondeck.jmcda.structure.sorting.assignment.IAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilitiesRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;
import org.decisiondeck.jmcda.structure.sorting.problem.assignments.ISortingAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.problem.assignments.ISortingAssignmentsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.problem.data.IProblemData;
import org.decisiondeck.jmcda.structure.sorting.problem.data.ISortingData;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.group_assignments.IGroupSortingAssignments;
import org.decisiondeck.jmcda.structure.sorting.problem.group_assignments.IGroupSortingAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.problem.group_assignments.IGroupSortingAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.IGroupSortingPreferences;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.GroupSortingResultsImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.GroupSortingResultsToMultipleImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.GroupSortingResultsWithCredibilitiesImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.IGroupSortingResults;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.IGroupSortingResultsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.IGroupSortingResultsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.ISortingPreferences;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.SortingPreferencesViewGroupBacked;
import org.decisiondeck.jmcda.structure.sorting.problem.results.ISortingResultsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.problem.results.SortingResultsToMultipleViewGroupBacked;
import org.decisiondeck.jmcda.structure.sorting.problem.results.SortingResultsWithCredibilitiesViewGroupBacked;
import org.decisiondeck.xmcda_oo.structure.sorting.SortingProblemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class ProblemFactory {
    private static final Logger s_logger = LoggerFactory.getLogger(ProblemFactory.class);

    public static IProblemData newProblemData() {
	return new SortingDataImpl();
    }

    static public ISortingData newSortingData(ISortingData source) {
	ISortingData target = newSortingData();
	SortingProblemUtils.copyDataToTarget(source, target);
	return target;
    }

    /**
     * Creates a sorting problem object that contains the given evaluations, scales, categories, profiles. The scales
     * and evaluations may concern different sets of criteria. </p>
     * <p>
     * The alternativesEvaluations and categories profiles must contain disjoint sets of alternatives. This is necessary
     * to ensure that no alternative is also a profile.
     * </p>
     * 
     * @param evaluations
     *            may be <code>null</code>.
     * @param scales
     *            may be <code>null</code>.
     * @param catsAndProfs
     *            may be <code>null</code>.
     * @return not <code>null</code>.
     */
    static public ISortingData newSortingData(EvaluationsRead evaluations, Map<Criterion, Interval> scales,
	    CatsAndProfs catsAndProfs) {
	ISortingData target = newSortingData();
	if (evaluations != null) {
	    target.setEvaluations(evaluations);
	}
	if (catsAndProfs != null) {
	    target.getCatsAndProfs().addAll(catsAndProfs);
	}
	if (scales != null) {
	    for (Criterion criterion : scales.keySet()) {
		target.setScale(criterion, scales.get(criterion));
	    }
	}
	return target;
    }

    public static ISortingData newSortingData() {
	return new SortingDataImpl();
    }

    /**
     * Creates a problem data object that contains the given evaluations and scales. The scales and evaluations may
     * concern different sets of criteria.
     * 
     * @param evaluations
     *            may be <code>null</code>.
     * @param scales
     *            may be <code>null</code>.
     * @return not <code>null</code>.
     */
    static public IProblemData newProblemData(EvaluationsRead evaluations, Map<Criterion, Interval> scales) {
	final IProblemData data = newProblemData();
	if (evaluations != null) {
	    data.setEvaluations(evaluations);
	}
	if (scales != null) {
	    for (Criterion criterion : scales.keySet()) {
		final Interval scale = scales.get(criterion);
		data.setScale(criterion, scale);
	    }
	}
	return data;
    }

    /**
     * Creates a problem data object that contains the given information.
     * 
     * @param source
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public IProblemData newProblemData(IProblemData source) {
	checkNotNull(source);
	final IProblemData target = newProblemData(source.getAlternativesEvaluations(), source.getScales());
	target.getAlternatives().addAll(source.getAlternatives());
	target.getCriteria().addAll(source.getCriteria());
	return target;
    }

    /**
     * <p>
     * Creates a problem data object that contains the given data. The scales, evaluations, thresholds, weights may
     * concern different sets of criteria.
     * </p>
     * <p>
     * The alternativesEvaluations and profilesEvaluations must contain disjoint sets of alternatives. The
     * alternativesEvaluations and categories profiles must contain disjoint sets of alternatives. These two conditions
     * ensure that no real alternative is also a profile.
     * </p>
     * 
     * @param alternativesEvaluations
     *            may be <code>null</code>.
     * @param scales
     *            may be <code>null</code>.
     * @param categories
     *            may be <code>null</code>.
     * @param profilesEvaluations
     *            may be <code>null</code>.
     * @param thresholds
     *            may be <code>null</code>.
     * @param coalitions
     *            may be <code>null</code>.
     * @return not <code>null</code>.
     */
    static public ISortingPreferences newSortingPreferences(EvaluationsRead alternativesEvaluations,
	    Map<Criterion, Interval> scales, CatsAndProfs categories, EvaluationsRead profilesEvaluations,
	    Thresholds thresholds, Coalitions coalitions) {
	checkArgument((alternativesEvaluations == null || profilesEvaluations == null)
		|| Sets.intersection(alternativesEvaluations.getRows(), profilesEvaluations.getRows()).isEmpty());
	checkArgument((alternativesEvaluations == null || categories == null)
		|| Sets.intersection(alternativesEvaluations.getRows(), categories.getProfiles()).isEmpty());

	final ISortingPreferences data = newSortingPreferences();
	if (alternativesEvaluations != null) {
	    data.setEvaluations(alternativesEvaluations);
	}
	if (scales != null) {
	    for (Criterion criterion : scales.keySet()) {
		final Interval scale = scales.get(criterion);
		data.setScale(criterion, scale);
	    }
	}
	if (categories != null) {
	    data.getCatsAndProfs().addAll(categories);
	}
	if (profilesEvaluations != null) {
	    data.setProfilesEvaluations(profilesEvaluations);
	}
	if (thresholds != null) {
	    data.setThresholds(thresholds);
	}
	if (coalitions != null) {
	    data.setCoalitions(coalitions);
	}
	return data;
    }

    public static ISortingPreferences newSortingPreferences() {
	return new SortingPreferencesViewGroupBacked(SortingProblemUtils.newGroupPreferences());
    }

    public static ISortingPreferences newSortingPreferences(ISortingPreferences source) {
	final ISortingPreferences target = newSortingPreferences();
	SortingProblemUtils.copyPreferencesToTarget(source, target);
	s_logger.debug("Copied data, dimension " + SortingProblemUtils.getDimensionStr(source) + ".");
	return target;
    }

    static public IGroupSortingResultsWithCredibilities newGroupSortingResultsWithCredibilities() {
	final GroupSortingResultsWithCredibilitiesImpl group = new GroupSortingResultsWithCredibilitiesImpl();
	return group;
    }

    static public ISortingResultsToMultiple newSortingResultsToMultiple(EvaluationsRead alternativesEvaluations,
	    Map<Criterion, Interval> scales, CatsAndProfs catsAndProfs, EvaluationsRead profilesEvaluations,
	    Thresholds thresholds, Coalitions coalitions, IOrderedAssignmentsToMultipleRead assignments) {
	final ISortingResultsToMultiple target = newSortingResultsToMultiple();
	if (alternativesEvaluations != null) {
	    target.setEvaluations(alternativesEvaluations);
	}
	if (catsAndProfs != null) {
	    target.getCatsAndProfs().addAll(catsAndProfs);
	}
	if (scales != null) {
	    for (Criterion criterion : scales.keySet()) {
		target.setScale(criterion, scales.get(criterion));
	    }
	}
	if (profilesEvaluations != null) {
	    target.setProfilesEvaluations(profilesEvaluations);
	}
	if (coalitions != null) {
	    target.setCoalitions(coalitions);
	}
	if (thresholds != null) {
	    target.setThresholds(thresholds);
	}
	if (assignments != null) {
	    AssignmentsUtils.copyOrderedAssignmentsToMultipleToTarget(assignments, target.getAssignments());
	}
	return target;
    }

    static public ISortingResultsToMultiple newSortingResultsToMultiple(ISortingResultsToMultiple source) {
	final ISortingResultsToMultiple results = newSortingResultsToMultiple();
	results.getAlternatives().addAll(source.getAlternatives());
	results.getCriteria().addAll(source.getCriteria());
	results.getProfiles().addAll(source.getProfiles());
	results.getCatsAndProfs().addAll(source.getCatsAndProfs());
	results.setEvaluations(source.getAlternativesEvaluations());
	final Map<Criterion, Interval> scales = source.getScales();
	for (Criterion criterion : scales.keySet()) {
	    results.setScale(criterion, scales.get(criterion));
	}
	results.setProfilesEvaluations(source.getProfilesEvaluations());
	results.setCoalitions(source.getCoalitions());
	results.setThresholds(source.getThresholds());
	AssignmentsUtils.copyOrderedAssignmentsToMultipleToTarget(source.getAssignments(), results.getAssignments());
	return results;
    }

    static public IGroupSortingPreferences newGroupSortingPreferences(EvaluationsRead alternativesEvaluations,
	    Map<Criterion, Interval> scales, CatsAndProfs catsAndProfs,
	    Map<DecisionMaker, EvaluationsRead> profilesEvaluations,
	    Map<DecisionMaker, Thresholds> thresholds, Map<DecisionMaker, Coalitions> coalitions) {
	final IGroupSortingPreferences target = newGroupSortingPreferences();
	if (alternativesEvaluations != null) {
	    target.setEvaluations(alternativesEvaluations);
	}
	if (scales != null) {
	    for (Criterion criterion : scales.keySet()) {
		target.setScale(criterion, scales.get(criterion));
	    }
	}
	if (catsAndProfs != null) {
	    target.getCatsAndProfs().addAll(catsAndProfs);
	}

	if (profilesEvaluations != null) {
	    for (DecisionMaker dm : profilesEvaluations.keySet()) {
		final EvaluationsRead evaluations = profilesEvaluations.get(dm);
		checkArgument(evaluations != null);
		target.setProfilesEvaluations(dm, evaluations);
	    }
	}
	if (thresholds != null) {
	    for (DecisionMaker dm : thresholds.keySet()) {
		final Thresholds thresholdsDm = thresholds.get(dm);
		checkArgument(thresholdsDm != null);
		target.setThresholds(dm, thresholdsDm);
	    }
	}
	if (coalitions != null) {
	    for (DecisionMaker dm : coalitions.keySet()) {
		final Coalitions coalitionsDm = coalitions.get(dm);
		checkArgument(coalitionsDm != null);
		target.setCoalitions(dm, coalitionsDm);
	    }
	}

	return target;
    }

    static public ISortingAssignmentsWithCredibilities newSortingAssignmentsWithCredibilities(
	    EvaluationsRead alternativesEvaluations, Map<Criterion, Interval> scales,
	    CatsAndProfs catsAndProfs, IOrderedAssignmentsWithCredibilitiesRead assignments) {
	final ISortingAssignmentsWithCredibilities target = newSortingAssignmentsWithCredibilities();
	if (alternativesEvaluations != null) {
	    target.setEvaluations(alternativesEvaluations);
	}
	if (catsAndProfs != null) {
	    target.getCatsAndProfs().addAll(catsAndProfs);
	}
	if (scales != null) {
	    for (Criterion criterion : scales.keySet()) {
		target.setScale(criterion, scales.get(criterion));
	    }
	}
	if (assignments != null) {
	    AssignmentsUtils.copyOrderedAssignmentsWithCredibilitiesToTarget(assignments, target.getAssignments());
	}
	return target;
    }

    static public ISortingAssignmentsWithCredibilities newSortingAssignmentsWithCredibilities() {
	final GroupSortingResultsWithCredibilitiesImpl group = new GroupSortingResultsWithCredibilitiesImpl();
	final DecisionMaker dm = new DecisionMaker("dm");
	group.getDms().add(dm);
	return new SortingResultsWithCredibilitiesViewGroupBacked(group, dm);
    }

    static public IGroupSortingPreferences newGroupSortingPreferences() {
	final GroupSortingResultsImpl group = new GroupSortingResultsImpl();
	return group;
    }

    static public ISortingAssignmentsToMultiple newSortingAssignmentsToMultiple() {
	final GroupSortingResultsToMultipleImpl group = new GroupSortingResultsToMultipleImpl();
	final DecisionMaker dm = new DecisionMaker("dm");
	group.getDms().add(dm);
	return new SortingResultsToMultipleViewGroupBacked(group, dm);
    }

    static public ISortingAssignmentsToMultiple newSortingAssignmentsToMultiple(EvaluationsRead alternativesEvaluations,
	    Map<Criterion, Interval> scales, CatsAndProfs catsAndProfs, IAssignmentsToMultipleRead assignments) {
	final ISortingResultsToMultiple target = newSortingResultsToMultiple();
	if (alternativesEvaluations != null) {
	    target.setEvaluations(alternativesEvaluations);
	}
	if (catsAndProfs != null) {
	    target.getCatsAndProfs().addAll(catsAndProfs);
	    target.getAssignments().setCategories(catsAndProfs.getCategories());
	}
	if (scales != null) {
	    for (Criterion criterion : scales.keySet()) {
		target.setScale(criterion, scales.get(criterion));
	    }
	}
	if (assignments != null) {
	    for (Alternative alternative : assignments.getAlternatives()) {
		final Set<Category> categories = assignments.getCategories(alternative);
		target.getAssignments().setCategories(alternative, categories);
	    }
	}
	return target;
    }

    static public IGroupSortingAssignmentsToMultiple newGroupSortingAssignmentsToMultiple(
	    EvaluationsRead alternativesEvaluations, Map<Criterion, Interval> scales,
	    CatsAndProfs catsAndProfs, Map<DecisionMaker, ? extends IOrderedAssignmentsToMultipleRead> assignments) {
	checkArgument(assignments == null || !assignments.keySet().contains(null));

	final IGroupSortingResultsToMultiple target = newGroupSortingResultsToMultiple();
	if (alternativesEvaluations != null) {
	    target.setEvaluations(alternativesEvaluations);
	}
	if (catsAndProfs != null) {
	    target.getCatsAndProfs().addAll(catsAndProfs);
	}
	if (scales != null) {
	    for (Criterion criterion : scales.keySet()) {
		target.setScale(criterion, scales.get(criterion));
	    }
	}

	if (assignments != null) {
	    target.getDms().addAll(assignments.keySet());

	    final Set<DecisionMaker> dms = assignments.keySet();
	    for (DecisionMaker dm : dms) {
		final IOrderedAssignmentsToMultipleRead source = assignments.get(dm);
		final IOrderedAssignmentsToMultiple targetAssignments = target.getAssignments(dm);
		checkArgument(targetAssignments != null, "Assignments not found for " + dm + ".");
		AssignmentsUtils.copyOrderedAssignmentsToMultipleToTarget(source, targetAssignments);
	    }
	}
	return target;
    }

    static public IGroupSortingResultsToMultiple newGroupSortingResultsToMultiple() {
	final GroupSortingResultsToMultipleImpl group = new GroupSortingResultsToMultipleImpl();
	return group;
    }

    static public IGroupSortingAssignmentsToMultiple newGroupSortingAssignmentsToMultiple() {
	return newGroupSortingResultsToMultiple();
    }

    static public IGroupSortingAssignments newGroupSortingAssignments(EvaluationsRead alternativesEvaluations,
	    Map<Criterion, Interval> scales, CatsAndProfs catsAndProfs,
	    Map<DecisionMaker, ? extends IOrderedAssignmentsRead> assignments) {
	final IGroupSortingResults target = newGroupSortingResults();
	if (alternativesEvaluations != null) {
	    target.setEvaluations(alternativesEvaluations);
	}
	if (catsAndProfs != null) {
	    target.getCatsAndProfs().addAll(catsAndProfs);
	}
	if (scales != null) {
	    for (Criterion criterion : scales.keySet()) {
		target.setScale(criterion, scales.get(criterion));
	    }
	}

	if (assignments != null) {
	    target.getDms().addAll(assignments.keySet());

	    final Set<DecisionMaker> dms = assignments.keySet();
	    for (DecisionMaker dm : dms) {
		final IOrderedAssignmentsRead source = assignments.get(dm);
		final IOrderedAssignments targetAssignments = target.getAssignments(dm);
		checkArgument(targetAssignments != null, "Assignments not found for " + dm + ".");
		AssignmentsUtils.copyOrderedAssignmentsToTarget(source, targetAssignments);
	    }
	}
	return target;
    }

    static public IGroupSortingAssignments newGroupSortingAssignments() {
	return new GroupSortingResultsImpl();
    }

    static public IGroupSortingResults newGroupSortingResults(EvaluationsRead alternativesEvaluations,
	    Map<Criterion, Interval> scales, CatsAndProfs catsAndProfs,
	    Map<DecisionMaker, EvaluationsRead> profilesEvaluations,
	    Map<DecisionMaker, Thresholds> thresholds, Map<DecisionMaker, Coalitions> coalitions,
	    Map<DecisionMaker, ? extends IOrderedAssignmentsRead> assignments) {
	final IGroupSortingResults target = newGroupSortingResults();
	if (alternativesEvaluations != null) {
	    target.setEvaluations(alternativesEvaluations);
	}
	if (scales != null) {
	    for (Criterion criterion : scales.keySet()) {
		target.setScale(criterion, scales.get(criterion));
	    }
	}
	if (catsAndProfs != null) {
	    target.getCatsAndProfs().addAll(catsAndProfs);
	}

	if (profilesEvaluations != null) {
	    for (DecisionMaker dm : profilesEvaluations.keySet()) {
		final EvaluationsRead evaluations = profilesEvaluations.get(dm);
		checkArgument(evaluations != null);
		target.setProfilesEvaluations(dm, evaluations);
	    }
	}
	if (thresholds != null) {
	    for (DecisionMaker dm : thresholds.keySet()) {
		final Thresholds thresholdsDm = thresholds.get(dm);
		checkArgument(thresholdsDm != null);
		target.setThresholds(dm, thresholdsDm);
	    }
	}
	if (coalitions != null) {
	    for (DecisionMaker dm : coalitions.keySet()) {
		final Coalitions coalitionsDm = coalitions.get(dm);
		checkArgument(coalitionsDm != null);
		target.setCoalitions(dm, coalitionsDm);
	    }
	}

	if (assignments != null) {
	    target.getDms().addAll(assignments.keySet());

	    final Set<DecisionMaker> dms = assignments.keySet();
	    for (DecisionMaker dm : dms) {
		final IOrderedAssignmentsRead source = assignments.get(dm);
		final IOrderedAssignments targetAssignments = target.getAssignments(dm);
		checkArgument(targetAssignments != null, "Assignments not found for " + dm + ".");
		AssignmentsUtils.copyOrderedAssignmentsToTarget(source, targetAssignments);
	    }
	}
	return target;
    }

    static public IGroupSortingResults newGroupSortingResults() {
	return new GroupSortingResultsImpl();
    }

    static public IGroupSortingAssignmentsToMultiple newGroupSortingAssignmentsToMultiple(
	    IGroupSortingAssignmentsToMultipleRead source) {
	return newGroupSortingAssignmentsToMultiple(source.getAlternativesEvaluations(), source.getScales(),
		source.getCatsAndProfs(), source.getAssignments());
    }

    static public ISortingResultsToMultiple newSortingResultsToMultiple() {
	final GroupSortingResultsToMultipleImpl group = new GroupSortingResultsToMultipleImpl();
	final DecisionMaker dm = new DecisionMaker("dm");
	group.getDms().add(dm);
	return new SortingResultsToMultipleViewGroupBacked(group, dm);
    }

}
