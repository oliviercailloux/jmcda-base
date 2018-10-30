package org.decisiondeck.jmcda.structure.sorting.problem.group_data;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataArray;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

public class GroupSortingDataArray extends SortingDataArray implements IGroupSortingData {

    /**
     * <code>null</code> when invalid.
     */
    private BiMap<DecisionMaker, Integer> m_idxDms;

    public GroupSortingDataArray(IGroupSortingData delegate) {
	super(delegate);
    }



    @Override
    public Set<DecisionMaker> getDms() {
	return delegate().getDms();
    }

    @Override
    protected IGroupSortingData delegate() {
	return (IGroupSortingData) super.delegate();
    }

    public int getDmIdx(DecisionMaker dm) {
	Preconditions.checkArgument(getDms().contains(dm));
	return idxDms().get(dm).intValue();
    }

    public Map<DecisionMaker, Integer> getDmsIndexes() {
	return Maps.newHashMap(idxDms());
    }

    private BiMap<DecisionMaker, Integer> idxDms() {
	m_idxDms = HashBiMap.create();
	int idx = 0;
	for (DecisionMaker dm : getDms()) {
	    m_idxDms.put(dm, Integer.valueOf(idx));
	    ++idx;
	}
	return m_idxDms;
    }

}
