package org.decision_deck.jmcda.structure.interval;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.decision_deck.jmcda.structure.Criterion;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

/**
 * Basic helper methods dealing with basic model (structural) classes.
 * 
 * @author Olivier Cailloux
 * 
 */
public class Intervals {
    static public Equivalence<Interval> getDirectedIntervalsEquivalenceRelation() {
	return new Equivalence<Interval>() {
	    @Override
	    public boolean doEquivalent(Interval i1, Interval i2) {
		if (i1.getMaximum() != i2.getMaximum()) {
		    return false;
		}
		if (i1.getMinimum() != i2.getMinimum()) {
		    return false;
		}
		if (!Objects.equal(i1.getPreferenceDirection(), i2.getPreferenceDirection())) {
		    return false;
		}
		if (!Objects.equal(i1.getStepSize(), i2.getStepSize())) {
		    return false;
		}
		return true;
	    }

	    @Override
	    public int doHash(Interval interval) {
		return Objects.hashCode(interval.getPreferenceDirection(), Double.valueOf(interval.getMinimum()),
			Double.valueOf(interval.getMaximum()), interval.getStepSize());
	    }
	};
    }

    static public Map<Criterion, Interval> getAsDiscrete(Map<Criterion, Interval> scales, final double stepSize) {
	return Maps.transformValues(scales, new Function<Interval, Interval>() {
	    @Override
	    public Interval apply(Interval input) {
		return DirectedIntervalImpl.newDiscreteInterval(input.getPreferenceDirection(), input.getMinimum(),
			input.getMaximum(), stepSize);
	    }
	});
    }

    static public Map<Criterion, Interval> getAsContinuous(Map<Criterion, Interval> scales) {
	return Maps.transformValues(scales, new Function<Interval, Interval>() {
	    @Override
	    public Interval apply(Interval input) {
		return DirectedIntervalImpl.newUnrestrictedInterval(input.getPreferenceDirection(), input.getMinimum(),
			input.getMaximum());
	    }
	});
    }

    static public <T> Function<PreferenceDirection, Ordering<T>> getDirectionToOrderingFunction(Ordering<T> maxOrdering) {
	final Ordering<T> maxOrdering2 = maxOrdering;
	return new Function<PreferenceDirection, Ordering<T>>() {
	    @Override
	    public Ordering<T> apply(PreferenceDirection input) {
		checkNotNull(input);
		return input == PreferenceDirection.MAXIMIZE ? maxOrdering2 : maxOrdering2.reverse();
	    }
	};
    }

    public static Map<Criterion, PreferenceDirection> getDirectionsFromScales(Map<Criterion, Interval> scales) {
	return Maps.transformValues(scales, getIntervalToDirectionFunction());
    }

    static public Function<Interval, PreferenceDirection> getIntervalToDirectionFunction() {
	return new Function<Interval, PreferenceDirection>() {
	    @Override
	    public PreferenceDirection apply(Interval input) {
		return input.getPreferenceDirection();
	    }
	};
    }

    static public Map<Criterion, Interval> getScalesFromDirections(Map<Criterion, PreferenceDirection> directions) {
	return Maps.transformValues(directions, new Function<PreferenceDirection, Interval>() {
	    @Override
	    public Interval apply(PreferenceDirection input) {
		return DirectedIntervalImpl.newDirection(input);
	    }
	});
    }

    /**
     * Reports whether the given value is in the boundaries defined by the given scale.
     * 
     * @param scale
     *            not <code>null</code>.
     * @param value
     *            not a NaN, may be infinite.
     * @return <code>true</code> iff the given value is between the minimum and the maximum of the scale.
     */
    static public boolean inBoundaries(Interval scale, double value) {
	checkNotNull(scale);
	checkArgument(!Double.isNaN(value));
	return scale.getMinimum() <= value && value <= scale.getMaximum();
    }

    /**
     * Creates a new real interval, with no minimum or maximum bounds, representing the given preference direction.
     * 
     * @param direction
     *            <code>null</code> for not set.
     * @return a new interval.
     */
    static public Interval newDirection(PreferenceDirection direction) {
	return DirectedIntervalImpl.newDirection(direction);
    }

    /**
     * @param direction
     *            may be <code>null</code>.
     * @param minimum
     *            a real number, not infinite.
     * @param maximum
     *            a real number, or positive infinity.
     * @param stepSize
     *            a real number, not infinite.
     * @return a new interval representing the given data.
     */
    static public DiscreteInterval newDiscreteInterval(PreferenceDirection direction, double minimum,
	    double maximum, double stepSize) {
	return DirectedIntervalImpl.newDiscreteInterval(direction, minimum, maximum, stepSize).getAsDiscreteInterval();
    }

    /**
     * @param direction
     *            may be <code>null</code>.
     * @param minimum
     *            a real number, not infinite.
     * @param maximum
     *            a real number, not infinite.
     * @return a new interval representing the given data.
     */
    static public Interval newInterval(PreferenceDirection direction, double minimum, double maximum) {
	return DirectedIntervalImpl.newInterval(direction, minimum, maximum);
    }

    /**
     * Creates an interval with infinite lower and upper bounds, representing the whole set of real numbers, and with a
     * preference direction.
     * 
     * @return a new interval.
     */
    static public Interval newMaximizeDirection() {
	return DirectedIntervalImpl.newMaximizeDirection();
    }

    /**
     * Creates an interval with infinite lower and upper bounds, representing the whole set of real numbers, and with a
     * preference direction.
     * 
     * @return a new interval.
     */
    static public Interval newMinimizeDirection() {
	return DirectedIntervalImpl.newMinimizeDirection();
    }

    /**
     * Creates an interval with infinite lower and upper bounds, representing the whole set of real numbers, and without
     * a preference direction.
     * 
     * @return a new interval.
     */
    static public Interval newRealsInterval() {
	return DirectedIntervalImpl.newRealsInterval();
    }

    /**
     * Creates an interval with possibly infinite lower and upper bounds.
     * 
     * @param direction
     *            may be <code>null</code>.
     * @param minimum
     *            a real number, or negative infinity.
     * @param maximum
     *            a real number, or positive infinity.
     * 
     * @return a new interval.
     */
    static public Interval newUnrestrictedInterval(PreferenceDirection direction, double minimum,
	    double maximum) {
	return DirectedIntervalImpl.newUnrestrictedInterval(direction, minimum, maximum);
    }
}
