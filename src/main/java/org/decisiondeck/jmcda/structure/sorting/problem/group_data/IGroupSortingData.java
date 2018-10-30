package org.decisiondeck.jmcda.structure.sorting.problem.group_data;

import java.util.Set;

import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decisiondeck.jmcda.structure.sorting.problem.data.ISortingData;

/**
 * <p>
 * An object representing the objective data of a typical group sorting problem: alternatives, profiles, criteria,
 * scales (co-domain) of the criteria, evaluations of alternatives according to criteria, categories to sort objects in.
 * Evaluations of alternatives on criteria are considered consensual, or objective for short, thus are shared by all
 * decision makers.
 * </p>
 * <p>
 * This object provides methods to check that it is in a consistent state. Note however that if its state are changed
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
 * <li>This object contains at least one decision maker.</li>
 * </ul>
 * </p>
 * <p>
 * Implementing objects may override this consistency definition provided that theirs is stronger, i.e., the object
 * being consistent considering its own definition must imply consistency considering this definition.
 * </p>
 * <p>
 * Read-only versions of this interface may be used where the setter methods throw {@link UnsupportedOperationException}
 * . Note that read-only does not imply immutable: the implementing class may be a read-only view of some mutable
 * delegate.
 * </p>
 * <p>
 * Implementing objects might want to impose further restrictions to the data this object accepts, e.g., to accept only
 * evaluations in some defined scale, or accept only all equal scales. Such objects will not be fully compliant with
 * this interface but it is suggested that a non accepted input throws as a result an
 * {@link UnsupportedOperationException}. Objects using this interface and wanting to capture such case might then
 * react, e.g., by wrapping this exception into an {@link org.decisiondeck.jmcda.exc.InvalidInputException}.
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
public interface IGroupSortingData extends ISortingData {

    /**
     * Retrieves a writeable view of the decision makers. When a decision maker is removed from the given set, all
     * informations bound to the given decision maker in this object is removed as well.
     * 
     * @return not <code>null</code>.
     */
    public Set<DecisionMaker> getDms();
}
