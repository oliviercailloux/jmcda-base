package org.decision_deck.jmcda.structure.interval;


/**
 * <p>
 * Represents an interval on the set of real numbers, directed if the preference direction is set. This is not
 * mandatory. The interval may end at infinity, thus the interval may also represent the whole set of reals.
 * </p>
 * <p>
 * The interval may be discrete in which case a step size <em>s</em> is defined and the minimum <em>m</em> must be non
 * infinite. The set of number this represents, supposing the maximum of this interval is infinite, is <em>m+k*s</em>
 * with <em>k</em> a positive integer or zero. If the maximum of this interval is defined as a real number <em>M</em>,
 * thus if it is not infinite, the set of numbers represented by this interval is <em>m+k*s</em> with <em>k ≥ 0</em> and
 * <em>m+k*s ≤ M</em>.
 * </p>
 * <p>
 * When associated with a criterion, such an interval is typically called a scale and represents the set of numbers an
 * evaluation of an alternative over that criterion must belong to. Except indicated otherwise, saying that a scale is
 * defined for a given criterion means that it has such an interval associated with it, but not necessarily that the
 * preference direction of this interval is defined. Therefore, saying that a scale is defined does not imply that the
 * criterion is indeed associated with a directed interval, only that it is associated with an interval.
 * </p>
 * <p>
 * Such an interval is sometimes called ordered interval in the javadoc for legacy reasons.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface Interval {

    /**
     * <p>
     * This interval must have a step size.
     * </p>
     * <p>
     * Retrieves a facade permitting easier usage of this interval when it represents a discrete interval.
     * </p>
     * 
     * @return not {@code null}.
     */
    public DiscreteInterval getAsDiscreteInterval();

    /**
     * <p>
     * The preference direction must be set.
     * </p>
     * <p>
     * Retrieves the maximum value if the preference direction is to maximize, the minimum value if the preference
     * direction is to minimize. If the corresponding bound is not set this method returns a positive or negative
     * infinity.
     * </p>
     * 
     * @return infinity or a real number.
     */
    public double getBest();

    /**
     * <p>
     * The preference direction must be set.
     * </p>
     * <p>
     * Useful for computations depending on the preference direction associated with this interval.
     * </p>
     * 
     * @return 1 if the preference direction is to maximize, -1 if it is to minimize.
     */
    public int getDirectionAsSign();

    /**
     * Retrieves the maximum value included in this interval, or positive infinity. The returned value is necessarily
     * greater than or equal to {@link #getMinimum()}.
     * 
     * @return {@link Double#POSITIVE_INFINITY} or a real number.
     */
    public double getMaximum();

    /**
     * Retrieves the minimum value included in this interval, or negative infinity. The returned value is necessarily
     * smaller than or equal to {@link #getMaximum()}.
     * 
     * @return {@link Double#NEGATIVE_INFINITY} or a real number.
     */
    public double getMinimum();

    /**
     * Retrieves the preference direction associated to this interval.
     * 
     * @return {@code null} for not set.
     */
    public PreferenceDirection getPreferenceDirection();

    /**
     * Retrieves the step size bound to this interval, or {@code null} if no step size is defined. If the step size
     * is defined, the minimum of this interval is a real, thus non-infinite, number.
     * 
     * @return {@code null} for no step size, or a number greater than zero.
     */
    public Double getStepSize();

    /**
     * <p>
     * The preference direction must be set.
     * </p>
     * <p>
     * Retrieves the minimum value if the preference direction is to maximize, the maximum value if the preference
     * direction is to minimize. If the corresponding bound is not set this method returns a positive or negative
     * infinity.
     * </p>
     * 
     * @return infinity or a real number.
     */
    public double getWorst();

}
