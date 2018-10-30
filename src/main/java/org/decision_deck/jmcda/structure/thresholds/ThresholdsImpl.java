package org.decision_deck.jmcda.structure.thresholds;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.utils.collection.CollectionUtils;

class ThresholdsImpl extends ThresholdsMapBased implements Thresholds {

    /**
     * Never <code>null</code>.
     */
    private final Map<Criterion, Double> m_indiffs = CollectionUtils.newMapNoNull();
    /**
     * Never <code>null</code>.
     */
    private final Map<Criterion, Double> m_prefs = CollectionUtils.newMapNoNull();
    /**
     * Never <code>null</code>.
     */
    private final Map<Criterion, Double> m_vetoes = CollectionUtils.newMapNoNull();

    @Override
    public Map<Criterion, Double> getVetoThresholds() {
	return m_vetoes;
    }

    @Override
    public Map<Criterion, Double> getPreferenceThresholds() {
	return m_prefs;
    }

    @Override
    public Map<Criterion, Double> getIndifferenceThresholds() {
	return m_indiffs;
    }

    /**
     * @param prefs
     *            not <code>null</code>.
     * @param indiffs
     *            not <code>null</code>.
     * @param vetoes
     *            not <code>null</code>.
     */
    public ThresholdsImpl(Map<Criterion, Double> prefs, Map<Criterion, Double> indiffs, Map<Criterion, Double> vetoes) {
	super();
	checkNotNull(prefs);
	checkNotNull(indiffs);
	checkNotNull(vetoes);
	getPreferenceThresholds().putAll(prefs);
	getIndifferenceThresholds().putAll(indiffs);
	getVetoThresholds().putAll(vetoes);
    }

    public ThresholdsImpl(Thresholds source) {
	super();
	checkNotNull(source);
	getPreferenceThresholds().putAll(source.getPreferenceThresholds());
	getIndifferenceThresholds().putAll(source.getIndifferenceThresholds());
	getVetoThresholds().putAll(source.getVetoThresholds());
    }

    public ThresholdsImpl() {
	super();
    }

}
