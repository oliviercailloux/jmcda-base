package org.decisiondeck.jmcda.structure.sorting.problem.group_preferences;

import java.util.Map;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.matrix.EvaluationsUtils;
import org.decision_deck.jmcda.structure.thresholds.Thresholds;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decision_deck.utils.collection.SetBackedMap;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.GroupSortingDataFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.GroupSortingDataForwarder;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.ISortingPreferences;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.SortingPreferencesViewGroupBacked;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * <p>
 * A read-only view. Restricting to a subset of alternatives is possible when the object is created. The filtering only
 * (possibly) impacts the alternatives. Profiles, proviles evaluations, etc., are untouched.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class GroupSortingPreferencesFiltering extends GroupSortingDataForwarder implements IGroupSortingPreferences {

    private final IGroupSortingPreferences m_delegate;


    /**
     * Creates a view that only view the alternatives admitted by the given filter, or equivalently, that filters out
     * the alternatives that do not pass the given filter. This only concerns the real alternatives, the profiles are
     * untouched. To filter a constant set of alternatives, use {@link Predicates#in(java.util.Collection)}.
     * 
     * @param delegate
     *            not {@code null}.
     * @param filterAlternatives
     *            {@code null} to allow everything (equivalent to {@link Predicates#alwaysTrue()}.
     */
    public GroupSortingPreferencesFiltering(IGroupSortingPreferences delegate, Predicate<Alternative> filterAlternatives) {
	super(new GroupSortingDataFiltering(delegate, filterAlternatives));
	m_delegate = delegate;
    }

    /**
     * Creates a read-only view of the given data.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public GroupSortingPreferencesFiltering(IGroupSortingPreferences delegate) {
	this(delegate, Predicates.<Alternative> alwaysTrue());
    }

    @Override
    public boolean setCoalitions(DecisionMaker dm, Coalitions coalitions) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public void setKeepSharedCoalitions(boolean keepShared) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public void setKeepSharedProfilesEvaluations(boolean keepShared) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public void setKeepSharedThresholds(boolean keepShared) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean setProfilesEvaluation(DecisionMaker dm, Alternative alternative, Criterion criterion, Double value) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean setProfilesEvaluations(DecisionMaker dm, EvaluationsRead evaluations) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean setSharedCoalitions(Coalitions coalitions) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean setSharedProfilesEvaluations(EvaluationsRead profilesEvaluations) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean setSharedThresholds(Thresholds thresholds) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean setThresholds(DecisionMaker dm, Thresholds thresholds) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }



    @Override
    public Map<DecisionMaker, Coalitions> getCoalitions() {
	return m_delegate.getCoalitions();
    }

    @Override
    public ISortingPreferences getPreferences(DecisionMaker dm) {
	Preconditions.checkNotNull(dm);
	if (!getDms().contains(dm)) {
	    return null;
	}
	return new SortingPreferencesViewGroupBacked(this, dm);
    }

    @Override
    public Coalitions getCoalitions(DecisionMaker dm) {
	return m_delegate.getCoalitions(dm);
    }

    @Override
    public Double getWeight(DecisionMaker dm, Criterion criterion) {
	return m_delegate.getWeight(dm, criterion);
    }

    @Override
    public Map<DecisionMaker, Thresholds> getThresholds() {
	return m_delegate.getThresholds();
    }

    @Override
    public Thresholds getThresholds(DecisionMaker dm) {
	return m_delegate.getThresholds(dm);
    }

    @Override
    public Map<DecisionMaker, EvaluationsRead> getProfilesEvaluations() {
	return new SetBackedMap<DecisionMaker, EvaluationsRead>(super.getDms(),
		new Function<DecisionMaker, EvaluationsRead>() {
		    @Override
		    public EvaluationsRead apply(DecisionMaker input) {
			return m_delegate.getProfilesEvaluations(input);
		    }
		});
    }



    @Override
    public EvaluationsRead getProfilesEvaluations(DecisionMaker dm) {
	return EvaluationsUtils.getFilteredView(m_delegate.getProfilesEvaluations(dm), delegate().getProfilesFilter(),
		null);
    }

    @Override
    public EvaluationsRead getSharedProfilesEvaluations() {
	return EvaluationsUtils.getFilteredView(m_delegate.getSharedProfilesEvaluations(), delegate()
		.getProfilesFilter(), null);
    }

    @Override
    public Coalitions getSharedCoalitions() {
	return m_delegate.getSharedCoalitions();
    }

    @Override
    public ISortingPreferences getSharedPreferences() {
	return new SortingPreferencesViewGroupBacked(this);
    }

    @Override
    public Thresholds getSharedThresholds() {
	return m_delegate.getSharedThresholds();
    }



    @Override
    protected GroupSortingDataFiltering delegate() {
	return (GroupSortingDataFiltering) super.delegate();
    }

    /**
     * @return {@code null} for everything allowed.
     */
    public Predicate<Alternative> getAlternativesFilter() {
	return delegate().getAlternativesFilter();
    }

}
