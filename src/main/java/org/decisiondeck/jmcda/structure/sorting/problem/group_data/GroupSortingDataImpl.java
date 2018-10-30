package org.decisiondeck.jmcda.structure.sorting.problem.group_data;

import java.util.Set;

import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decisiondeck.jmcda.structure.sorting.problem.data.ISortingData;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataForwarder;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataImpl;

import com.google.common.collect.Sets;

public class GroupSortingDataImpl extends SortingDataForwarder implements IGroupSortingData {

    private final Set<DecisionMaker> m_dms = Sets.newLinkedHashSet();

    public GroupSortingDataImpl() {
	this(new SortingDataImpl());
    }

    /**
     * Decorates a {@link ISortingData} and adds group functionality to it. References to the delegate should
     * <em>not</em> be kept.
     * 
     * @param delegate
     *            not <code>null</code>.
     */
    public GroupSortingDataImpl(ISortingData delegate) {
	super(delegate);
    }

    @Override
    public Set<DecisionMaker> getDms() {
	return m_dms;
    }



}
