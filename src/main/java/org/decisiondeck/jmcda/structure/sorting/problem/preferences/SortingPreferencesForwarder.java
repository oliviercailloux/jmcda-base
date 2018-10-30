package org.decisiondeck.jmcda.structure.sorting.problem.preferences;

import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.thresholds.Thresholds;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataForwarder;

import com.google.common.base.Preconditions;

public class SortingPreferencesForwarder extends SortingDataForwarder implements ISortingPreferences {

    public SortingPreferencesForwarder(ISortingPreferences delegate) {
	super(delegate);
	Preconditions.checkNotNull(delegate);
    }

    @Override
    protected ISortingPreferences delegate() {
	return (ISortingPreferences) super.delegate();
    }



    @Override
    public Coalitions getCoalitions() {
	return delegate().getCoalitions();
    }

    @Override
    public EvaluationsRead getProfilesEvaluations() {
	return delegate().getProfilesEvaluations();
    }

    @Override
    public Thresholds getThresholds() {
	return delegate().getThresholds();
    }

    @Override
    public Double getWeight(Criterion criterion) {
	return delegate().getWeight(criterion);
    }

    @Override
    public boolean setCoalitions(Coalitions coalitions) {
	return delegate().setCoalitions(coalitions);
    }

    @Override
    public boolean setProfilesEvaluations(EvaluationsRead evaluations) {
	return delegate().setProfilesEvaluations(evaluations);
    }

    @Override
    public boolean setThresholds(Thresholds thresholds) {
	return delegate().setThresholds(thresholds);
    }

}
