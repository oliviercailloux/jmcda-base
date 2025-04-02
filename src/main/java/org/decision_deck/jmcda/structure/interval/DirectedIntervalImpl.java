package org.decision_deck.jmcda.structure.interval;

import static com.google.common.base.Preconditions.checkArgument;

import org.decision_deck.jmcda.structure.interval.mess.OrderedIntervalInteger;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;

/**
 * <p>
 * An interval on the set of reals, with a preference direction, typically attached to a criterion. The preference
 * direction indicates if an alternative, when evaluated on that criterion, is preferred to an other one if its
 * evaluation is higher (preference direction is to Maximize) or if it is lower (preference direction is to Minimize)
 * than an other one. The interval indicates the set of values that any evaluation may lie into. When considering the
 * criterion as an evaluation function, it is the codomain of the criterion. This object may have a preference direction
 * set, or the interval set (if not set, this is equivalent to the whole set of real numbers with infinite minimum and
 * maximum), or both, or nothing (be empty), and is immutable. The minimum value, when set, is the first value this
 * interval accepts, thus it is minimum inclusive. The same holds for the maximum. With a preference direction and an
 * interval defined, it is possible to query this object for the worst value and the best value supplementary to the
 * minimum and maximum value. The maximum must be greater than or equal to the minimum, regardless of the preference
 * direction of the bound criterion.
 * </p>
 * <p>
 * This object may also have a step size, that permits it to define a <em>discrete</em> interval, e.g. a subset of the
 * set of integers when using an integer step size and an integer minimum value.
 * </p>
 * <p>
 * Because this object accepts a minimum value equal to the maximum, this interval may represent a single point.
 * </p>
 * 
 * 
 * @author Olivier Cailloux
 * 
 */
public class DirectedIntervalImpl implements Interval {
    /**
     * {@link Double#NEGATIVE_INFINITY} for not set.
     */
    private final double m_minimum;
    /**
     * {@link Double#POSITIVE_INFINITY} for not set.
     */
    private final double m_maximum;
    /**
     * {@code null} or greater than zero.
     */
    private final Double m_stepSize;

    /**
     * @param preferenceDirection
     *            {@code null} for not set.
     * @param minimum
     *            {@link Double#NEGATIVE_INFINITY} for not set.
     * @param maximum
     *            {@link Double#POSITIVE_INFINITY} for not set.
     * @param stepSize
     *            {@code null} for not set, or must be greater than zero, in which case minimum must be set.
     */
    public DirectedIntervalImpl(PreferenceDirection preferenceDirection, double minimum, double maximum, Double stepSize) {
	m_preferenceDirection = preferenceDirection;
	checkArgument(!Double.isNaN(minimum));
	checkArgument(minimum < Double.POSITIVE_INFINITY);
	checkArgument(!Double.isNaN(maximum));
	checkArgument(maximum > Double.NEGATIVE_INFINITY);
	checkArgument(minimum <= maximum);
	checkArgument(stepSize == null || !Double.isInfinite(minimum));
	checkArgument(stepSize == null || stepSize.doubleValue() > 0d);
	m_minimum = minimum;
	m_maximum = maximum;
	m_stepSize = stepSize;
    }

    /**
     * Creates a new real interval, with no minimum or maximum bounds, representing the given preference direction.
     * 
     * @param direction
     *            {@code null} for not set.
     * @return a new interval.
     */
    static DirectedIntervalImpl newDirection(PreferenceDirection direction) {
	return new DirectedIntervalImpl(direction, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
    }

    @Override
    public double getWorst() {
	if (m_preferenceDirection == PreferenceDirection.MAXIMIZE) {
	    return m_minimum;
	}
	if (m_preferenceDirection == PreferenceDirection.MINIMIZE) {
	    return m_maximum;
	}
	throw new IllegalStateException("Preference direction must be set.");
    }

    /**
     * @param direction
     *            may be {@code null}.
     * @param minimum
     *            a real number, not infinite.
     * @param maximum
     *            a real number, or positive infinity.
     * @param stepSize
     *            a real number, not infinite.
     * @return a new interval representing the given data.
     */
    static DirectedIntervalImpl newDiscreteInterval(PreferenceDirection direction, double minimum, double maximum,
	    double stepSize) {
	checkArgument(!Double.isInfinite(minimum));
	checkArgument(!Double.isNaN(minimum));
	checkArgument(!Double.isInfinite(maximum));
	checkArgument(!Double.isNaN(maximum));
	checkArgument(!Double.isInfinite(stepSize));
	checkArgument(!Double.isNaN(stepSize));
	return new DirectedIntervalImpl(direction, minimum, maximum, Double.valueOf(stepSize));
    }

    /**
     * Creates an interval with possibly infinite lower and upper bounds.
     * 
     * @param direction
     *            may be {@code null}.
     * @param minimum
     *            a real number, or negative infinity.
     * @param maximum
     *            a real number, or positive infinity.
     * 
     * @return a new interval.
     */
    static DirectedIntervalImpl newUnrestrictedInterval(PreferenceDirection direction, double minimum, double maximum) {
	Preconditions.checkArgument(!Double.isNaN(minimum));
	Preconditions.checkArgument(minimum < Double.POSITIVE_INFINITY);
	Preconditions.checkArgument(!Double.isNaN(maximum));
	Preconditions.checkArgument(maximum > Double.NEGATIVE_INFINITY);
	return new DirectedIntervalImpl(direction, minimum, maximum, null);
    }

    static DirectedIntervalImpl newMaximizeDirection() {
	return new DirectedIntervalImpl(PreferenceDirection.MAXIMIZE, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
		null);
    }

    static DirectedIntervalImpl newMinimizeDirection() {
	return new DirectedIntervalImpl(PreferenceDirection.MINIMIZE, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
		null);
    }

    @Override
    public PreferenceDirection getPreferenceDirection() {
	return m_preferenceDirection;
    }

    @Override
    public int getDirectionAsSign() {
	if (m_preferenceDirection == PreferenceDirection.MAXIMIZE) {
	    return 1;
	}
	if (m_preferenceDirection == PreferenceDirection.MINIMIZE) {
	    return -1;
	}
	throw new IllegalStateException("Preference direction must be set.");
    }

    @Override
    public double getMaximum() {
	return m_maximum;
    }

    @Override
    public double getMinimum() {
	return m_minimum;
    }

    /**
     * Retrieves this object with a new facade permitting easier use when this interval only contains integers, i.e.,
     * when the worst, best, and increment values all are integers.
     * 
     * @return {@code null} iff this object can't be converted to an integer interval.
     */
    OrderedIntervalInteger getAsIntegerInterval() {
	return new OrderedIntervalInteger(this);
    }

    /**
     * May be {@code null}.
     */
    private final PreferenceDirection m_preferenceDirection;
    private static final Character REALS = Character.valueOf('\u211D');

    @Override
    public Double getStepSize() {
	return m_stepSize;
    }

    @Override
    public double getBest() {
	if (m_preferenceDirection == PreferenceDirection.MAXIMIZE) {
	    return m_maximum;
	}
	if (m_preferenceDirection == PreferenceDirection.MINIMIZE) {
	    return m_minimum;
	}
	throw new IllegalStateException("Preference direction must be set.");
    }

    /**
     * @param direction
     *            may be {@code null}.
     * @param minimum
     *            a real number, not infinite.
     * @param maximum
     *            a real number, not infinite.
     * @return a new interval representing the given data.
     */
    static DirectedIntervalImpl newInterval(PreferenceDirection direction, double minimum, double maximum) {
	Preconditions.checkArgument(!Double.isInfinite(minimum));
	Preconditions.checkArgument(!Double.isNaN(minimum));
	Preconditions.checkArgument(!Double.isInfinite(maximum));
	Preconditions.checkArgument(!Double.isNaN(maximum));
	return new DirectedIntervalImpl(direction, minimum, maximum, null);
    }

    @Override
    public DiscreteInterval getAsDiscreteInterval() {
	return new DiscreteInterval(this);
    }

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof Interval)) {
	    return false;
	}
	Interval i2 = (Interval) obj;
	return Intervals.getDirectedIntervalsEquivalenceRelation().equivalent(this, i2);
    }

    @Override
    public String toString() {
	final ToStringHelper helper = Objects.toStringHelper(this);
	// helper.add("Direction", m_preferenceDirection);
	// helper.add("Minimum", Double.valueOf(m_minimum));
	// helper.add("Maximum", Double.valueOf(m_maximum));
	// helper.add("Step", m_stepSize);
	if (m_preferenceDirection != null) {
	    final String dirStr = m_preferenceDirection.toString().substring(0, 3);
	    helper.addValue(dirStr);
	}

	if (Double.isInfinite(m_minimum) && Double.isInfinite(m_maximum)) {
	    if (m_stepSize != null) {
		throw new IllegalStateException();
	    }
	    helper.addValue(REALS);
	} else {
	    final String minStr;
	    if (Double.isInfinite(m_minimum)) {
		if (m_stepSize != null) {
		    throw new IllegalStateException();
		}
		minStr = "]";
	    } else {
		final StringBuffer beg = new StringBuffer("[" + m_minimum);
		if (m_stepSize != null) {
		    beg.append("+k*" + m_stepSize);
		}
		minStr = beg.toString();
	    }
	    helper.addValue(minStr);

	    final String maxStr;
	    if (Double.isInfinite(m_maximum)) {
		maxStr = "[";
	    } else {
		maxStr = m_maximum + "]";
	    }
	    helper.addValue(maxStr);
	}

	return helper.toString();
    }

    @Override
    public int hashCode() {
	return Intervals.getDirectedIntervalsEquivalenceRelation().hash(this);
    }

    /**
     * Creates an interval with infinite lower and upper bounds, representing the whole set of real numbers, and without
     * a preference direction.
     * 
     * @return a new interval.
     */
    static DirectedIntervalImpl newRealsInterval() {
	return new DirectedIntervalImpl(null, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
    }

    static public Function<Interval, PreferenceDirection> getToPreferenceDirectionFunction() {
	return new Function<Interval, PreferenceDirection>() {
	    @Override
	    public PreferenceDirection apply(Interval input) {
		return input.getPreferenceDirection();
	    }
	};
    }
}
