package org.decision_deck.jmcda.structure.weights;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Set;

import org.decision_deck.jmcda.structure.Criterion;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class CoalitionsImpl implements Coalitions {
    @Override
    public String toString() {
	final ToStringHelper helper = Objects.toStringHelper(this);
	helper.add("Weights", m_weights);
	helper.add("Majority", m_lambda);
	return helper.toString();
    }

    /**
     * Wrap the given weights to provide coalitions features. Changes to this object writes to the delegate weights, and
     * conversely.
     * 
     * @param weights
     *            not <code>null</code>.
     * @param majorityThreshold
     *            a number greater than or equal to zero.
     */
    CoalitionsImpl(Weights weights, double majorityThreshold) {
	this(weights);
	setMajorityThreshold(majorityThreshold);
    }

    public CoalitionsImpl() {
	m_weights = WeightsImpl.create();
    }

    /**
     * Wrap the given weights to provide coalitions features. Changes to this object writes to the delegate weights, and
     * conversely.
     * 
     * @param weights
     *            not <code>null</code>.
     */
    CoalitionsImpl(Weights weights) {
	checkNotNull(weights);
	m_weights = weights;
    }

    public CoalitionsImpl(CoalitionsImpl source) {
	m_weights = WeightsUtils.newWeights(source.getWeights());
	m_lambda = source.m_lambda;
    }

    @Override
    public int hashCode() {
	return CoalitionsUtils.getCoalitionsEquivalenceRelation().hash(this);
    }

    @Override
    public boolean approxEquals(Coalitions c2, double tolerance) {
	return org.decision_deck.jmcda.structure.weights.CoalitionsUtils.approxEqual(this, c2, tolerance);
    }

    /**
     * Never an infinite or NaN value. When not set, is <code>null</code>.
     */
    private Double m_lambda;

    @Override
    public Double setMajorityThreshold(double majorityThreshold) {
	if (Double.isInfinite(majorityThreshold) || Double.isNaN(majorityThreshold) || majorityThreshold < 0) {
	    throw new IllegalArgumentException("Valid value required.");
	}
	final Double old = m_lambda;
	m_lambda = Double.valueOf(majorityThreshold);
	return old;
    }

    private final Weights m_weights;

    @Override
    public double getWeight(Criterion criterion) {
	checkState(m_weights.containsKey(criterion));
	final double weight = m_weights.getWeightBetter(criterion);
	return weight;
    }

    public void putAll(Weights weights) {
	m_weights.putAll(weights);
    }

    @Deprecated
    public int numberOfWeights() {
	return m_weights.size();
    }

    @Override
    public Weights getWeights() {
	return m_weights;
    }

    @Override
    public Set<Criterion> getCriteria() {
	return m_weights.keySet();
    }

    /**
     * TODO remove and replace with set and with remove.
     * 
     * @param majorityThreshold
     *            a valid value (not infinite or NaN). To unset the value, set it to <code>null</code>.
     */
    public void setMajorityThresholdOld(Double majorityThreshold) {
	if (majorityThreshold != null
		&& (majorityThreshold.isInfinite() || majorityThreshold.isNaN() || majorityThreshold.doubleValue() < 0)) {
	    throw new IllegalArgumentException("Valid value required.");
	}
	m_lambda = majorityThreshold;
    }

    @Override
    public Double putWeight(Criterion criterion, double weight) {
	return m_weights.putWeight(criterion, weight);
    }

    @Override
    public Double removeMajorityThreshold() {
	final Double old = m_lambda;
	m_lambda = null;
	return old;
    }



    @Override
    public Double removeWeight(Criterion criterion) {
	return m_weights.remove(criterion);
    }

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof Coalitions)) {
	    return false;
	}
	Coalitions c2 = (Coalitions) obj;
	return CoalitionsUtils.getCoalitionsEquivalenceRelation().equivalent(this, c2);
    }

    @Override
    public boolean isEmpty() {
	return m_lambda == null && m_weights.isEmpty();
    }

    @Override
    public double getMajorityThreshold() {
	checkState(m_lambda != null);
	return m_lambda.doubleValue();
    }

    @Override
    public boolean containsMajorityThreshold() {
	return m_lambda != null;
    }


}
