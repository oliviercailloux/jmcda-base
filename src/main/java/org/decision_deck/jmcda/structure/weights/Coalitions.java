package org.decision_deck.jmcda.structure.weights;

import java.util.Set;

import org.decision_deck.jmcda.structure.Criterion;

/**
 * A set of weights (bound to criteria) and a majority threshold. The weights and the majority threshold are not
 * requested to be between 0 and 1 because some users may whish to use non normalized weights. They must however be
 * positive or zero. This represent criteria satisfying coalitions (or sometimes winning coalitions), where a coalition
 * is a set of criteria whose sum of weight is at least the majority threshold.</p>
 * <p>
 * This interface accepts positive or zero values for weights and for the majority threshold, thus excluding infinity or
 * NaN. This permits to ease manipulation of valid inequalities on combinations of the weights and the threshold.
 * </p>
 * <p>
 * Usage note: it is recommanded, to avoid numerical imprecision problems, to lower a bit the majority threshold, if it
 * holds that no subset of weights that should not be a winning coalition sum to a value just lower to the current
 * majority threshold, and if it holds that some subset of weights that should be a winning coalition sum to a value
 * just equal to or above the current majority threshold. For example, if trying to represent a set of four weights =
 * 1/4 and a majority threshold of 1/2, it may be a good idea to rather use a majority threshold of 0.449. This avoids
 * the sum of two weights 1/4 + 1/4 being less than the majority threshold of 1/2 because of a numerical imprecision
 * introduced during the computation. This strategy is also useful to avoid situations where the sum of all weights is
 * just below the majority threshold because of numerical imprecision.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface Coalitions {

    /**
     * Two coalitions are equal iff they contain the same weight values for the same criteria and the same value of
     * majority threshold (or both do not contain majority thresholds).
     * 
     */
    @Override
    public boolean equals(Object obj);

    /**
     * Retrieves a view of the set of criteria on which weights are defined. Removal from the returned set might be
     * supported in the future.
     * 
     * @return not <code>null</code>.
     */
    public Set<Criterion> getCriteria();

    /**
     * The majority threshold must be defined.
     * 
     * @return a number greater than or equal to zero.
     * @see #containsMajorityThreshold()
     */
    public double getMajorityThreshold();

    public boolean containsMajorityThreshold();

    /**
     * Retrieves the weight bound to the given criterion. The criterion must be have a weight.
     * 
     * @param criterion
     *            not <code>null</code>.
     * @return a number greater than or equal to zero.
     * @see #getCriteria()
     */
    public double getWeight(Criterion criterion);

    /**
     * Removes the weight bound to the given criterion, if there was such a weight.
     * 
     * @param criterion
     *            not <code>null</code>.
     * @return the weight previously associated to that criterion, or <code>null</code> iff this method call had no
     *         effect.
     */
    public Double removeWeight(Criterion criterion);

    /**
     * Sets the weight associated to the given criterion.
     * 
     * @param criterion
     *            not <code>null</code>.
     * @param weight
     *            a positive or zero value.
     * @return the weight that was previously associated to that criterion (positive or zero), or <code>null</code> if
     *         it was not set.
     */
    public Double putWeight(Criterion criterion, double weight);

    /**
     * Sets or replaces the majority threshold.
     * 
     * @param majorityThreshold
     *            a positive or zero value.
     * @return the previous majority threshold value, or <code>null</code> iff it was not set.
     */
    public Double setMajorityThreshold(double majorityThreshold);

    /**
     * Removes the possibly associated majority threshold. If not set, this method has no effect.
     * 
     * @return the previous value of the majority threshold, or <code>null</code> iff it was not set.
     */
    public Double removeMajorityThreshold();

    /**
     * Retrieves a view that reads and writes through to this object.
     * 
     * @return not <code>null</code>.
     */
    public Weights getWeights();

    boolean approxEquals(Coalitions c2, double tolerance);

    /**
     * Tests whether this object contains no information.
     * 
     * @return <code>true</code> iff this object contains no weights and no majority threshold.
     */
    public boolean isEmpty();

}
