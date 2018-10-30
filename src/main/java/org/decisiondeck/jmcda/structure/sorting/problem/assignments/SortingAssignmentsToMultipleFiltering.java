package org.decisiondeck.jmcda.structure.sorting.problem.assignments;

import static com.google.common.base.Preconditions.checkNotNull;

import org.decision_deck.jmcda.structure.Alternative;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsToMultipleFiltering;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataForwarder;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class SortingAssignmentsToMultipleFiltering extends SortingDataForwarder implements
	ISortingAssignmentsToMultiple {

    private final ISortingAssignmentsToMultiple m_delegate;
    private final boolean m_alsoRestrictAssignments;

    /**
     * Creates a read-only view of the given data.
     * 
     * @param delegate
     *            not <code>null</code>.
     */
    public SortingAssignmentsToMultipleFiltering(ISortingAssignmentsToMultiple delegate) {
	this(delegate, Predicates.<Alternative> alwaysTrue(), false);
    }

    /**
     * Creates a view that only view the alternatives admitted by the given filter, or equivalently, that filters out
     * the alternatives that do not pass the given filter. This only concerns the real alternatives, the profiles are
     * untouched. To filter a constant set of alternatives, use {@link Predicates#in(java.util.Collection)}.
     * 
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
    public SortingAssignmentsToMultipleFiltering(ISortingAssignmentsToMultiple delegate,
	    Predicate<Alternative> filterAlternatives,
	    boolean alsoFilterAssignments) {
	super(new SortingDataFiltering(delegate, filterAlternatives, null));
	checkNotNull(filterAlternatives);
	m_delegate = delegate;
	m_alsoRestrictAssignments = alsoFilterAssignments;
    }

    @Override
    protected SortingDataFiltering delegate() {
	return (SortingDataFiltering) super.delegate();
    }

    /**
     * @return <code>null</code> for everything allowed.
     */
    public Predicate<Alternative> getAlternativesFilter() {
	return delegate().getAlternativesPredicate();
    }

    @Override
    public IOrderedAssignmentsToMultiple getAssignments() {
	if (m_alsoRestrictAssignments) {
	    final Predicate<Alternative> alternativesFilter = getAlternativesFilter();
	    return AssignmentsUtils.getFakeWritableToMultiple(new OrderedAssignmentsToMultipleFiltering(m_delegate
		    .getAssignments(),
 alternativesFilter == null ? Predicates.<Alternative> alwaysTrue()
		    : alternativesFilter));
	}
	return AssignmentsUtils.getFakeWritableToMultiple(AssignmentsUtils.getReadView(m_delegate.getAssignments()));
    }

    @Override
    public boolean hasCompleteAssignments() {
	return (getAssignments().getAlternatives().size() == getAlternatives().size());
    }

}
