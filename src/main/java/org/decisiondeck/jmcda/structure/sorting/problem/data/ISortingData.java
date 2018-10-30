package org.decisiondeck.jmcda.structure.sorting.problem.data;

import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;

/**
 * <p>
 * An object representing the objective data of a typical sorting problem:
 * <ul>
 * <li>Alternatives.</li>
 * <li>Criteria.</li>
 * <li>Alternative evaluations.</li>
 * <li>Categories and profiles.</li>
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
 * <li>The categories are all linked to up and down profiles, except the worst and best one having no down and up
 * (respectively) profiles. At least one category is defined.</li>
 * <li>The set of profiles corresponding to the up or down profile of a category equals (independently of the order) the
 * set of profiles given by {@link #getProfiles()}.</li>
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
public interface ISortingData extends IProblemData {
    /**
     * @return a read-only view of all alternatives (i.e., alternatives and profiles). The returned set is the union of
     *         the alternatives and the profiles sets.
     */
    public Set<Alternative> getAllAlternatives();

    /**
     * {@inheritDoc}
     * <p>
     * The returned evaluations correspond to the alternatives in this sorting problem, excluding the profiles.
     * </p>
     * 
     */
    @Override
    public EvaluationsRead getAlternativesEvaluations();

    /**
     * Retrieves a writable view of the categories and profiles in this object. The profiles associated with the
     * returned categories constitute a (possibly empty) subset of the profiles given by {@link #getProfiles()}. Adding
     * a profile to the returned object adds it to this object as well. Removing a profile from the returned object does
     * not remove the profile from the set of profiles contained in this object and retrieved by {@link #getProfiles()}.
     * 
     * @return not <code>null</code>.
     */
    public CatsAndProfs getCatsAndProfs();

    /**
     * @return a writable view of the profiles, excluding the alternatives.
     */
    public Set<Alternative> getProfiles();

    /**
     * {@inheritDoc}
     * <p>
     * The returned set do not contain the profiles.
     * </p>
     * 
     * @see #getAllAlternatives()
     */
    @Override
    public Set<Alternative> getAlternatives();
}
