package org.decisiondeck.jmcda.structure.sorting.problem.results;

import static com.google.common.base.Preconditions.checkNotNull;

import org.decision_deck.jmcda.structure.Alternative;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsFiltering;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsToMultipleFiltering;
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
public class SortingResultsToMultipleFiltering extends SortingPreferencesForwarder implements ISortingResultsToMultiple {

    private final ISortingResultsToMultiple m_delegateToMultiple;
    private final boolean m_alsoRestrictAssignments;
    private final ISortingResults m_delegate;

    /**
     * Creates a new read-only view of the given delegate.
     * 
     * @param delegate
     *            not <code>null</code>.
     */
    public SortingResultsToMultipleFiltering(ISortingResultsToMultiple delegate) {
	super(new SortingPreferencesFiltering(delegate));
	m_delegate = null;
	m_delegateToMultiple = delegate;
	m_alsoRestrictAssignments = false;
    }


    /**
     * Creates a new read-only view of the given delegate.
     * 
     * @param delegate
     *            not <code>null</code>.
     */
   public SortingResultsToMultipleFiltering(ISortingResults delegate) {
	super(new SortingPreferencesFiltering(delegate));
	m_delegate = delegate;
	m_delegateToMultiple = null;
	m_alsoRestrictAssignments = false;
   }

    /**
     * @param delegate
     *            not <code>null</code>.
     * @param filterAlternatives
     *            not <code>null</code>.
     * @param alsoRestrictAssignments
     *            <code>true</code> to apply the restriction to the assignments contained in this object. Set this to
     *            <code>false</code> if the predicate uses the assignments to determine if an alternative is filtered,
     *            otherwize an infinite recursion will occur (filter defined according to the assignments which require
     *            the filter to be determined).
     */
    public SortingResultsToMultipleFiltering(ISortingResultsToMultiple delegate,
	    Predicate<Alternative> filterAlternatives, boolean alsoRestrictAssignments) {
	super(new SortingPreferencesFiltering(delegate, filterAlternatives, null));
	checkNotNull(filterAlternatives);
	m_delegate = null;
	m_delegateToMultiple = delegate;
	m_alsoRestrictAssignments = alsoRestrictAssignments;
    }

    /**
     * @param delegate
     *            not <code>null</code>.
     * @param filterAlternatives
     *            not <code>null</code>.
     * @param alsoRestrictAssignments
     *            <code>true</code> to apply the restriction to the assignments contained in this object. Set this to
     *            <code>false</code> if the predicate uses the assignments to determine if an alternative is filtered,
     *            otherwize an infinite recursion will occur (filter defined according to the assignments which require
     *            the filter to be determined).
     */
    public SortingResultsToMultipleFiltering(ISortingResults delegate,
	    Predicate<Alternative> filterAlternatives, boolean alsoRestrictAssignments) {
	super(new SortingPreferencesFiltering(delegate, filterAlternatives, null));
	checkNotNull(filterAlternatives);
	m_delegate = delegate;
	m_delegateToMultiple = null;
	m_alsoRestrictAssignments = alsoRestrictAssignments;
    }

    @Override
    public IOrderedAssignmentsToMultiple getAssignments() {
	if (m_alsoRestrictAssignments) {
	    final Predicate<Alternative> alternativesFilter = getAlternativesFilter();
	    if (m_delegate != null) {
		if (alternativesFilter == null) {
		    return AssignmentsUtils.getFakeWritableToMultiple(new OrderedAssignmentsFiltering(m_delegate
		    	.getAssignments()));
		}
		return AssignmentsUtils.getFakeWritableToMultiple(new OrderedAssignmentsFiltering(m_delegate
			.getAssignments(), alternativesFilter));
	    }
	    if (alternativesFilter == null) {
		return AssignmentsUtils.getFakeWritableToMultiple(new OrderedAssignmentsToMultipleFiltering(
		    m_delegateToMultiple.getAssignments()));
	    }
	    return AssignmentsUtils.getFakeWritableToMultiple(new OrderedAssignmentsToMultipleFiltering(
		    m_delegateToMultiple.getAssignments(), alternativesFilter));
	}
	if (m_delegate != null) {
	    return AssignmentsUtils
		    .getFakeWritableToMultiple(AssignmentsUtils.getReadView(m_delegate.getAssignments()));
	}
	return AssignmentsUtils.getFakeWritableToMultiple(AssignmentsUtils.getReadView(m_delegateToMultiple
		.getAssignments()));
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
