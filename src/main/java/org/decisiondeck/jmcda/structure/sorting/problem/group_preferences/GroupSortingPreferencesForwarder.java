package org.decisiondeck.jmcda.structure.sorting.problem.group_preferences;

import java.util.Map;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.thresholds.Thresholds;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.GroupSortingDataForwarder;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.ISortingPreferences;

public class GroupSortingPreferencesForwarder extends GroupSortingDataForwarder implements IGroupSortingPreferences {

    public GroupSortingPreferencesForwarder(IGroupSortingPreferences delegate) {
	super(delegate);
    }

    @Override
    protected IGroupSortingPreferences delegate() {
	return (IGroupSortingPreferences) super.delegate();
    }



    @Override
    public Map<DecisionMaker, Coalitions> getCoalitions() {
	return delegate().getCoalitions();
    }

    @Override
    public boolean setProfilesEvaluation(DecisionMaker dm, Alternative alternative, Criterion criterion, Double value) {
	return delegate().setProfilesEvaluation(dm, alternative, criterion, value);
    }

    @Override
    public ISortingPreferences getPreferences(DecisionMaker dm) {
	return delegate().getPreferences(dm);
    }

    @Override
    public Coalitions getCoalitions(DecisionMaker dm) {
	return delegate().getCoalitions(dm);
    }

    @Override
    public Double getWeight(DecisionMaker dm, Criterion criterion) {
	return delegate().getWeight(dm, criterion);
    }

    @Override
    public boolean setThresholds(DecisionMaker dm, Thresholds thresholds) {
	return delegate().setThresholds(dm, thresholds);
    }

    @Override
    public Map<DecisionMaker, Thresholds> getThresholds() {
	return delegate().getThresholds();
    }

    @Override
    public Thresholds getThresholds(DecisionMaker dm) {
	return delegate().getThresholds(dm);
    }

    @Override
    public Map<DecisionMaker, EvaluationsRead> getProfilesEvaluations() {
	return delegate().getProfilesEvaluations();
    }



    @Override
    public EvaluationsRead getProfilesEvaluations(DecisionMaker dm) {
	return delegate().getProfilesEvaluations(dm);
    }

    @Override
    public EvaluationsRead getSharedProfilesEvaluations() {
	return delegate().getSharedProfilesEvaluations();
    }

    @Override
    public void setKeepSharedThresholds(boolean keepShared) {
	delegate().setKeepSharedThresholds(keepShared);
    }

    @Override
    public void setKeepSharedCoalitions(boolean keepShared) {
	delegate().setKeepSharedCoalitions(keepShared);
    }

    @Override
    public void setKeepSharedProfilesEvaluations(boolean keepShared) {
	delegate().setKeepSharedProfilesEvaluations(keepShared);
    }

    @Override
    public boolean setSharedProfilesEvaluations(EvaluationsRead profilesEvaluations) {
	return delegate().setSharedProfilesEvaluations(profilesEvaluations);
    }

    @Override
    public boolean setCoalitions(DecisionMaker dm, Coalitions coalitions) {
	return delegate().setCoalitions(dm, coalitions);
    }

    @Override
    public boolean setProfilesEvaluations(DecisionMaker dm, EvaluationsRead evaluations) {
	return delegate().setProfilesEvaluations(dm, evaluations);
    }



    @Override
    public boolean setSharedThresholds(Thresholds thresholds) {
	return delegate().setSharedThresholds(thresholds);
    }

    @Override
    public Coalitions getSharedCoalitions() {
	return delegate().getSharedCoalitions();
    }

    @Override
    public ISortingPreferences getSharedPreferences() {
	return delegate().getSharedPreferences();
    }

    @Override
    public boolean setSharedCoalitions(Coalitions coalitions) {
	return delegate().setSharedCoalitions(coalitions);
    }

    @Override
    public Thresholds getSharedThresholds() {
	return delegate().getSharedThresholds();
    }

}
