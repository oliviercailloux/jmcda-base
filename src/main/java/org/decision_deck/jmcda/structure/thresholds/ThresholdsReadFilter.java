package org.decision_deck.jmcda.structure.thresholds;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Map;

import org.decision_deck.jmcda.structure.Criterion;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

class ThresholdsReadFilter extends ThresholdsMapBased implements Thresholds {

    private Thresholds m_delegate;
    private Predicate<Criterion> m_criteriaPredicate;
    private final Map<Criterion, Double> m_indifsView;
    private final Map<Criterion, Double> m_prefsView;
    private final Map<Criterion, Double> m_vetoesView;

    /**
     * @param delegate
     *            not <code>null</code>.
     * @param criteriaPredicate
     *            not <code>null</code>.
     */
    public ThresholdsReadFilter(Thresholds delegate, Predicate<Criterion> criteriaPredicate) {
	checkNotNull(delegate);
	checkNotNull(criteriaPredicate);
	m_delegate = delegate;
	m_criteriaPredicate = criteriaPredicate;
	m_prefsView = Collections.unmodifiableMap(Maps.filterKeys(m_delegate.getPreferenceThresholds(),
		m_criteriaPredicate));
	m_indifsView = Collections.unmodifiableMap(Maps.filterKeys(m_delegate.getIndifferenceThresholds(),
		m_criteriaPredicate));
	m_vetoesView = Collections
		.unmodifiableMap(Maps.filterKeys(m_delegate.getVetoThresholds(), m_criteriaPredicate));
    }

    @Override
    public Map<Criterion, Double> getVetoThresholds() {
	return m_vetoesView;
    }

    @Override
    public Map<Criterion, Double> getPreferenceThresholds() {
	return m_prefsView;
    }

    @Override
    public Map<Criterion, Double> getIndifferenceThresholds() {
	return m_indifsView;
    }

}
