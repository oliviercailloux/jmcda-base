package org.decisiondeck.jmcda.structure.sorting.problem.data;

import java.util.Map;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.sorting.category.Category;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;

/**
 * <p>
 * Associates with the data (alternatives, categories, criteria, profiles) indexes from zero. Permits to use the data in
 * an array mode. When the data changes through this object, the arrays are automatically updated. Indexes are
 * attributed in the order the objects are returned by the underlying data object. This implies that adding a new object
 * (e.g. an alternative) may change indexes already attributed to some other objects of the same type (e.g.
 * alternatives). Generally speaking, the user should not make assumptions on the stability of indexes when the
 * underlying data changes. Stability of indexes is guaranteed only as long as the data does not change.
 * </p>
 * <p>
 * Note that no writing should be done to the delegate data directly (bypassing this object), as this object has no
 * means of detecting such changes and update its indexes accordingly. Best usage is either to use an immutable delegate
 * or to drop all external references to the delegate right after creation of this object and modify the data through
 * this object exclusively.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class SortingDataArray extends SortingDataForwarder implements ISortingData {

    /**
     * @return a copy, possibly immutable. Not <code>null</code>.
     */
    public Map<Category, Integer> getCategoriesIndexes() {
	return ImmutableMap.copyOf(idxCategories());
    }

    /**
     * <code>null</code> when invalid.
     */
    private BiMap<Category, Integer> m_idxCategories;
    /**
     * <code>null</code> when invalid.
     */
    private BiMap<Criterion, Integer> m_idxCriteria;
    /**
     * <code>null</code> when invalid.
     */
    private BiMap<Alternative, Integer> m_idxProfiles;
    /**
     * <code>null</code> when invalid.
     */
    private BiMap<Alternative, Integer> m_idxAlternatives;

    public SortingDataArray(ISortingData delegate) {
	super(delegate);
	m_idxAlternatives = null;
	m_idxCategories = null;
	m_idxCriteria = null;
	m_idxProfiles = null;
    }

    private BiMap<Alternative, Integer> idxAlternatives() {
	// if (m_idxAlternatives != null) {
	// return m_idxAlternatives;
	// }
	m_idxAlternatives = HashBiMap.create();
	int idx = 0;
	for (Alternative alternative : getAlternatives()) {
	    m_idxAlternatives.put(alternative, Integer.valueOf(idx));
	    ++idx;
	}
	return m_idxAlternatives;
    }

    private BiMap<Category, Integer> idxCategories() {
	m_idxCategories = HashBiMap.create();
	int idx = 0;
	for (Category category : getCatsAndProfs().getCategories()) {
	    m_idxCategories.put(category, Integer.valueOf(idx));
	    ++idx;
	}
	return m_idxCategories;
    }

    private BiMap<Criterion, Integer> idxCriteria() {
	m_idxCriteria = HashBiMap.create();
	int idx = 0;
	for (Criterion criterion : getCriteria()) {
	    m_idxCriteria.put(criterion, Integer.valueOf(idx));
	    ++idx;
	}
	return m_idxCriteria;
    }

    private BiMap<Alternative, Integer> idxProfiles() {
	m_idxProfiles = HashBiMap.create();
	int idx = 0;
	for (Alternative profile : getProfiles()) {
	    m_idxProfiles.put(profile, Integer.valueOf(idx));
	    ++idx;
	}
	return m_idxProfiles;
    }

    public int getAlternativeIdx(Alternative alternative) {
	Preconditions.checkArgument(getAlternatives().contains(alternative));
	return idxAlternatives().get(alternative).intValue();
    }

    public int getProfileIdx(Alternative profile) {
	Preconditions.checkArgument(getProfiles().contains(profile));
	return idxProfiles().get(profile).intValue();
    }

    public int getCriterionIdx(Criterion criterion) {
	Preconditions.checkArgument(getCriteria().contains(criterion));
	return idxCriteria().get(criterion).intValue();
    }

    public int getCategoryIdx(Category category) {
	Preconditions.checkArgument(getCatsAndProfs().getCategories().contains(category));
	return idxCategories().get(category).intValue();
    }

    /**
     * @param category
     *            must be contained in this object.
     * @return the rank, between 1 (the best category) and getCategories().getCategories().size() (the worst category).
     */
    public int getCategoryRank(Category category) {
	Preconditions.checkArgument(getCatsAndProfs().getCategories().contains(category));
	return getCatsAndProfs().getCategories().size() - getCategoryIdx(category);
    }

    /**
     * @return a copy, possibly immutable. Not <code>null</code>.
     */
    public Map<Alternative, Integer> getProfilesIndexes() {
	return ImmutableMap.copyOf(idxProfiles());
    }

    /**
     * @return a copy, possibly immutable. Not <code>null</code>.
     */
    public Map<Criterion, Integer> getCriteriaIndexes() {
	return ImmutableMap.copyOf(idxCriteria());
    }

    /**
     * @return a copy, possibly immutable. Not <code>null</code>.
     */
    public Map<Alternative, Integer> getAlternativesIndexes() {
	return ImmutableMap.copyOf(idxAlternatives());
    }

    /**
     * @return a copy, possibly immutable. Not <code>null</code>.
     */
    public Map<Category, Integer> getCategoriesRanks() {
	// final Map<ImmutableCategory, Integer> ranks = Maps.transformValues(idxCategories(), new Function<Integer,
	// Integer>() {
	// @Override
	// public Integer apply(Integer input) {
	// final int rank = getCategories().getCategories().size()-input.intValue();
	// return Integer.valueOf(rank);
	// }
	// });
	final Map<Category, Integer> ranks = Maps.transformEntries(idxCategories(),
		new EntryTransformer<Category, Integer, Integer>() {
		    @Override
		    public Integer transformEntry(Category key, Integer value) {
			final int rank = getCategoryRank(key);
			return Integer.valueOf(rank);
		    }
		});
	return ImmutableMap.copyOf(ranks);
    }
}
