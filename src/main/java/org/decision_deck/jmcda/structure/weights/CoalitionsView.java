package org.decision_deck.jmcda.structure.weights;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.decision_deck.jmcda.structure.Criterion;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * An read-only view wrapping an other {@link Coalitions} object.
 * 
 * @author Olivier Cailloux
 * 
 */
class CoalitionsView implements Coalitions {
    private final Coalitions m_delegate;
    private final Predicate<Criterion> m_criteriaPredicate;
    private final WeightsImpl m_filteredWeights;

    /**
     * @param delegate
     *            not <code>null</code>.
     */
    public CoalitionsView(Coalitions delegate) {
	this(delegate, Predicates.<Criterion> alwaysTrue());
    }

    /**
     * Creates a view that views only the criteria allowed by the given predicate, or equivalently, that filters out
     * criteria not permitted by the given predicate.
     * 
     * @param delegate
     *            not <code>null</code>.
     * @param criteriaPredicate
     *            <code>null</code> to allow everything.
     */
    public CoalitionsView(Coalitions delegate, Predicate<Criterion> criteriaPredicate) {
	checkNotNull(delegate);
	checkNotNull(criteriaPredicate);
	m_delegate = delegate;
	m_criteriaPredicate = criteriaPredicate;
	m_filteredWeights = WeightsImpl.wrap(Maps.filterKeys(m_delegate.getWeights(), m_criteriaPredicate));
    }

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof Coalitions)) {
	    return false;
	}
	Coalitions c2 = (Coalitions) obj;
	return CoalitionsUtils.getCoalitionsEquivalenceRelation().equivalent(
		this, c2);
    }

    @Override
    public int hashCode() {
	return CoalitionsUtils.getCoalitionsEquivalenceRelation().hash(this);
    }

    @Override
    public Set<Criterion> getCriteria() {
	Set<Criterion> source = m_delegate.getCriteria();
	if (m_criteriaPredicate == null) {
	    return source;
	}
	return Sets.filter(source, m_criteriaPredicate);
    }

    @Override
    public boolean isEmpty() {
	return !containsMajorityThreshold() && getWeights().isEmpty();
    }

    @Override
    public double getWeight(Criterion criterion) {
	if (m_criteriaPredicate != null && !m_criteriaPredicate.apply(criterion)) {
	    throw new IllegalArgumentException();
	}
	return m_delegate.getWeight(criterion);
    }

    @Override
    public Double putWeight(Criterion criterion, double weight) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public Weights getWeights() {
	return m_filteredWeights;
	// final Weights source = m_delegate.getWeights();
	// if (m_criteriaPredicate == null) {
	// return source;
	// }
	// Weights weights = new WeightsImpl(source);
	// for (Criterion criterion : m_delegate.getCriteria()) {
	// if (!m_criteriaPredicate.apply(criterion)) {
	// weights.remove(criterion);
	// }
	// }
	// return weights;
    }



    @Override
    public Double setMajorityThreshold(double majorityThreshold) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public Double removeMajorityThreshold() {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean approxEquals(Coalitions c2, double tolerance) {
	return CoalitionsUtils.approxEqual(this, c2, tolerance);
    }

    @Override
    public String toString() {
	return m_delegate.toString();
    }

    @Override
    public double getMajorityThreshold() {
	return m_delegate.getMajorityThreshold();
    }

    @Override
    public boolean containsMajorityThreshold() {
	return m_delegate.containsMajorityThreshold();
    }

    @Override
    public Double removeWeight(Criterion criterion) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

}
