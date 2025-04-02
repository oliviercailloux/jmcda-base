package org.decisiondeck.jmcda.structure.sorting.problem.preferences;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.interval.Interval;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.matrix.EvaluationsUtils;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decision_deck.jmcda.structure.thresholds.Thresholds;
import org.decision_deck.jmcda.structure.thresholds.ThresholdsUtils;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decision_deck.jmcda.structure.weights.CoalitionsUtils;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.IGroupSortingPreferences;

import com.google.common.base.Preconditions;

/**
 * A writeable view that reads and writes through an {@link IGroupSortingPreferences} object.
 * 
 * @author Olivier Cailloux
 * 
 */
public class SortingPreferencesViewGroupBacked implements ISortingPreferences {
    private final IGroupSortingPreferences m_delegate;
    /**
     * {@code null} to access the shared informations.
     */
    private final DecisionMaker m_dm;

    /**
     * A new view that delegates to the shared preferential informations instead of delegating to a specific decision
     * maker's preferences.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public SortingPreferencesViewGroupBacked(IGroupSortingPreferences delegate) {
	if (delegate == null) {
	    throw new NullPointerException("" + delegate);
	}
	m_delegate = delegate;
	m_dm = null;
    }

    /**
     * 
     * Creates a new preferences view that reads the given decision maker information into the given delegate. To read
     * the shared information instead, use the other constructor.
     * 
     * @param delegate
     *            not {@code null}.
     * @param dm
     *            not {@code null}.
     */
    public SortingPreferencesViewGroupBacked(IGroupSortingPreferences delegate, DecisionMaker dm) {
	Preconditions.checkNotNull(delegate);
	Preconditions.checkNotNull(dm);
	m_delegate = delegate;
	m_dm = dm;
    }

    @Override
    public Set<Alternative> getAllAlternatives() {
	return m_delegate.getAllAlternatives();
    }

    @Override
    public EvaluationsRead getAlternativesEvaluations() {
	return m_delegate.getAlternativesEvaluations();
    }

    @Override
    public CatsAndProfs getCatsAndProfs() {
	return m_delegate.getCatsAndProfs();
    }

    @Override
    public Set<Alternative> getProfiles() {
	return m_delegate.getProfiles();
    }

    @Override
    public Set<Criterion> getCriteria() {
	return m_delegate.getCriteria();
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return m_delegate.getAlternatives();
    }

    @Override
    public Map<Criterion, Interval> getScales() {
	return m_delegate.getScales();
    }

    @Override
    public boolean setEvaluation(Alternative alternative, Criterion criterion, Double value) {
	return m_delegate.setEvaluation(alternative, criterion, value);
    }

    @Override
    public boolean setScale(Criterion criterion, Interval scale) {
	return m_delegate.setScale(criterion, scale);
    }



    @Override
    public Coalitions getCoalitions() {
	if (m_dm == null) {
	    return m_delegate.getSharedCoalitions();
	}
	Coalitions coalitions = m_delegate.getCoalitions(m_dm);
	return coalitions == null ? CoalitionsUtils.asReadView(CoalitionsUtils.newCoalitions()) : coalitions;
    }

    @Override
    public EvaluationsRead getProfilesEvaluations() {
	if (m_dm == null) {
	    return m_delegate.getSharedProfilesEvaluations();
	}
	EvaluationsRead profilesEvaluations = m_delegate.getProfilesEvaluations(m_dm);
	return profilesEvaluations == null ? EvaluationsUtils.getReadView(EvaluationsUtils.newEvaluationMatrix())
		: profilesEvaluations;
    }

    @Override
    public Thresholds getThresholds() {
	if (m_dm == null) {
	    return m_delegate.getSharedThresholds();
	}
	Thresholds thresholds = m_delegate.getThresholds(m_dm);
	return thresholds == null ? ThresholdsUtils.getReadView(ThresholdsUtils.newThresholds()) : thresholds;
    }

    @Override
    public Double getWeight(Criterion criterion) {
	if (m_dm == null) {
	    return m_delegate.getSharedCoalitions().getWeights().get(criterion);
	}
	Double weight = m_delegate.getWeight(m_dm, criterion);
	return weight;
    }

    @Override
    public boolean setCoalitions(Coalitions coalitions) {
	if (m_dm == null) {
	    return m_delegate.setSharedCoalitions(coalitions);
	}
	return m_delegate.setCoalitions(m_dm, coalitions);
    }

    @Override
    public boolean setProfilesEvaluations(EvaluationsRead evaluations) {
	if (m_dm == null) {
	    return m_delegate.setSharedProfilesEvaluations(evaluations);
	}
	return m_delegate.setProfilesEvaluations(m_dm, evaluations);
    }

    @Override
    public boolean setThresholds(Thresholds thresholds) {
	if (m_dm == null) {
	    return m_delegate.setSharedThresholds(thresholds);
	}
	return m_delegate.setThresholds(m_dm, thresholds);
    }

    @Override
    public boolean setEvaluations(EvaluationsRead evaluations) {
	return m_delegate.setEvaluations(evaluations);
    }

    /**
     * Retrieves the decision maker whose related informations this objects reads in the delegated group sorting object.
     * 
     * @return {@code null} iff this objects read the shared informations.
     */
    public DecisionMaker getViewedDm() {
	return m_dm;
    }

    protected IGroupSortingPreferences delegate() {
	return m_delegate;
    }

}
