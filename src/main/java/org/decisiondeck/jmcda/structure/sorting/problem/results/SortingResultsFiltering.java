package org.decisiondeck.jmcda.structure.sorting.problem.results;

import static com.google.common.base.Preconditions.checkNotNull;

import org.decision_deck.jmcda.structure.Alternative;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsFiltering;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.SortingPreferencesFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.SortingPreferencesForwarder;

import com.google.common.base.Predicate;

/**
 * <p>
 * A read-only view. Restricting to a subset of alternatives is possible when the object is created.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class SortingResultsFiltering extends SortingPreferencesForwarder implements ISortingResults {

    private final ISortingResults m_delegate;
    private final boolean m_alsoRestrictAssignments;

    /**
     * Creates a read-only view of the given delegate.
     * 
     * @param delegate
     *            not <code>null</code>.
     */
    public SortingResultsFiltering(ISortingResults delegate) {
	super(new SortingPreferencesFiltering(delegate));
	m_delegate = delegate;
	m_alsoRestrictAssignments = false;
    }

    /**
     * @param delegate
     *            not <code>null</code>.
     * @param filterAlternatives
     *            not <code>null</code>.
     * @param alsoFilterAssignments
     *            <code>true</code> to apply the filter to the assignments contained in this object. Set this to
     *            <code>false</code> if the predicate uses the assignments to determine if an alternative is filtered,
     *            otherwize an infinite recursion will occur (filter defined according to the assignments which require
     *            the filter to be determined).
     */
    public SortingResultsFiltering(ISortingResults delegate, Predicate<Alternative> filterAlternatives,
	    boolean alsoFilterAssignments) {
	super(new SortingPreferencesFiltering(delegate, filterAlternatives, null));
	checkNotNull(filterAlternatives);
	m_delegate = delegate;
	m_alsoRestrictAssignments = alsoFilterAssignments;
    }

    @Override
    public IOrderedAssignments getAssignments() {
	if (m_alsoRestrictAssignments) {
	    final Predicate<Alternative> alternativesFilter = getAlternativesFilter();
	    if (alternativesFilter == null) {
		return AssignmentsUtils.getFakeWritable(new OrderedAssignmentsFiltering(m_delegate.getAssignments()));
	    }
	    return AssignmentsUtils.getFakeWritable(new OrderedAssignmentsFiltering(m_delegate.getAssignments(),
		    alternativesFilter));
	}
	return AssignmentsUtils.getFakeWritable(AssignmentsUtils.getReadView(m_delegate.getAssignments()));
    }

    @Override
    public boolean hasCompleteAssignments() {
	return (getAssignments().getAlternatives().size() == getAlternatives().size());
    }

    @Override
    protected SortingPreferencesFiltering delegate() {
	return (SortingPreferencesFiltering) super.delegate();
    }

    /**
     * @return <code>null</code> for allow everything.
     */
    public Predicate<Alternative> getAlternativesFilter() {
	return delegate().getAlternativesPredicate();
    }

}
