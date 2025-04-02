package org.decisiondeck.jmcda.structure.sorting.problem.group_results;

import java.util.Map;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.utils.collection.SetBackedMap;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsFiltering;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesWithOrder;
import org.decisiondeck.jmcda.structure.sorting.problem.results.ISortingResults;
import org.decisiondeck.jmcda.structure.sorting.problem.results.SortingResultsViewGroupBacked;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;

/**
 * <p>
 * A group results object that provides ordering possibilities over its sets of criteria, alternatives, etc. The maps
 * (e.g. {@link #getAssignments()}) returned by this object are also iterated in the order determined by this object.
 * </p>
 * <p>
 * This object is a (partially) read-only view.
 * </p>
 * 
 * @see GroupSortingPreferencesWithOrder
 * 
 * @author Olivier Cailloux
 * 
 */
public class GroupSortingResultsWithOrder extends GroupSortingPreferencesWithOrder implements IGroupSortingResults {

    /**
     * Creates a new object delegating to the given data. All modifications to the delegate must go through this object.
     * The default order uses the natural ordering.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public GroupSortingResultsWithOrder(IGroupSortingResults delegate) {
	super(delegate);
    }

    @Override
    protected IGroupSortingResults delegate() {
	return (IGroupSortingResults) super.delegate();
    }

    @Override
    public Map<DecisionMaker, IOrderedAssignments> getAssignments() {
	return new SetBackedMap<DecisionMaker, IOrderedAssignments>(super.getDms(),
		new Function<DecisionMaker, IOrderedAssignments>() {
		    @Override
		    public IOrderedAssignments apply(DecisionMaker input) {
			return delegate().getAssignments(input);
		    }
		});
    }

    @Override
    public IOrderedAssignments getAssignments(DecisionMaker dm) {
	return AssignmentsUtils.getFakeWritable(new OrderedAssignmentsFiltering(delegate().getAssignments(dm),
		Predicates.<Alternative> alwaysTrue()));
    }

    @Override
    public boolean hasCompleteAssignments() {
	return delegate().hasCompleteAssignments();
    }

    @Override
    public ISortingResults getResults(DecisionMaker dm) {
	Preconditions.checkNotNull(dm);
	if (!getDms().contains(dm)) {
	    return null;
	}
	return new SortingResultsViewGroupBacked(this, dm);
    }
}
