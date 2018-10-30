package org.decisiondeck.jmcda.structure.sorting.problem.group_data;

import java.util.Set;

import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataForwarder;

import com.google.common.base.Preconditions;

public class GroupSortingDataForwarder extends SortingDataForwarder implements IGroupSortingData {

    public GroupSortingDataForwarder(IGroupSortingData delegate) {
	super(delegate);
        Preconditions.checkNotNull(delegate);
    }

    @Override
    protected IGroupSortingData delegate() {
	return (IGroupSortingData) super.delegate();
    }



    @Override
    public Set<DecisionMaker> getDms() {
	return delegate().getDms();
    }

}
