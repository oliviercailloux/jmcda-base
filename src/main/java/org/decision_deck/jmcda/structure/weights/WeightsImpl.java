package org.decision_deck.jmcda.structure.weights;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.utils.PredicateUtils;
import org.decision_deck.utils.collection.MapEvents.PreAdditionEvent;
import org.decision_deck.utils.collection.MapEvents.PreRemovalEvent;
import org.decision_deck.utils.collection.ObservableMap;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

public class WeightsImpl extends ObservableMap<Criterion, Double> implements Weights {

    private static class NormalizedWeights extends ForwardingWeights implements Weights {

	public NormalizedWeights(final Weights delegate) {
	    super(wrap(Maps.transformValues(delegate, new Function<Double, Double>() {
		@Override
		public Double apply(Double input) {
		    return Double.valueOf(input.doubleValue() / delegate.getSum());
		}
	    })));
	}

	@Override
	public Weights getNormalized() {
	    return this;
	}
    }

    /**
     * Unverified code, unused - to delete?
     * 
     * @author Olivier Cailloux
     * 
     */
    @SuppressWarnings("unused")
    private static class WeightsIterator implements Iterator<Criterion> {
	private final WeightsImpl m_delegateWeights;
	private final Iterator<Criterion> m_iterator;
	private Criterion m_lastKey;

	public WeightsIterator(final WeightsImpl weights) {
	    if (weights == null) {
		throw new NullPointerException("Must have a delegate.");
	    }
	    m_delegateWeights = weights;
	    m_lastKey = null;
	    m_iterator = weights.keySet().iterator();
	}

	@Override
	public boolean hasNext() {
	    return m_iterator.hasNext();
	}

	@Override
	public Criterion next() {
	    m_lastKey = m_iterator.next();
	    return m_lastKey;
	}

	@Override
	public void remove() {
	    if (m_lastKey == null) {
		throw new IllegalStateException("One remove per call to next allowed.");
	    }
	    m_delegateWeights.remove(m_lastKey);
	    m_lastKey = null;
	}
    }

    private static final Predicate<Criterion> KEY_PREDICATE = Predicates.<Criterion> notNull();
    private static final Predicate<Double> VALUE_PREDICATE = Predicates.<Double> and(Predicates.<Double> notNull(),
	    PredicateUtils.atLeast(0d));

    private static final Predicate<Entry<Criterion, Double>> ZE_PREDICATE = PredicateUtils.asEntryPredicate(
	    KEY_PREDICATE, VALUE_PREDICATE);

    static public WeightsImpl create() {
	return new WeightsImpl();
    }

    static public WeightsImpl newWeights(WeightsImpl source) {
	return new WeightsImpl(source);
    }

    static public WeightsImpl wrap(Map<Criterion, Double> delegateMap) {
	return new WeightsImpl(delegateMap);
    }

    /**
     * Vocabulary note: this object is dirty iff the cached computations must be re-computed. This field is
     * <code>true</code> iff this object is dirty.
     */
    private boolean m_dirty;

    /**
     * The sum of the weights contained in this object. Valid iff this object is not dirty.
     */
    private double m_sum;

    private WeightsImpl() {
	super(Maps.filterEntries(Maps.<Criterion, Double> newLinkedHashMap(), ZE_PREDICATE));
	m_sum = 0d;
	m_dirty = false;
	register(this);
    }

    /**
     * This object assumes ownership of the delegate map.
     * 
     * @param delegateMap
     *            no <code>null</code> key or values, all values at least zero.
     */
    private WeightsImpl(Map<Criterion, Double> delegateMap) {
	super(Maps.filterEntries(delegateMap, ZE_PREDICATE));
	assert (Iterables.all(delegateMap.entrySet(), ZE_PREDICATE));
	m_dirty = true;
	register(this);
    }

    private WeightsImpl(WeightsImpl source) {
	super(Maps.filterEntries(Maps.<Criterion, Double> newLinkedHashMap(), ZE_PREDICATE));
	m_sum = source.m_sum;
	m_dirty = source.m_dirty;
	putAll(source);
	register(this);
    }

    @Override
    public boolean approxEquals(Weights w2, double tolerance) {
	if (this == w2) {
	    return true;
	}
	if (w2 == null) {
	    return false;
	}
	if (!keySet().equals(w2.keySet())) {
	    return false;
	}

	final Weights w2Param = w2;
	final double toleranceParam = tolerance;

	final Set<Criterion> keySet = keySet();
	for (Criterion criterion : keySet) {
	    final double weight1 = getWeightBetter(criterion);
	    final double weight2 = w2Param.getWeightBetter(criterion);
	    final double diff = weight1 - weight2;
	    if (Math.abs(diff) > toleranceParam) {
		return false;
	    }
	}

	return true;
    }

    private void computeSum() {
	m_sum = 0d;
	for (Double weight : values()) {
	    m_sum = m_sum + weight.doubleValue();
	}

	m_dirty = false;
    }

    @Override
    public Weights getNormalized() {
	// if (m_dirty) {
	// computeSum();
	// }
	// final WeightsImpl normWeights = new WeightsImpl();
	// for (final Entry<Criterion, Double> entry : m_weights.entrySet()) {
	// final Double weight = entry.getValue();
	// final double norm = weight.doubleValue() / m_sum;
	// normWeights.putWeight(entry.getKey(), norm);
	// }
	// return normWeights;
	return new NormalizedWeights(this);
    }

    @Override
    public double getSum() {
	if (m_dirty) {
	    computeSum();
	}
	return m_sum;
    }

    @Override
    public double getWeightBetter(Criterion criterion) {
	checkArgument(containsKey(criterion), "No weight for " + criterion + ".");
	return get(criterion).doubleValue();
    }

    @Override
    public Double put(Criterion key, Double value) {
	/**
	 * The underlying map will refuse some entries, but will post pre- events before it realizes these entries are
	 * incorrect. This permits to avoid incorrect posting.
	 */
	checkArgument(KEY_PREDICATE.apply(key));
	checkArgument(VALUE_PREDICATE.apply(value), "Invalid value: " + value + ", key: " + key + ".");

	return super.put(key, value);
    }

    @Override
    public Double putWeight(Criterion criterion, double weight) {
	checkNotNull(criterion);
	final Double theWeight = Double.valueOf(weight);
	return put(criterion, theWeight);
    }

    @Override
    public String toString() {
	final ToStringHelper helper = Objects.toStringHelper(this);
	helper.addValue(delegate());
	return helper.toString();
    }

    @Subscribe
    public void updateSum(PreAdditionEvent<Criterion, Double> addition) {
	if (m_dirty) {
	    return;
	}
	assert addition != null;
	assert addition.getValue() != null;
	m_sum += addition.getValue().doubleValue();
    }

    @Subscribe
    public void updateSum(PreRemovalEvent<Criterion, Double> removal) {
	if (m_dirty) {
	    return;
	}
	m_sum -= removal.getValue().doubleValue();
    }

}
