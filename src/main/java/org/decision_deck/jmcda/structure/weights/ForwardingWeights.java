package org.decision_deck.jmcda.structure.weights;

import static com.google.common.base.Preconditions.checkNotNull;

import org.decision_deck.jmcda.structure.Criterion;

import com.google.common.collect.ForwardingMap;

public class ForwardingWeights extends ForwardingMap<Criterion, Double> implements Weights {

    private final Weights m_delegate;

    public ForwardingWeights(Weights delegate) {
	checkNotNull(delegate);
	m_delegate = delegate;
    }

    @Override
    public boolean approxEquals(Weights w2, double tolerance) {
	return m_delegate.approxEquals(w2, tolerance);
    }

    @Override
    public Weights getNormalized() {
	return m_delegate.getNormalized();
    }

    @Override
    public double getSum() {
	return m_delegate.getSum();
    }

    @Override
    public double getWeightBetter(Criterion criterion) {
	return m_delegate.getWeightBetter(criterion);
    }

    @Override
    public Double putWeight(Criterion criterion, double weight) {
	return m_delegate.putWeight(criterion, weight);
    }

    @Override
    protected Weights delegate() {
	return m_delegate;
    }

}
