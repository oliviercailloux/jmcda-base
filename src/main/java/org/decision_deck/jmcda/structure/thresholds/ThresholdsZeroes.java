package org.decision_deck.jmcda.structure.thresholds;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.utils.collection.SetBackedMap;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

class ThresholdsZeroes extends ThresholdsMapBased implements Thresholds {

    private final Set<Criterion> m_criteria;
    private final ImmutableMap<Criterion, Double> m_empty = ImmutableMap.of();

    public ThresholdsZeroes(Set<Criterion> criteria) {
	checkNotNull(criteria);
	m_criteria = criteria;
    }

    @Override
    public Map<Criterion, Double> getVetoThresholds() {
	return m_empty;
    }

    @Override
    public Map<Criterion, Double> getPreferenceThresholds() {
	return new SetBackedMap<Criterion, Double>(m_criteria, Functions.constant(Double.valueOf(0d)));
    }

    @Override
    public Map<Criterion, Double> getIndifferenceThresholds() {
	return new SetBackedMap<Criterion, Double>(m_criteria, Functions.constant(Double.valueOf(0d)));
    }

}
