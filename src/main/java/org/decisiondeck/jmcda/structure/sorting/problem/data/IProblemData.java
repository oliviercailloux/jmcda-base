package org.decisiondeck.jmcda.structure.sorting.problem.data;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.interval.Interval;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;

/**
 * <p>
 * An object representing the objective data of a typical MCDA problem:
 * <ul>
 * <li>Alternatives.</li>
 * <li>Criteria.</li>
 * <li>Alternative evaluations.</li>
 * <li>Evaluation scales for the criteria.</li>
 * </ul>
 * </p>
 * <p>
 * This object provides methods to check that it is in a consistent state. Note however that if its state is changed
 * after a check for consistency, the consistency is not guaranteed any more.
 * </p>
 * <p>
 * This object is consistent iff all the following holds.
 * <ul>
 * <li>All the alternatives given by {@link #getAlternatives()} are evaluated on all the criteria given by
 * {@link #getCriteria()}. Thus the evaluation is complete.</li>
 * <li>All criteria have a preference direction set.</li>
 * </ul>
 * </p>
 * <p>
 * Read-only versions of this interface may be used where the setter methods throw {@link UnsupportedOperationException}
 * . Note that read-only does not imply immutable: the implementing class may be a read-only view of some mutable
 * delegate.
 * </p>
 * <p>
 * Implementing objects might want to impose further restrictions to the data this object accepts, e.g., to accept only
 * evaluations in the right scale, or accept only all equal scales. Such objects will not be fully compliant with this
 * interface but it is suggested that a non accepted input throw as a result an {@link UnsupportedOperationException}.
 * Objects using this interface and wanting to capture such case might then react, e.g., by wrapping this exception into
 * an {@link org.decisiondeck.jmcda.exc.InvalidInputException}.
 * </p>
 * <p>
 * Objects implementing this interface are forbidden to implement an auto-remove behavior, that is, once a criterion
 * (e.g.) has been added to the data, e.g. through addition of a new scale, deletion of that scale does not
 * automatically remove the criterion from the data, even though no information is linked to that criterion any more.
 * Such a behavior would generally be difficult to implement efficiently, would be most of the time useless, and would
 * render this interface difficult to understand e.g. when setting manually the sets of criteria (the user would not
 * want these criteria to be auto-removed).
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public interface IProblemData {

    /**
     * <p>
     * Retrieves a read-only view of the evaluations of the alternatives. The set of alternatives on which evaluations
     * are provided is a subset of the set returned by {@link #getAlternatives()}. The set of criteria on which
     * evaluations are provided is a subset of the set returned by {@link #getCriteria()}.
     * </p>
     * 
     * @return not <code>null</code>.
     * 
     */
    public EvaluationsRead getAlternativesEvaluations();

    /**
     * Retrieves a writable view of the criteria.
     * 
     * @return not <code>null</code>.
     */
    public Set<Criterion> getCriteria();

    /**
     * Retrieves a writable view of the alternatives.
     * 
     * @return not <code>null</code>.
     */
    public Set<Alternative> getAlternatives();

    /**
     * Retrieves a read-only view of the scales. Scales are defined on all criteria in this object: adding a criterion
     * to this object automatically associates it with a scale representing the set of real numbers.
     * 
     * @return not <code>null</code>, no <code>null</code> key or value.
     */
    public Map<Criterion, Interval> getScales();

    /**
     * Sets, replaces, or removes the evaluation of the given alternative according to the given criterion. The given
     * alternative and criterion are added to this object if necessary.
     * 
     * @param alternative
     *            not <code>null</code>.
     * @param criterion
     *            not <code>null</code>.
     * @param value
     *            <code>null</code> to remove a possibly previously associated value.
     * @return <code>true</code> iff this call changed the data contained in this object.
     */
    public boolean setEvaluation(Alternative alternative, Criterion criterion, Double value);

    /**
     * Sets replaces, or remove the evaluations of the alternatives. The given information is added to any possibly
     * existing information. The set of alternatives and criteria on which the given evaluations are defined are added,
     * if not already existing, to this object.
     * 
     * @param evaluations
     *            if <code>null</code>, the possibly existing evaluations will be removed.
     * @return <code>true</code> iff this call changed this object.
     */
    public boolean setEvaluations(EvaluationsRead evaluations);

    /**
     * <p>
     * Sets, replaces, or removes the scale of the given criterion. The criterion is added to this object if it is not
     * known already.
     * </p>
     * 
     * @param criterion
     *            not <code>null</code>.
     * @param scale
     *            <code>null</code> to remove a possibly previously associated scale.
     * @return <code>true</code> iff this call changed the data contained in this object.
     */
    public boolean setScale(Criterion criterion, Interval scale);
}
