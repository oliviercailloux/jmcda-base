package org.decision_deck.jmcda.structure.thresholds;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Criterion;

/**
 * <p>
 * This object holds preference, indifference, vetoes thresholds, associated to criteria. The threshold values are
 * represented as numbers.
 * </p>
 * <p>
 * Note that objects implementing this interface may be read-only, in which case the methods permitting to modify this
 * object's state will throw {@link UnsupportedOperationException} when invoked.
 * </p>
 * <p>
 * NB no need to accept null thresholds. The only reason is to have criteria without thresholds, but would be better to
 * separate the thresholds data and the criteria data.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface Thresholds {

    /**
     * Retrieves a view of the set of criteria contained in this object, i.e., each criterion to which at least one
     * threshold value is bound.
     * 
     * @return not {@code null}.
     */
    public Set<Criterion> getCriteria();

    public boolean isEmpty();

    /**
     * Retrieves the veto threshold that has been set for the given criterion. The criterion must have a veto threshold.
     * 
     * @param criterion
     *            not {@code null}.
     * @return the value of the threshold.
     * @see #containsVetoThreshold(Criterion)
     */
    public double getVetoThreshold(Criterion criterion);

    public boolean containsVetoThreshold(Criterion criterion);

    /**
     * Binds a threshold to the given criterion.
     * 
     * @param criterion
     *            not {@code null}.
     * @param threshold
     *            a real number.
     * @return the value of the threshold that was previously bound to the given criterion, or {@code null} iff
     *         there was no such threshold for this criterion.
     */
    public Double setPreferenceThreshold(Criterion criterion, double threshold);

    /**
     * Retrieves a view of the veto thresholds. The view is read-only iff this object is read-only. Setting the returned
     * map has the same effect as setting this object. The map does not accept {@code null} key or value.
     * 
     * @return not {@code null}.
     */
    public Map<Criterion, Double> getVetoThresholds();

    /**
     * Retrieves a view of the indifference thresholds. The view is read-only iff this object is read-only. Setting the
     * returned map has the same effect as setting this object. The map does not accept {@code null} key or value.
     * 
     * @return not {@code null}.
     */
    public Map<Criterion, Double> getIndifferenceThresholds();

    /**
     * Retrieves a view of the preference thresholds. The view is read-only iff this object is read-only. Setting the
     * returned map has the same effect as setting this object. The map does not accept {@code null} key or value.
     * 
     * @return not {@code null}.
     */
    public Map<Criterion, Double> getPreferenceThresholds();

    public boolean containsPreferenceThreshold(Criterion criterion);

    public boolean containsIndifferenceThreshold(Criterion criterion);

    /**
     * Retrieves the indifference threshold that has been set for the given criterion. The criterion must have an
     * indifference threshold.
     * 
     * @param criterion
     *            not {@code null}.
     * @return the value of the threshold.
     * @see #containsIndifferenceThreshold(Criterion)
     */
    public double getIndifferenceThreshold(Criterion criterion);

    /**
     * Retrieves the preference threshold that has been set for the given criterion. The criterion must have a
     * preference threshold.
     * 
     * @param criterion
     *            not {@code null}.
     * @return the value of the threshold.
     * @see #containsPreferenceThreshold(Criterion)
     */
    public double getPreferenceThreshold(Criterion criterion);

    /**
     * Binds a threshold to the given criterion.
     * 
     * @param criterion
     *            not {@code null}.
     * @param threshold
     *            a real number.
     * @return the value of the threshold that was previously bound to the given criterion, or {@code null} iff
     *         there was no such threshold for this criterion.
     */
    public Double setIndifferenceThreshold(Criterion criterion, double threshold);


    /**
     * Binds a threshold to the given criterion.
     * 
     * @param criterion
     *            not {@code null}.
     * @param threshold
     *            a real number.
     * @return the value of the threshold that was previously bound to the given criterion, or {@code null} iff
     *         there was no such threshold for this criterion.
     */
    public Double setVetoThreshold(Criterion criterion, double threshold);

}
