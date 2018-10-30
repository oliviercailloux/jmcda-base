package org.decisiondeck.jmcda.structure.sorting.problem.view;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decisiondeck.jmcda.structure.sorting.problem.data.IProblemData;
import org.decisiondeck.jmcda.structure.sorting.problem.data.ISortingData;
import org.decisiondeck.jmcda.structure.sorting.problem.data.ProblemDataFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.IGroupSortingPreferences;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.ISortingPreferences;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.SortingPreferencesFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.SortingPreferencesViewGroupBacked;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class ProblemViewFactory {



    /**
     * Retrieves a writable view that delegates to the shared preferential informations in the given delegate.
     * 
     * @param groupDelegate
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public ISortingPreferences getSortingPreferencesGroupBacked(IGroupSortingPreferences groupDelegate) {
	return new SortingPreferencesViewGroupBacked(groupDelegate);
    }

    /**
     * Retrieves a view that sees only alternatives and criteria satisfying the given predicate. Only the alternatives
     * are filtered, the profiles are untouched.
     * 
     * @param preferences
     *            not <code>null</code>.
     * @param predicateAlternatives
     *            <code>null</code> to not filter alternatives.
     * @param predicateCriteria
     *            <code>null</code> to not filter criteria.
     * @return not <code>null</code>.
     */
    public static ISortingPreferences getRestrictedPreferences(ISortingPreferences preferences,
	    Predicate<Alternative> predicateAlternatives, Predicate<Criterion> predicateCriteria) {
	Preconditions.checkNotNull(preferences);
	final ISortingPreferences view = new SortingPreferencesFiltering(preferences, predicateAlternatives,
		predicateCriteria);
	return view;
    }

    /**
     * Retrieves a view that sees only alternatives and criteria satisfying the given predicate.
     * 
     * @param data
     *            not <code>null</code>.
     * @param predicateAlternatives
     *            <code>null</code> to not filter alternatives.
     * @param predicateCriteria
     *            <code>null</code> to not filter criteria.
     * @return not <code>null</code>.
     */
    public static ISortingData getRestrictedSortingData(ISortingData data,
            Predicate<Alternative> predicateAlternatives, Predicate<Criterion> predicateCriteria) {
	Preconditions.checkNotNull(data);
	final ISortingData view = new SortingDataFiltering(data, predicateAlternatives,
        	predicateCriteria);
        return view;
    }

    /**
     * Retrieves view of the preferences contained in the given group preferences, per decision maker, plus the shared
     * preferences view which is bound to the <code>null</code> key.
     * 
     * @param groupPreferences
     *            not <code>null</code>.
     * @return not <code>null</code>, at least one entry.
     */
    static public Map<DecisionMaker, ISortingPreferences> getPreferencesPerDmWithNull(
	    IGroupSortingPreferences groupPreferences) {
	checkNotNull(groupPreferences);
	final Map<DecisionMaker, ISortingPreferences> allPreferences = Maps.newLinkedHashMap();
	allPreferences.put(null, groupPreferences.getSharedPreferences());
	final Set<DecisionMaker> dms = groupPreferences.getDms();
	for (DecisionMaker dm : dms) {
	    allPreferences.put(dm, groupPreferences.getPreferences(dm));
	}
	return allPreferences;
    }

    /**
     * Retrieves a view that sees only alternatives and criteria satisfying the given predicate.
     * 
     * @param data
     *            not <code>null</code>.
     * @param predicateAlternatives
     *            <code>null</code> to not filter alternatives.
     * @param predicateCriteria
     *            <code>null</code> to not filter criteria.
     * @return not <code>null</code>.
     */
    public static IProblemData getRestrictedData(IProblemData data,
            Predicate<Alternative> predicateAlternatives, Predicate<Criterion> predicateCriteria) {
        Preconditions.checkNotNull(data);
        final IProblemData view = new ProblemDataFiltering(data, predicateAlternatives,
        	predicateCriteria);
        return view;
    }

}
