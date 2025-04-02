package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decision_deck.utils.collection.CollectionUtils;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilitiesRead;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

/**
 * <p>
 * An ordered equivalent to {@link VersatileAssignments}.
 * </p>
 * <p>
 * The orderings returned by this object are always compatible with the order given by the categories. Hence, when the
 * categories are changed, it is possible that the order changes. Read carefully the contract of the relevant methods.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class VersatileOrderedAssignments {

    /**
     * The number of assigned alternatives that are assigned to multiple (i.e., more than one) categories. The
     * complement of this number to the number of assigned alternatives is the number of alternatives assigned to
     * exactly one category.
     */
    private int m_nbMultiCatsAlts;
    /**
     * For each category where at least one alternative is assigned, the alternatives assigned there. No
     * {@code null} values or key.
     */
    private final SetMultimap<Category, Alternative> m_alternatives = HashMultimap.create();
    /**
     * {@code null} iff not set. The content of the set is never changed, the full set is replaced with a new one
     * when needed.
     */
    private NavigableSet<Category> m_categories;
    /**
     * No {@code null} key, no {@code null} value. The map values each have at least one entry (Cat, Double),
     * themselves being without {@code null} key and value and having positive values. The contents of the maps as
     * values never change, they are replaced when an update is needed.
     */
    private final Map<Alternative, NavigableMap<Category, Double>> m_credibilitiesSorted = Maps.newLinkedHashMap();

    public VersatileOrderedAssignments() {
	/** Public default constructor. */
	m_categories = null;
	m_nbMultiCatsAlts = 0;
    }

    /**
     * Copy constructor by value.
     * 
     * @param copy
     *            not {@code null}. The source data to be copied into this object.
     */
    public VersatileOrderedAssignments(VersatileOrderedAssignments copy) {

	m_nbMultiCatsAlts = copy.m_nbMultiCatsAlts;

	if (copy.m_categories == null) {
	    m_categories = null;
	} else {
	    m_categories = new TreeSet<Category>(copy.m_categories);
	}

	/**
	 * NB Although this yields shared references to the underlying maps, it works because the content of the
	 * underlying maps never changes as they are replaced when a modification is needed.
	 */
	m_credibilitiesSorted.putAll(copy.m_credibilitiesSorted);
    }

    public VersatileOrderedAssignments(IOrderedAssignmentsWithCredibilitiesRead assignments) {
	this();
	final SortedSet<Category> categoriesSorted = assignments.getCategories();
	setCategories(categoriesSorted);

	for (Alternative alternative : assignments.getAlternatives()) {
	    final Map<Category, Double> credibilities = assignments.getCredibilities(alternative);
	    setCredibilities(alternative, credibilities);
	}
    }

    public VersatileOrderedAssignments(IOrderedAssignmentsToMultipleRead assignments) {
	this();
	final SortedSet<Category> categoriesSorted = assignments.getCategories();
	setCategories(categoriesSorted);

	for (Alternative alternative : assignments.getAlternatives()) {
	    final NavigableSet<Category> categories = assignments.getCategories(alternative);
	    setCategories(alternative, categories);
	}
    }

    public boolean setCategories(SortedSet<Category> categories) {
	if (categories == null && !m_credibilitiesSorted.isEmpty()) {
	    throw new IllegalStateException("Should not remove order when not empty.");
	}
	if (categories == null) {
	    final boolean wasNull = (m_categories == null);
	    m_categories = null;
	    return !wasNull;
	}

	final boolean categoriesChanged = (m_categories != null && !Iterables.elementsEqual(categories, m_categories) || m_categories == null);
	if (!categoriesChanged) {
	    return false;
	}

	final NavigableSet<Category> newCategories = CollectionUtils.newExtentionalTotalOrder(categories);
	m_categories = newCategories;
	reloadCredibilitiesSorted();
	return true;
    }

    /**
     * @return {@code true} iff every assigned alternatives are assigned to exactly one category (thus with a
     *         credibility degree of one). Returns {@code false} iff at least one alternative is assigned to more
     *         than one category.
     */
    public boolean isCrisp() {
	return m_nbMultiCatsAlts == 0;
    }

    /**
     * Sets, replaces, or removes the assignment of an alternative and the associated degrees of credibility. The used
     * ordering is the one given by {@link #getCategories()}. A zero value as a degree of credibility is considered as
     * equivalent to a missing entry: it is interpreted as meaning that the given alternative is not assigned to the
     * corresponding category.
     * 
     * @param alternative
     *            not {@code null}.
     * @param credibilities
     *            {@code null} or empty to assign the alternative to no category, i.e., to remove the assignment of
     *            the given alternative. The map entries may not contain a {@code null} key or value, the values
     *            must be positive or zero, the categories must be contained in {@link #getCategories()}. If the map
     *            contains only zeroes, it is considered empty.
     * @return {@code true} iff the call changed the assignments, i.e., iff the assignment existed and has been
     *         removed, or existed and has changed (be it a change in some credibility degrees or a change of category),
     *         or did not exist and has been added.
     */
    public boolean setCredibilities(Alternative alternative, Map<Category, Double> credibilities) {
	if (alternative == null) {
	    throw new NullPointerException("" + alternative + credibilities);
	}
	if (m_categories == null) {
	    throw new IllegalStateException("Categories order must be set.");
	}
	if (credibilities != null && !m_categories.containsAll(credibilities.keySet())) {
	    throw new IllegalArgumentException(String.valueOf("Given categories " + credibilities.keySet()
		    + " are not a subset of categories: " + m_categories + "."));
	}

	/**
	 * Note that we may not re-use a possibly existing previous map and only change the underlying entries as the
	 * external world may have a reference to it through the use of the get methods. We must create a new map (or
	 * change implementation of the get methods). We also need a defensive copy of the credibilities argument.
	 */
	final Map<Category, Double> previous = m_credibilitiesSorted.get(alternative);

	final Map<Category, Double> credibilitiesNoZeroes = credibilities == null ? Maps
		.<Category, Double> newHashMap() : Maps.filterValues(credibilities,
		Predicates.not(Predicates.in(Collections.singleton(Double.valueOf(0d)))));
	final boolean same = (credibilitiesNoZeroes.isEmpty() && previous == null)
		|| (credibilitiesNoZeroes.equals(previous));

	if (credibilitiesNoZeroes.isEmpty()) {
	    remove(alternative);
	    return !same;
	}

	final TreeMap<Category, Double> sortedMap = new TreeMap<Category, Double>(m_categories.comparator());

	final Set<Category> newCategories = credibilitiesNoZeroes.keySet();
	for (Category category : newCategories) {
	    final Double credibility = credibilitiesNoZeroes.get(category);
	    if (credibility == null || credibility.doubleValue() <= 0d) {
		throw new IllegalArgumentException("Invalid credibility found for " + alternative + ", " + category
			+ ": " + credibility + ".");
	    }
	    sortedMap.put(category, credibility);
	}

	final Set<Category> previousCategories = previous == null ? new HashSet<Category>() : previous.keySet();
	for (Category previousCategory : previousCategories) {
	    m_alternatives.remove(previousCategory, alternative);
	}
	if (previousCategories.size() > 1) {
	    --m_nbMultiCatsAlts;
	}

	m_credibilitiesSorted.put(alternative, sortedMap);

	for (Category newCategory : newCategories) {
	    m_alternatives.put(newCategory, alternative);
	}
	if (credibilitiesNoZeroes.size() > 1) {
	    ++m_nbMultiCatsAlts;
	}

	return !same;
    }

    public NavigableMap<Category, Double> remove(Alternative alternative) {
	final NavigableMap<Category, Double> previous = m_credibilitiesSorted.remove(alternative);
	final Set<Category> previousCategories = previous == null ? new HashSet<Category>() : previous.keySet();
	for (Category previousCategory : previousCategories) {
	    m_alternatives.remove(previousCategory, alternative);
	}
	if (previousCategories.size() > 1) {
	    --m_nbMultiCatsAlts;
	}
	return previous;
    }

    /**
     * <p>
     * Sets, replaces, or removes the assignment of an alternative. The given categories ordering must have been
     * defined.
     * </p>
     * <p>
     * When this method is used to add an assignment, evenly shared degrees of credibility are added automatically for
     * compatibility with {@link #getCredibilities(Alternative)}.
     * </p>
     * 
     * @param alternative
     *            not {@code null}.
     * @param categories
     *            {@code null} or empty to assign the alternative to no category, i.e., to remove the assignment of
     *            the given alternative. Otherwise, must be a subset of the categories returned by
     *            {@link #getCategories()}.
     * @return {@code true} iff the call changed the assignments, i.e. true iff the given alternative was assigned
     *         and the assignment has been removed, or was assigned to a not identical set of categories, or was not
     *         assigned and has been.
     */
    public boolean setCategories(Alternative alternative, Set<Category> categories) {
	final Map<Category, Double> credibilities;
	if (categories == null || categories.isEmpty()) {
	    credibilities = null;
	} else {
	    credibilities = new HashMap<Category, Double>();
	    final Double credibility = Double.valueOf(1d / categories.size());
	    for (Category category : categories) {
		credibilities.put(category, credibility);
	    }
	}
	return setCredibilities(alternative, credibilities);
    }

    public Map<Category, Double> getCredibilities(Alternative alternative) {
	return getCredibilitiesSorted(alternative);
    }

    public NavigableMap<Category, Double> getCredibilitiesSorted(Alternative alternative) {
	final NavigableMap<Category, Double> map = m_credibilitiesSorted.get(alternative);
	if (map == null) {
	    return null;
	}
	return Maps.unmodifiableNavigableMap(map);
    }

    /**
     * Reloads the sorted map on the basis of the current categories order. Note that this will <em>change</em> the
     * ordering of the assignments if there are some assignments whose current ordering is not the same as the one given
     * by the current categories.
     * 
     */
    private void reloadCredibilitiesSorted() {
	if (m_categories == null) {
	    throw new IllegalStateException("No order has been defined on the categories.");
	}
	for (Alternative alternative : m_credibilitiesSorted.keySet()) {
	    final Map<Category, Double> map = m_credibilitiesSorted.get(alternative);
	    final TreeMap<Category, Double> newSortedMap = new TreeMap<Category, Double>(m_categories.comparator());
	    newSortedMap.putAll(map);
	    m_credibilitiesSorted.put(alternative, newSortedMap);
	}
    }

    public Set<Category> getCategories(Alternative alternative) {
	final Map<Category, Double> credibilities = m_credibilitiesSorted.get(alternative);
	if (credibilities == null) {
	    return null;
	}
	return Collections.unmodifiableSet(credibilities.keySet());
    }

    /**
     * Sets, replaces, or removes the assignment of an alternative. A credibility value of one is used. The given
     * category must exist in the categories order, otherwise the sorted categories returned by
     * {@link #getCategoriesSorted()} would not be a superset of the used categories any more.
     * 
     * @param alternative
     *            not {@code null}.
     * @param category
     *            {@code null} to remove the assignment.
     * @return {@code true} iff the call changed the assignments, i.e. {@code true} iff the given alternative
     *         was assigned and the assignment has been removed, or was assigned to a different category, or was not
     *         assigned and has been assigned to a category.
     */
    public boolean setCategory(Alternative alternative, Category category) {
	if (category != null && m_categories != null && !m_categories.contains(category)) {
	    throw new IllegalStateException("Category must be defined in the ordering.");
	}
	final Map<Category, Double> credibilities;
	if (category == null) {
	    credibilities = null;
	} else {
	    credibilities = Collections.singletonMap(category, Double.valueOf(1d));
	}
	return setCredibilities(alternative, credibilities);
    }

    /**
     * <p>
     * The alternative must not be assigned to more than one category (otherwise an exception is raised). Note that this
     * object needs this supplementary condition and thus can't implement correctly the interface for the single
     * category case, because it already implements more complex cases, hence there is no guarantee that all
     * alternatives are assigned to no more than one category.
     * </p>
     * 
     * @param alternative
     *            not {@code null}.
     * @return the category to which this alternative is assigned, or {@code null} iff this alternative is not
     *         assigned.
     */
    public Category getCategory(Alternative alternative) {
	final Map<Category, Double> credibilities = m_credibilitiesSorted.get(alternative);
	if (credibilities == null) {
	    return null;
	}
	if (credibilities.size() > 1) {
	    throw new IllegalStateException("One category was asked while " + alternative
		    + " is assigned to more than one category.");
	}
	return credibilities.keySet().iterator().next();
    }

    public Set<Category> getCategories() {
	return getCategoriesSorted();
    }

    public Set<Alternative> getAlternatives(Category category) {
	Preconditions.checkNotNull(category);
	final Set<Alternative> alternatives = m_alternatives.get(category);
	return Collections.unmodifiableSet(alternatives);
    }

    public NavigableSet<Category> getCategoriesSorted() {
	if (m_categories == null) {
	    /**
	     * Simply returning a new empty treeset does not work because no comparator would be defined, thus the user
	     * would not be able to test e.g. if some category is contained in the returned set.
	     */
	    return CollectionUtils.newExtentionalTotalOrder(Collections.<Category> emptySet());
	}
	return Sets.unmodifiableNavigableSet(m_categories);
    }

    public Set<Alternative> getAlternatives() {
	final Set<Alternative> alternatives = m_credibilitiesSorted.keySet();
	return Collections.unmodifiableSet(alternatives);
    }

    public NavigableSet<Category> getCategoriesSorted(Alternative alternative) {
	final NavigableMap<Category, Double> map = getCredibilitiesSorted(alternative);
	if (map == null) {
	    return null;
	}
	final NavigableSet<Category> cats = map.navigableKeySet();
	return Sets.unmodifiableNavigableSet(cats);
    }

    public boolean clear() {
	final boolean empty = m_categories == null;
	if (empty) {
	    return false;
	}
	m_alternatives.clear();
	m_categories = null;
	m_credibilitiesSorted.clear();
	m_nbMultiCatsAlts = 0;
	return true;
    }

}
