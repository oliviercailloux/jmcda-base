package org.decision_deck.jmcda.structure.interval;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;

/**
 * A facade to an {@link Interval} permitting easier use when the interval is discrete.
 * 
 * @author Olivier Cailloux
 * 
 */
public class DiscreteInterval implements Interval {

    private final Interval m_delegate;

    /**
     * @param delegate
     *            not <code>null</code>, must represent a discrete interval thus must have a step size defined (which
     *            also implies a non infinite minimum).
     */
    public DiscreteInterval(Interval delegate) {
	checkNotNull(delegate);
	checkArgument(delegate.getStepSize() != null);
	assert (!Double.isInfinite(delegate.getMinimum()));
	m_delegate = delegate;
    }

    /**
     * Retrieves the value belonging to this discrete interval which is the closest to the given value. If the given
     * value is smaller than the minimum value allowed in this interval, the minimum value is returned. Similarily for
     * the maximum value.
     * 
     * @param value
     *            the value to target.
     * @param roundToCeiling
     *            in case the given value is exactly between two steps, and this is <code>true</code>, the returned
     *            value will be the highest step, if this is <code>false</code>, the smallest one will be returned.
     * @return a value in this interval.
     */
    public double getClosest(double value, boolean roundToCeiling) {
	if (value <= getMinimum()) {
	    return getMinimum();
	}
	if (value >= getMaximum()) {
	    return getMaximum();
	}
	final double valueF = value - getMinimum();
	final double stepSize = getStepSize().doubleValue();
	final double nbStepsExact = valueF / stepSize;
	final double nbStepsLow = Math.floor(nbStepsExact);
	final double nbStepsHigh = Math.ceil(nbStepsExact);
	final double errorFromBelow = nbStepsExact - nbStepsLow;
	final double errorFromAbove = nbStepsHigh - nbStepsExact;
	final double found;
	if (errorFromBelow < errorFromAbove || (errorFromBelow == errorFromAbove && !roundToCeiling)) {
	    found = (stepSize * nbStepsLow) + getMinimum();
	} else {
	    found = (stepSize * nbStepsHigh) + getMinimum();
	}
	return found;
    }

    @Override
    public DiscreteInterval getAsDiscreteInterval() {
	return this;
    }

    @Override
    public double getBest() {
	return m_delegate.getBest();
    }

    @Override
    public int getDirectionAsSign() {
	return m_delegate.getDirectionAsSign();
    }

    @Override
    public double getMaximum() {
	return m_delegate.getMaximum();
    }

    /**
     * Retrieves the minimum value included in this interval. The returned value is necessarily smaller than or equal to
     * {@link #getMaximum()} and may not be infinity.
     * 
     * @return a real number.
     */
    @Override
    public double getMinimum() {
	return m_delegate.getMinimum();
    }

    @Override
    public PreferenceDirection getPreferenceDirection() {
	return m_delegate.getPreferenceDirection();
    }

    /**
     * Retrieves the step size.
     * 
     * @return not <code>null</code>.
     */
    @Override
    public Double getStepSize() {
	return m_delegate.getStepSize();
    }

    @Override
    public double getWorst() {
	return m_delegate.getWorst();
    }

    /**
     * <p>
     * The maximum must be a non infinite number.
     * </p>
     * <p>
     * Retrieves the number of steps this discrete interval accepts. This is necessarily at least one. For example, the
     * number of steps accepted by an interval from 10 to 21 by steps of 5 is three: the step 10, the step 15, the step
     * 20.
     * </p>
     * 
     * @return at least one.
     */
    public int getNbSteps() {
	Preconditions.checkState(!Double.isInfinite(getMaximum()));
	final double div = (getMaximum() - getMinimum()) / getStepSize().doubleValue();
	return (int) (Math.floor(div)) + 1;
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
    public int hashCode() {
	return Intervals.getDirectedIntervalsEquivalenceRelation().hash(this);
    }

    /**
     * Retrieves the step size bound to this discrete interval, which is the value returned by {@link #getStepSize()}
     * with the supplementary guarantee that it is non <code>null</code>.
     * 
     * @return a number greater than zero.
     */
    public double getNonNullStepSize() {
	return m_delegate.getStepSize().doubleValue();
    }

    public boolean contains(double value) {
	return getMinimum() <= value && value <= getMaximum() && (value - getMinimum()) % getNonNullStepSize() == 0;
    }
}
