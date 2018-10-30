package org.decisiondeck.jmcda.structure.sorting.problem.results;

import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.problem.group_results.IGroupSortingResults;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.SortingPreferencesViewGroupBacked;
import org.decisiondeck.xmcda_oo.structure.sorting.SortingProblemUtils;

import com.google.common.base.Preconditions;

/**
 * A writeable view that reads and writes through an {@link IGroupSortingResults} object. This object reads the
 * information from a given decision maker: reading the shared information is not supported as the assignments can't be
 * shared.
 * 
 * @author Olivier Cailloux
 * 
 */
public class SortingResultsViewGroupBacked extends SortingPreferencesViewGroupBacked implements ISortingResults {
    private final IGroupSortingResults m_delegate;

    /**
     * Retrieves the decision maker whose related informations this objects reads in the delegated group sorting object.
     * 
     * @return not <code>null</code>.
     */
    @Override
    public DecisionMaker getViewedDm() {
	return super.getViewedDm();
    }

    /**
     * 
     * This constructor creates a view that will read through the given group delegate, in particular it will see when
     * queried for the alternatives all the alternatives in that group data. If what is wanted instead is to observe
     * only the alternatives assigned by the given decision maker and forget the other ones, the factory method from
     * {@link SortingProblemUtils} might be preferred.
     * 
     * @param delegate
     *            not <code>null</code>.
     * @param dm
     *            not <code>null</code>.
     */
    public SortingResultsViewGroupBacked(IGroupSortingResults delegate, DecisionMaker dm) {
	super(delegate, dm);
	m_delegate = delegate;
    }

    @Override
    public IOrderedAssignments getAssignments() {
	final IOrderedAssignments assignments = m_delegate.getAssignments(getViewedDm());
	Preconditions.checkState(assignments != null);
	return assignments;
    }

    @Override
    public boolean hasCompleteAssignments() {
	/** NB Can't delegate because not equivalent. */
	return (getAssignments().getAlternatives().size() == getAlternatives().size());
    }

}
