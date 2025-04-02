package org.decision_deck.jmcda.structure.weights;

import java.util.Map;

import org.decision_deck.jmcda.structure.Criterion;

/**
 * <p>
 * A set of criteria weights (retrievable by their respective criterion). This object allows to check whether this set
 * of weights is normalized, and to retrieve the equivalent set after normalization.
 * </p>
 * <p>
 * This map never contains {@code null} key or values.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface Weights extends Map<Criterion, Double> {
    public boolean approxEquals(Weights w2, double tolerance);

    /**
     * <p>
     * A weight instance equals a map of criterion and double values iff they contain the same weight mappings, thus the
     * same values for the same criteria.
     * </p>
     * <p>
     * This definition of equals is a consequence of this object being a Map.
     * </p>
     */
    @Override
    public boolean equals(Object obj);

    /**
     * <p>
     * Provides a read-only view of these weight values, normalized, thus where each weight value is divided by the sum
     * of the weights.
     * </p>
     * <p>
     * The returned object shares its tolerance value with this object.
     * </p>
     * <p>
     * Note that the returned object does not necessarily give weight values that sum exactly to one, because of the
     * precision error. It is unclear whether it is possible to guarantee that the returned object provides a sum of
     * weights which is not further to one than this object provided sum of weights. This implication maybe holds, but
     * we are unsure. If you are knowledgable in IEEE double precision properties, please let us know.
     * </p>
     * <p>
     * <b>Warning:</b> this is currently not implemented as a view. The returned object is only valid as long as this
     * object' state does not change. Copy the returned object into a new structure or use it immediately.
     * </p>
     * 
     * 
     * @return a set of normalized weights.
     */
    public Weights getNormalized();

    /**
     * Retrieves the sum of the weights this object contains.
     * 
     * @return a number greater than or equal to zero.
     */
    public double getSum();

    /**
     * Retrieves the weight bound to the given criterion. The criterion must have a weight, or equivalently, must be in
     * the key set of this map.
     * 
     * @param criterion
     *            not {@code null}.
     * @return a number greater than or equal to zero.
     * @see #keySet()
     */
    public double getWeightBetter(Criterion criterion);

    /**
     * Associates the given weight to the given criterion. This method is equivalent to the
     * {@link #put(Criterion, Double)} method, but this one makes it clear that {@code null} values are not
     * accepted.
     * 
     * @param criterion
     *            the criterion to consider. Not {@code null}.
     * @param weight
     *            the weight to associate to the given criterion.
     * @return the weight that was previously associated to that criterion, or {@code null} if there was no.
     * @see #put(Criterion, Double)
     */
    public Double putWeight(Criterion criterion, double weight);
}
