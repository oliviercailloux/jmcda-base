package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IAssignmentsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IAssignmentsWithCredibilitiesRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilities;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

/**
 * <p>
 * This class implements both the methods from {@link IAssignmentsWithCredibilities} and {@link IAssignmentsToMultiple},
 * and thus provides auto conversion between credibilities and ‘‘no credibilities’’. When an alternative is assigned to
 * several categories, with no credibilities specification, it is automatically interpreted as meaning that each
 * category has an associated credibility of one divided by the number of categories to which this alternative has been
 * assigned. It is probably not such a good idea for a user of this class to rely on that automatic conversion
 * capability as it can be difficult to read and maintain, but it is permitted, and this allows for this object to be
 * easily wrapped by objects that implement one of these interfaces specifically. This object may not implement both
 * these interfaces because the related equality relations are incompatible (see definition in these interfaces
 * documentation).
 * </p>
 * <p>
 * This class does <em>not</em> implement {@link IOrderedAssignmentsWithCredibilities} or
 * {@link IOrderedAssignmentsToMultiple}, as the ordered case is not compatible with the non-ordered one: the former
 * demands that the order on the categories be specified beforehand. Also this class can't implement
 * {@link IAssignmentsRead} as this class authorizes an alternative to be assigned to more than one categories. It is
 * however functionally equivalent if only the setter method appropriate to the single category case is used.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class VersatileAssignments {

    /**
     * No {@code null} key, no {@code null} value. The map values each have at least one entry (Cat, Double),
     * themselves being without {@code null} key and value and having values summing to one, and not negative.
     */
    private final Map<Alternative, Map<Category, Double>> m_credibilities = Maps.newLinkedHashMap();
    /**
     * The number of assigned alternatives that are assigned to multiple (i.e., more than one) categories. The
     * complement of this number to the number of assigned alternatives is the number of alternatives assigned to
     * exactly one category.
     */
    private int m_nbMultiCatsAlts;
    /**
     * {@code null} iff not set. The content of the set is never changed, the full set is replaced with a new one
     * when needed.
     */
    private Set<Category> m_categories;
    /**
     * For each category where at least one alternative is assigned, the alternatives assigned there. No
     * {@code null} values or key.
     */
    private final SetMultimap<Category, Alternative> m_alternatives = LinkedHashMultimap.create();

    public VersatileAssignments() {
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
    public VersatileAssignments(VersatileAssignments copy) {

	m_nbMultiCatsAlts = copy.m_nbMultiCatsAlts;

	if (copy.m_categories == null) {
	    m_categories = null;
	} else {
	    m_categories = new HashSet<Category>(copy.m_categories);
	}

	/**
	 * NB Although this yields shared references to the underlying maps, it works because the content of the
	 * underlying maps never changes as they are replaced when a modification is needed.
	 */
	m_credibilities.putAll(copy.m_credibilities);
    }

    public VersatileAssignments(IAssignmentsWithCredibilitiesRead assignments) {
	this();
	final Set<Category> categories = assignments.getCategories();
	setCategories(categories);

	for (Alternative alternative : assignments.getAlternatives()) {
	    final Map<Category, Double> credibilities = assignments.getCredibilities(alternative);
	    setCredibilities(alternative, credibilities);
	}
    }

    /**
     * <p>
     * Sets the categories this object will return to {@link #getCategories()}, or removes them and restore the default
     * behavior. The given categories should be a superset of the categories to which objects are and will be assigned,
     * otherwise the contract of {@link #getCategories()} will not be fulfilled.
     * </p>
     * <p>
     * When no categories is associated to this object, it returns the set of categories to which at least one
     * alternative has been assigned.
     * </p>
     * 
     * @param categories
     *            {@code null} to remove the associated categories (all the orderings are lost). A superset of the
     *            categories already used.
     * @return {@code true} iff the categories changed.
     */
    public boolean setCategories(Set<Category> categories) {
	if (categories == null) {
	    final boolean changed = m_categories != null;
	    m_categories = null;
	    return changed;
	}

	final boolean categoriesChanged = !categories.equals(m_categories);
	if (!categoriesChanged) {
	    return false;
	}

	final Set<Category> newCategories = new HashSet<Category>(categories);
	m_categories = newCategories;
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
     * Sets, replaces, or removes the assignment of an alternative and the associated degrees of credibility. A zero
     * value as a degree of credibility is considered as equivalent to a missing entry: it is interpreted as meaning
     * that the given alternative is not assigned to the corresponding category.
     * 
     * @param alternative
     *            not {@code null}.
     * @param credibilities
     *            {@code null} or empty to assign the alternative to no category, i.e., to remove the assignment of
     *            the given alternative. The map entries may not contain a {@code null} key or value, the values
     *            must be positive or zero. If the map contains only zeroes, it is considered empty.
     * @return {@code true} iff the call changed the assignments, i.e., iff the assignment existed and has been
     *         removed, or existed and has changed (be it a change in some credibility degrees or a change of category),
     *         or did not exist and has been added.
     */
    public boolean setCredibilities(Alternative alternative, Map<Category, Double> credibilities) {
	if (alternative == null) {
	    throw new NullPointerException("" + alternative + credibilities);
	}

	/**
	 * Note that we may not re-use a possibly existing previous map and only change the underlying entries as the
	 * external world may have a reference to it through the use of the get methods. We must create a new map (or
	 * change implementation of the get methods). We also need a defensive copy of the credibilities argument.
	 */
	final Map<Category, Double> previous = remove(alternative);

	final Map<Category, Double> credibilitiesNoZeroes = credibilities == null ? Maps
		.<Category, Double> newHashMap() : Maps.filterValues(
		credibilities, Predicates.not(Predicates.in(Collections.singleton(Double.valueOf(0d)))));
	final boolean same = (credibilitiesNoZeroes.isEmpty() && previous == null)
		|| (credibilitiesNoZeroes.equals(previous));

	if (credibilitiesNoZeroes.isEmpty()) {
	    return !same;
	}

	final Map<Category, Double> newCredibilities = new HashMap<Category, Double>();

	final Set<Category> newCategories = credibilitiesNoZeroes.keySet();
	for (Category category : newCategories) {
	    final Double credibility = credibilitiesNoZeroes.get(category);
	    if (credibility == null || credibility.doubleValue() <= 0d) {
		throw new IllegalArgumentException("Invalid credibility found for " + alternative + ", " + category
			+ ": " + credibility + ".");
	    }
	    newCredibilities.put(category, credibility);
	}
	m_credibilities.put(alternative, newCredibilities);
	for (Category newCategory : newCategories) {
	    m_alternatives.put(newCategory, alternative);
	}
	if (credibilitiesNoZeroes.size() > 1) {
	    ++m_nbMultiCatsAlts;
	}

	return !same;
    }

    public Map<Category, Double> remove(Alternative alternative) {
	final Map<Category, Double> previous = m_credibilities.remove(alternative);
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
     * Sets, replaces, or removes the assignment of an alternative.
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
     *            the given alternative.
     * @return {@code true} iff the call changed the assignments, i.e. true iff the given alternative was assigned
     *         and the assignment has been removed, or was assigned to a not identical set of categories, or was not
     *         assigned and has been.
     */
    public boolean setCategories(Alternative alternative, Set<Category> categories) {
	if (categories == null || categories.isEmpty()) {
	    return setCredibilities(alternative, null);
	}
	final Map<Category, Double> credibilities = new HashMap<Category, Double>();
	final Double credibility = Double.valueOf(1d / categories.size());
	for (Category category : categories) {
	    credibilities.put(category, credibility);
	}
	return setCredibilities(alternative, credibilities);
    }

    public Map<Category, Double> getCredibilities(Alternative alternative) {
	final Map<Category, Double> credibilities = m_credibilities.get(alternative);
	if (credibilities == null) {
	    return null;
	}
	return Collections.unmodifiableMap(credibilities);
    }

    public Set<Category> getCategories(Alternative alternative) {
	final Map<Category, Double> credibilities = m_credibilities.get(alternative);
	if (credibilities == null) {
	    return null;
	}
	return Collections.unmodifiableSet(credibilities.keySet());
    }

    /**
     * Sets, replaces, or removes the assignment of an alternative. A credibility value of one is used.
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
	final Map<Category, Double> credibilities;
	credibilities = m_credibilities.get(alternative);
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
	if (m_categories == null) {
	    return new HashSet<Category>(m_alternatives.keySet());
	}
	return Collections.unmodifiableSet(m_categories);
    }

    public Set<Alternative> getAlternatives() {
	final Set<Alternative> alternatives = m_credibilities.keySet();
	return Collections.unmodifiableSet(alternatives);
    }

    public Set<Alternative> getAlternatives(Category category) {
	Preconditions.checkNotNull(category);
	final Set<Alternative> alternatives = m_alternatives.get(category);
	return Collections.unmodifiableSet(alternatives);
    }

    public boolean clear() {
	final boolean empty = m_alternatives.isEmpty() && m_categories == null;
	if (empty) {
	    return false;
	}
	m_alternatives.clear();
	m_categories = null;
	m_credibilities.clear();
	m_nbMultiCatsAlts = 0;
	return true;
    }

}
