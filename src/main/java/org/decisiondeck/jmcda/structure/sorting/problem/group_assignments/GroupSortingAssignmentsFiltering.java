package org.decisiondeck.jmcda.structure.sorting.problem.group_assignments;

import java.util.Map;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.utils.collection.SetBackedMap;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsFiltering;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.GroupSortingDataFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.GroupSortingDataForwarder;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class GroupSortingAssignmentsFiltering extends GroupSortingDataForwarder implements IGroupSortingAssignments {

    private final IGroupSortingAssignments m_delegate;

    /**
     * Creates a read-only view of the given data.
     * 
     * @param delegate
     *            not <code>null</code>.
     */
    public GroupSortingAssignmentsFiltering(IGroupSortingAssignments delegate) {
	this(delegate, Predicates.<Alternative> alwaysTrue());
    }

    /**
     * Creates a view that only view the alternatives admitted by the given filter, or equivalently, that filters out
     * the alternatives that do not pass the given filter. This only concerns the real alternatives, the profiles are
     * untouched. To filter a constant set of alternatives, use {@link Predicates#in(java.util.Collection)}.
     * 
     * @param delegate
     *            not <code>null</code>.
     * @param filterAlternatives
     *            <code>null</code> to allow everything (equivalent to {@link Predicates#alwaysTrue()}.
     */
    public GroupSortingAssignmentsFiltering(IGroupSortingAssignments delegate, Predicate<Alternative> filterAlternatives) {
	super(new GroupSortingDataFiltering(delegate, filterAlternatives));
	m_delegate = delegate;
    }

    @Override
    protected GroupSortingDataFiltering delegate() {
	return (GroupSortingDataFiltering) super.delegate();
    }

    /**
     * @return <code>null</code> for everything allowed.
     */
    public Predicate<Alternative> getAlternativesFilter() {
	return delegate().getAlternativesFilter();
    }

    @Override
    public Map<DecisionMaker, IOrderedAssignments> getAssignments() {
	return new SetBackedMap<DecisionMaker, IOrderedAssignments>(super.getDms(),
		new Function<DecisionMaker, IOrderedAssignments>() {
		    @Override
		    public IOrderedAssignments apply(DecisionMaker input) {
			return getAssignments(input);
		    }
		});
    }

    @Override
    public IOrderedAssignments getAssignments(DecisionMaker dm) {
	final Predicate<Alternative> alternativesFilter = delegate().getAlternativesFilter();
	return AssignmentsUtils.getFakeWritable(new OrderedAssignmentsFiltering(m_delegate.getAssignments(dm),
		alternativesFilter == null ? Predicates.<Alternative> alwaysTrue() : alternativesFilter));
    }

    @Override
    public boolean hasCompleteAssignments() {
	for (DecisionMaker dm : getDms()) {
	    final IOrderedAssignments assignments = getAssignments(dm);
	    if (assignments.getAlternatives().size() < getAlternatives().size()) {
		return false;
	    }
	}
	return true;
    }

}
