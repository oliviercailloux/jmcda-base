package org.decision_deck.jmcda.structure.interval.mess;

import org.decision_deck.jmcda.structure.interval.Interval;

import com.google.common.base.Preconditions;

/**
 * A facade to an {@link Interval} permitting easier use when the interval only contains integer numbers.
 * 
 * @author Olivier Cailloux
 * 
 */
public class OrderedIntervalInteger {

    @SuppressWarnings("unused")
    private final Interval m_delegate;

    /**
     * @param delegate
     *            not {@code null}, must have a step size.
     */
    public OrderedIntervalInteger(Interval delegate) {
	Preconditions.checkNotNull(delegate);
	if ((int) delegate.getMaximum() != delegate.getMaximum()) {
	    throw new IllegalArgumentException("Delegate has a non integer maximum value.");
	}
	if ((int) delegate.getMinimum() != delegate.getMinimum()) {
	    throw new IllegalArgumentException("Delegate has a non integer minimum value.");
	}
	if ((int) delegate.getStepSize().doubleValue() != delegate.getStepSize().doubleValue()) {
	    throw new IllegalArgumentException("Delegate has a non integer step size value.");
	}
	m_delegate = delegate;
    }

}
