package org.decisiondeck.jmcda.structure.sorting.assignment.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.sorting.category.Categories;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decision_deck.utils.Pair;
import org.decision_deck.utils.collection.CollectionUtils;
import org.decision_deck.utils.collection.SetBackedMap;
import org.decision_deck.utils.collection.extensional_order.ExtentionalTotalOrder;
import org.decisiondeck.jmcda.exc.InvalidInputException;
import org.decisiondeck.jmcda.persist.utils.ExportSettings;
import org.decisiondeck.jmcda.structure.sorting.assignment.IAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.IAssignmentsRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.IAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.assignment.IAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultipleRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsFiltering;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsFromRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsToMultipleEdgesView;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsToMultipleFiltering;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsToMultipleFromRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IAssignmentsWithCredibilitiesRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilities;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.IOrderedAssignmentsWithCredibilitiesRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.OrderedAssignmentsWithCredibilitiesFromRead;
import org.decisiondeck.jmcda.structure.sorting.assignment.credibilities.OrderedAssignmentsWithCredibilitiesViewFromMultiple;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class AssignmentsUtils {

    static public void assertRestriction(IOrderedAssignmentsToMultipleRead assignments,
	    IOrderedAssignmentsToMultipleRead restriction, String name1, String name2) throws IllegalStateException {
	assertEqual(assignments.getAlternatives(), restriction.getAlternatives(), name1, name2);
	final Set<Alternative> alternatives = assignments.getAlternatives();
	for (Alternative alternative : alternatives) {
	    final NavigableSet<Category> categoriesFull = assignments.getCategories(alternative);
	    final NavigableSet<Category> categoriesRestricted = restriction.getCategories(alternative);
	    if (!CollectionUtils.containsInOrder(categoriesFull, categoriesRestricted)) {
		final String interval1 = Categories.toIntervalString(categoriesFull);
		final String interval2 = Categories.toIntervalString(categoriesRestricted);
		throw new IllegalStateException("Incompatible assignments for " + alternative + ": " + interval1
			+ " according to " + name1 + " and " + interval2 + " according to " + name2 + ".");
	    }
	}
    }

    /**
     * Retrieves a read-only view of all the alternatives assigned in the given assignments objects
     * <em>at the time this method is invoked</em>. If the set of assignments in a given assignments object change after
     * this method returns, the change will be reflected in the returned set, but if an assignments object is added to
     * the iterable, the change is not reflected.
     * 
     * @param allAssignments
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public Set<Alternative> getUnionAssignedAlternatives(
	    Iterable<? extends IOrderedAssignmentsToMultipleRead> allAssignments) {
	Preconditions.checkNotNull(allAssignments);
	final Iterable<Set<Alternative>> allAlternatives = Iterables.transform(allAssignments,
		new Function<IOrderedAssignmentsToMultipleRead, Set<Alternative>>() {
		    @Override
		    public Set<Alternative> apply(IOrderedAssignmentsToMultipleRead input) {
			return input.getAlternatives();
		    }
		});
	return CollectionUtils.union(allAlternatives);
    }

    static public IOrderedAssignmentsWithCredibilitiesRead getOrderedAssignmentsWithCredibilitiesView(
	    IOrderedAssignmentsToMultipleRead assignments, double credibilityValue) {
	return new OrderedAssignmentsWithCredibilitiesViewFromMultiple(assignments, credibilityValue);
    }

    /**
     * Overrides the target by replacing every information it contains with the information in the given source.
     * 
     * @param source
     *            not <code>null</code>.
     * @param target
     *            not <code>null</code>.
     * @return <code>true</code> iff the call to this method changed the contents of the given target, or equivalently,
     *         <code>false</code> iff the given target was equal to the given source.
     */
    static public boolean copyOrderedAssignmentsToMultipleToTarget(IOrderedAssignmentsToMultipleRead source,
	    IOrderedAssignmentsToMultiple target) {
	Preconditions.checkNotNull(source);
	Preconditions.checkNotNull(target);

	if (target.equals(source)) {
	    return false;
	}

	target.clear();

	target.setCategories(source.getCategories());

	for (Alternative alternative : source.getAlternatives()) {
	    final NavigableSet<Category> categories = source.getCategories(alternative);
	    target.setCategories(alternative, categories);
	}
	return true;
    }

    static public boolean equivalentOrderedWithCredibilities(IOrderedAssignmentsWithCredibilitiesRead ordered1,
	    IOrderedAssignmentsWithCredibilitiesRead ordered2) {
	return getEquivalenceRelationOrderedWithCredibilities().equivalent(ordered1, ordered2);
    }

    static private <T> void assertEqual(Set<T> set1, Set<T> set2, String name1, String name2) {
	final Set<T> difference = Sets.symmetricDifference(set1, set2);
	if (!difference.isEmpty()) {
	    final String realName2 = name2 == null ? "2" : name2;
	    final String realName1 = name1 == null ? "1" : name1;
	    final T first = Iterables.get(difference, 0);
	    final String contained;
	    final String missing;
	    if (set1.contains(first)) {
		contained = realName1;
		missing = realName2;
	    } else {
		contained = realName2;
		missing = realName1;
	    }
	    throw new IllegalStateException("Element " + first + " is contained in set " + contained + " but not in "
		    + missing + ".");
	}
    }

    static public void ensuresSingle(IAssignmentsToMultipleRead assignments) throws InvalidInputException {
	final Set<Alternative> alternatives = assignments.getAlternatives();
	for (Alternative alternative : alternatives) {
	    if (assignments.getCategories(alternative).size() != 1) {
		throw new InvalidInputException("Alternative " + alternative + " is in more than one category.");
	    }
	}
    }

    /**
     * Replaces all assignments to the given old category with assignments to the given new category, and replaces the
     * old category with the new one in the set of categories bound to the given assignments object.
     * 
     * @param assignments
     *            not <code>null</code>.
     * @param oldCategory
     *            not <code>null</code>, must exist in the set of categories of the given assignments.
     * @param newCategory
     *            not <code>null</code>, must not exist in the set of categories of the given assignments.
     */
    static public void renameCategory(final IOrderedAssignments assignments, final Category oldCategory,
	    Category newCategory) {
	Preconditions.checkNotNull(assignments);
	Preconditions.checkNotNull(oldCategory);
	Preconditions.checkNotNull(newCategory);
	final NavigableSet<Category> current = assignments.getCategories();
	Preconditions.checkArgument(current.contains(oldCategory));
	Preconditions.checkArgument(!current.contains(newCategory));

	final ExtentionalTotalOrder<Category> modified = ExtentionalTotalOrder.create(current);
	modified.addAfter(oldCategory, newCategory);
	assignments.setCategories(modified);
	final Set<Alternative> alternatives = ImmutableSet.copyOf(assignments.getAlternatives(oldCategory));
	for (Alternative alternative : alternatives) {
	    assignments.setCategory(alternative, newCategory);
	}
	modified.remove(oldCategory);
	assignments.setCategories(modified);
    }

    /**
     * Removes all assignments to the given category if there is any, and removes the category from the set of
     * categories bound to the given assignments if it exists.
     * 
     * @param assignments
     *            not <code>null</code>.
     * @param toRemove
     *            not <code>null</code>.
     * @return <code>true</code> iff the call to this method changed the state of the given assignments, or
     *         equivalently, <code>true</code> iff the given category was contained in the given assignments.
     */
    static public boolean removeCategory(IOrderedAssignments assignments, Category toRemove) {
	Preconditions.checkNotNull(assignments);
	Preconditions.checkNotNull(toRemove);
	final NavigableSet<Category> current = assignments.getCategories();
	if (!current.contains(toRemove)) {
	    return false;
	}
	final ExtentionalTotalOrder<Category> modified = ExtentionalTotalOrder.create(current);
	final Set<Alternative> alternatives = ImmutableSet.copyOf(assignments.getAlternatives(toRemove));
	for (Alternative alternative : alternatives) {
	    assignments.setCategory(alternative, null);
	}
	modified.remove(toRemove);
	assignments.setCategories(modified);
	return true;
    }

    static public void renameCategory(IOrderedAssignmentsToMultiple assignments, Category oldCategory,
	    Category newCategory) {
	Preconditions.checkNotNull(assignments);
	Preconditions.checkNotNull(oldCategory);
	Preconditions.checkNotNull(newCategory);
	final NavigableSet<Category> current = assignments.getCategories();
	Preconditions.checkArgument(current.contains(oldCategory));
	Preconditions.checkArgument(!current.contains(newCategory));

	final ExtentionalTotalOrder<Category> modified = ExtentionalTotalOrder.create(current);
	modified.addAfter(oldCategory, newCategory);
	assignments.setCategories(modified);
	final Set<Alternative> alternatives = ImmutableSet.copyOf(assignments.getAlternatives(oldCategory));
	for (Alternative alternative : alternatives) {
	    final Set<Category> categories = assignments.getCategories(alternative);
	    final Set<Category> extended = Sets.<Category> newHashSet(categories);
	    extended.add(newCategory);
	    assignments.setCategories(alternative, extended);
	}
	modified.remove(oldCategory);
	assignments.setCategories(modified);
    }

    static public IOrderedAssignmentsToMultiple newRenameAndReorderToMultiple(IOrderedAssignmentsToMultipleRead source,
	    Function<? super Category, Category> renameCategories,
	    Function<? super Alternative, Alternative> renameAlternatives,
	    Comparator<? super Alternative> newAlternativeOrder) {
	final IOrderedAssignmentsToMultiple assignments = AssignmentsFactory.newOrderedAssignmentsToMultiple();
	// final List<Category> sortedCategories = Ordering.from(newCategoryOrder).sortedCopy(source.getCategories());
	assignments.setCategories(ExtentionalTotalOrder.create(Iterables.transform(source.getCategories(),
		renameCategories)));

	final List<Alternative> sortedAlternatives = Ordering.from(newAlternativeOrder).sortedCopy(
		source.getAlternatives());
	for (Alternative alternative : sortedAlternatives) {
	    final Alternative renamedAlternative = renameAlternatives.apply(alternative);
	    final NavigableSet<Category> sourceCategories = source.getCategories(alternative);
	    final ExtentionalTotalOrder<Category> renamedCategories = ExtentionalTotalOrder.create();
	    for (Category sourceCategory : sourceCategories) {
		final Category renamedCategory = renameCategories.apply(sourceCategory);
		renamedCategories.addAsHighest(renamedCategory);
	    }
	    assignments.setCategories(renamedAlternative, renamedCategories);
	}

	return assignments;
    }

    /**
     * Removes all assignments to the given category if there is any, and removes the category from the set of
     * categories bound to the given assignments if it exists.
     * 
     * @param assignments
     *            not <code>null</code>.
     * @param toRemove
     *            not <code>null</code>.
     * @return <code>true</code> iff the call to this method changed the state of the given assignments, or
     *         equivalently, <code>true</code> iff the given category was contained in the given assignments.
     */
    static public boolean removeCategory(IOrderedAssignmentsToMultiple assignments, Category toRemove) {
	Preconditions.checkNotNull(assignments);
	Preconditions.checkNotNull(toRemove);
	final NavigableSet<Category> current = assignments.getCategories();
	if (!current.contains(toRemove)) {
	    return false;
	}
	final ExtentionalTotalOrder<Category> modified = ExtentionalTotalOrder.create(current);
	final Set<Alternative> alternatives = ImmutableSet.copyOf(assignments.getAlternatives(toRemove));
	for (Alternative alternative : alternatives) {
	    assignments.setCategories(alternative, null);
	}
	modified.remove(toRemove);
	assignments.setCategories(modified);
	return true;
    }

    /**
     * Overrides the target by replacing every information it contains with the information in the given source.
     * 
     * @param source
     *            not <code>null</code>.
     * @param target
     *            not <code>null</code>.
     * @return <code>true</code> iff the call to this method changed the contents of the given target, or equivalently,
     *         <code>false</code> iff the given target was equal to the given source.
     */
    static public boolean copyOrderedAssignmentsToTarget(IOrderedAssignmentsRead source, IOrderedAssignments target) {
	Preconditions.checkNotNull(source);
	Preconditions.checkNotNull(target);

	if (target.equals(source)) {
	    return false;
	}

	target.clear();

	target.setCategories(source.getCategories());

	for (Alternative alternative : source.getAlternatives()) {
	    final Category category = source.getCategory(alternative);
	    target.setCategory(alternative, category);
	}
	return true;
    }

    /**
     * <p>
     * The source must have each alternative assigned to only one category.
     * </p>
     * <p>
     * Overrides the target by replacing every information it contains with the information in the given source.
     * </p>
     * 
     * @param source
     *            not <code>null</code>.
     * @param target
     *            not <code>null</code>.
     * @throws InvalidInputException
     *             if the source contains at least one alternative assigned to more than one category.
     */
    static public void copyOrderedAssignmentsToMultipleToTargetSingle(IOrderedAssignmentsToMultipleRead source,
	    IOrderedAssignments target) throws InvalidInputException {
	Preconditions.checkNotNull(source);
	Preconditions.checkNotNull(target);

	target.clear();

	target.setCategories(source.getCategories());

	for (Alternative alternative : source.getAlternatives()) {
	    final NavigableSet<Category> assigned = source.getCategories(alternative);
	    if (assigned.size() != 1) {
		throw new InvalidInputException("Alternative assigned to more than one categories: " + alternative
			+ ".");
	    }
	    final Category category = Iterables.getOnlyElement(assigned);
	    target.setCategory(alternative, category);
	}
    }

    static public Equivalence<IAssignmentsWithCredibilitiesRead> getEquivalenceRelationWithCredibilities() {
	return new Equivalence<IAssignmentsWithCredibilitiesRead>() {
	    @Override
	    public boolean doEquivalent(IAssignmentsWithCredibilitiesRead assignments1,
		    IAssignmentsWithCredibilitiesRead assignments2) {
		if (assignments1 instanceof IOrderedAssignmentsWithCredibilitiesRead
			&& assignments2 instanceof IOrderedAssignmentsWithCredibilitiesRead) {
		    return equivalentOrderedWithCredibilities((IOrderedAssignmentsWithCredibilitiesRead) assignments1,
			    (IOrderedAssignmentsWithCredibilitiesRead) assignments2);
		}
		if (assignments1 instanceof IOrderedAssignmentsWithCredibilitiesRead
			|| assignments2 instanceof IOrderedAssignmentsWithCredibilitiesRead) {
		    return false;
		}

		final Set<Category> cats1 = assignments1.getCategories();
		final Set<Category> cats2 = assignments2.getCategories();
		if (!cats1.equals(cats2)) {
		    return false;
		}

		final Set<Alternative> alternatives1 = assignments1.getAlternatives();
		final Set<Alternative> alternatives2 = assignments2.getAlternatives();
		if (!alternatives1.equals(alternatives2)) {
		    return false;
		}

		for (Alternative alternative : assignments1.getAlternatives()) {
		    final Map<Category, Double> credibilities1 = assignments1.getCredibilities(alternative);
		    final Map<Category, Double> credibilities2 = assignments2.getCredibilities(alternative);
		    if (!credibilities1.equals(credibilities2)) {
			return false;
		    }
		}

		return true;
	    }

	    @Override
	    public int doHash(IAssignmentsWithCredibilitiesRead assignments) {
		int hash = 0;
		hash += getMapView(assignments).hashCode();
		hash += Objects.hashCode(assignments.getCategories(), assignments.getAlternatives());
		return hash;
	    }
	};
    }

    /**
     * Retrieves a description of all data contained in the given assignments object in a packed, debug, form. This
     * method is suitable for use in a {@link #toString()} method.
     * 
     * @param assignments
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public String getShortDescription(final IAssignmentsRead assignments) {
	final ToStringHelper helper = Objects.toStringHelper(assignments);

	{
	    final Set<Category> categories = assignments.getCategories();
	    final Iterable<String> categoriesAsStrings = Iterables.transform(categories,
		    new ExportSettings.CategoryToName());
	    final String categoriesString = Joiner.on(", ").join(categoriesAsStrings);
	    helper.add("categories", "{" + categoriesString + "}");
	}

	final Function<Alternative, String> transformAssignmentToDescription = new Function<Alternative, String>() {
	    private static final String RIGHT_ARROW = "\u2192";

	    @Override
	    public String apply(Alternative alternative) {
		final Category category = assignments.getCategory(alternative);
		return alternative.getId() + RIGHT_ARROW + (new ExportSettings.CategoryToName().apply(category));
	    }
	};
	final Iterable<String> assignmentsDescriptions = Iterables.transform(assignments.getAlternatives(),
		transformAssignmentToDescription);
	final String descrAssignments = Joiner.on("; ").join(assignmentsDescriptions);
	helper.add("assignments", "{" + descrAssignments + "}");
	return helper.toString();
    }

    static public Equivalence<IAssignmentsToMultipleRead> getEquivalenceRelationToMultiple() {
	return new Equivalence<IAssignmentsToMultipleRead>() {
	    @Override
	    public boolean doEquivalent(IAssignmentsToMultipleRead assignments1, IAssignmentsToMultipleRead assignments2) {
		if (assignments1 instanceof IOrderedAssignmentsToMultipleRead
			&& assignments2 instanceof IOrderedAssignmentsToMultipleRead) {
		    return equivalentOrderedToMultiple((IOrderedAssignmentsToMultipleRead) assignments1,
			    (IOrderedAssignmentsToMultipleRead) assignments2);
		}
		if (assignments1 instanceof IOrderedAssignmentsToMultipleRead
			|| assignments2 instanceof IOrderedAssignmentsToMultipleRead) {
		    return false;
		}
		if (assignments1 instanceof IAssignmentsWithCredibilitiesRead
			&& assignments2 instanceof IAssignmentsWithCredibilitiesRead) {
		    return equivalentWithCredibilities((IAssignmentsWithCredibilitiesRead) assignments1,
			    (IAssignmentsWithCredibilitiesRead) assignments2);
		}
		if (assignments1 instanceof IAssignmentsWithCredibilitiesRead
			|| assignments2 instanceof IAssignmentsWithCredibilitiesRead) {
		    return false;
		}

		final Set<Category> cats1 = assignments1.getCategories();
		final Set<Category> cats2 = assignments2.getCategories();
		if (!cats1.equals(cats2)) {
		    return false;
		}

		for (Category category : cats1) {
		    final Set<Alternative> alternatives1 = assignments1.getAlternatives(category);
		    final Set<Alternative> alternatives2 = assignments2.getAlternatives(category);
		    if (!alternatives1.equals(alternatives2)) {
			return false;
		    }
		}

		return true;
	    }

	    @Override
	    public int doHash(IAssignmentsToMultipleRead assignments) {
		int hash = 0;
		hash += getMapView(assignments).hashCode();
		hash += Objects.hashCode(assignments.getCategories(), assignments.getAlternatives());
		return hash;
	    }
	};
    }

    static public boolean equivalentWithCredibilities(IAssignmentsWithCredibilitiesRead assignments1,
	    IAssignmentsWithCredibilitiesRead assignments2) {
	return getEquivalenceRelationWithCredibilities().equivalent(assignments1, assignments2);
    }

    /**
     * Retrieves the credibilities of assignments of the given alternative to all categories contained in the given
     * assignments. For each category where the alternative is not assigned, a zero credibility value is used. For
     * example, if the given assignments object has three categories c1, c2, c3, and the credibilities of assignment of
     * a given alternative are 2 in c1 and 4 in c3, then this method will return for that alternative a set of
     * credibilities of, in order, 2, 0, 4.
     * 
     * @param assignments
     *            not <code>null</code>.
     * @param alternative
     *            not <code>null</code>.
     * @return not <code>null</code>. The set of categories are the same as the set of categories in the given
     *         assignments. All values are positive or zero. The returned map is a copy.
     */
    static public NavigableMap<Category, Double> getCredibilitiesWithZeroes(
	    IOrderedAssignmentsWithCredibilitiesRead assignments, Alternative alternative) {
	Preconditions.checkNotNull(assignments);
	Preconditions.checkNotNull(alternative);
	final NavigableSet<Category> allCategories = assignments.getCategories();
	final TreeMap<Category, Double> withZeroes = new TreeMap<Category, Double>(allCategories.comparator());
	final NavigableMap<Category, Double> credibilitiesNotNull = getCredibilitiesOrEmpty(assignments, alternative);
	withZeroes.putAll(credibilitiesNotNull);
	final SetView<Category> zeroes = Sets.difference(allCategories, credibilitiesNotNull.keySet());
	for (Category category : zeroes) {
	    withZeroes.put(category, Double.valueOf(0d));
	}
	return withZeroes;
    }

    /**
     * If the given alternative is not assigned, returns a new empty map with comparator set to the comparator of the
     * categories in the given assignments. This allows to use the returned map to add values relating to the same
     * categories as this assignment and benefit from the same ordering of the categories. If the given alternative is
     * assigned, this method returns the credibilities as would be returned from the assignments object.
     * 
     * @param assignments
     *            not <code>null</code>.
     * @param alternative
     *            not <code>null</code>.
     * @return not <code>null</code>, a possibly read-only copy.
     */
    public static NavigableMap<Category, Double> getCredibilitiesOrEmpty(
	    IOrderedAssignmentsWithCredibilitiesRead assignments, Alternative alternative) {
	final NavigableMap<Category, Double> credibilities = assignments.getCredibilities(alternative);
	final NavigableMap<Category, Double> credibilitiesNotNull;
	if (credibilities == null) {
	    credibilitiesNotNull = new TreeMap<Category, Double>(assignments.getCategories().comparator());
	} else {
	    credibilitiesNotNull = credibilities;
	}
	return credibilitiesNotNull;
    }

    static public void renameCategory(IOrderedAssignmentsWithCredibilities assignments, Category oldCategory,
	    Category newCategory) {
	Preconditions.checkNotNull(assignments);
	Preconditions.checkNotNull(oldCategory);
	Preconditions.checkNotNull(newCategory);
	final NavigableSet<Category> current = assignments.getCategories();
	Preconditions.checkArgument(current.contains(oldCategory));
	Preconditions.checkArgument(!current.contains(newCategory));

	final ExtentionalTotalOrder<Category> modified = ExtentionalTotalOrder.create(current);
	modified.addAfter(oldCategory, newCategory);
	assignments.setCategories(modified);
	final Set<Alternative> alternatives = ImmutableSet.copyOf(assignments.getAlternatives(oldCategory));
	for (Alternative alternative : alternatives) {
	    final Map<Category, Double> credibilities = assignments.getCredibilities(alternative);
	    final HashMap<Category, Double> extended = Maps.<Category, Double> newHashMap(credibilities);
	    extended.put(newCategory, credibilities.get(oldCategory));
	    assignments.setCredibilities(alternative, extended);
	}
	modified.remove(oldCategory);
	assignments.setCategories(modified);
    }

    /**
     * Removes all assignments to the given category if there is any, and removes the category from the set of
     * categories bound to the given assignments if it exists.
     * 
     * @param assignments
     *            not <code>null</code>.
     * @param toRemove
     *            not <code>null</code>.
     * @return <code>true</code> iff the call to this method changed the state of the given assignments, or
     *         equivalently, <code>true</code> iff the given category was contained in the given assignments.
     */
    static public boolean removeCategory(IOrderedAssignmentsWithCredibilities assignments, Category toRemove) {
	Preconditions.checkNotNull(assignments);
	Preconditions.checkNotNull(toRemove);
	final NavigableSet<Category> current = assignments.getCategories();
	if (!current.contains(toRemove)) {
	    return false;
	}
	final ExtentionalTotalOrder<Category> modified = ExtentionalTotalOrder.create(current);
	final Set<Alternative> alternatives = ImmutableSet.copyOf(assignments.getAlternatives(toRemove));
	for (Alternative alternative : alternatives) {
	    assignments.setCredibilities(alternative, null);
	}
	modified.remove(toRemove);
	assignments.setCategories(modified);
	return true;
    }

    static public void completeAssignments(Set<Alternative> alternatives, IOrderedAssignmentsToMultiple assignments) {
	final NavigableSet<Category> full = assignments.getCategories();
	for (Alternative alternative : alternatives) {
	    final NavigableSet<Category> categories = assignments.getCategories(alternative);
	    if (categories != null) {
		continue;
	    }
	    assignments.setCategories(alternative, full);
	}
    }

    /**
     * Returns a new object containing every alternative assigned in at least one of the given assignments objects, and
     * for each alternative, containing as an assignment the union of all categories to which the alternative is
     * assigned across the given assignment objects. The returned assignments object has as categories the categories
     * given as argument. These categories must be a superset of all the categories used in the given assignments
     * objects. This is required because otherwise this object has no means to know the total order over the union of
     * the categories.
     * 
     * @param allAssignments
     *            not <code>null</code>.
     * @param categories
     *            not <code>null</code>, must be a superset of all categories used in the assignments objects.
     * @param alternatives
     *            if not <code>null</code>, the set must be included in the set of all alternatives assigned in at least
     *            one of the given assignments. Those alternatives are the ones assigned in the returned union. The
     *            iteration order of the returned union matches that of the given set.
     * @return not <code>null</code>.
     */
    static public IOrderedAssignmentsToMultiple getUnion(
	    Iterable<? extends IOrderedAssignmentsToMultipleRead> allAssignments, NavigableSet<Category> categories,
	    Set<Alternative> alternatives) {
	final IOrderedAssignmentsToMultiple union = AssignmentsFactory.newOrderedAssignmentsToMultiple();
	union.setCategories(categories);
	final Set<Alternative> allAlternatives = alternatives == null ? getUnionAssignedAlternatives(allAssignments)
		: alternatives;
	for (final Alternative alternative : allAlternatives) {
	    final Set<Category> allCategories = getAllCategoriesAssignedTo(allAssignments, alternative);
	    union.setCategories(alternative, allCategories);
	}
	return union;
    }

    public static Map<Alternative, Set<Category>> getMapView(final IAssignmentsToMultipleRead assignments) {
	final Map<Alternative, Set<Category>> map = SetBackedMap.create(assignments.getAlternatives(),
		new Function<Alternative, Set<Category>>() {
		    @Override
		    public Set<Category> apply(Alternative input) {
			return assignments.getCategories(input);
		    }
		});
	return map;
    }

    public static Map<Alternative, Map<Category, Double>> getMapViewOfCredibilities(
	    final IAssignmentsWithCredibilitiesRead assignments) {
	final Map<Alternative, Map<Category, Double>> map = SetBackedMap.create(assignments.getAlternatives(),
		new Function<Alternative, Map<Category, Double>>() {
		    @Override
		    public Map<Category, Double> apply(Alternative input) {
			return assignments.getCredibilities(input);
		    }
		});
	return map;
    }

    static public void ensuresSingle(IAssignmentsWithCredibilitiesRead assignments) throws InvalidInputException {
	final Set<Alternative> alternatives = assignments.getAlternatives();
	for (Alternative alternative : alternatives) {
	    if (assignments.getCredibilities(alternative).size() != 1) {
		throw new InvalidInputException("Alternative " + alternative + " is in more than one category.");
	    }
	}
    }

    /**
     * <p>
     * The source must have each alternative assigned to only one category.
     * </p>
     * <p>
     * Overrides the target by replacing every information it contains with the information in the given source.
     * </p>
     * 
     * @param source
     *            not <code>null</code>.
     * @param target
     *            not <code>null</code>.
     * @throws InvalidInputException
     *             if the source contains at least one alternative assigned to more than one category.
     */
    static public void copyOrderedAssignmentsWithCredibilitiesToTargetSingle(
	    IOrderedAssignmentsWithCredibilitiesRead source, IOrderedAssignments target) throws InvalidInputException {
	Preconditions.checkNotNull(source);
	Preconditions.checkNotNull(target);

	target.clear();

	target.setCategories(source.getCategories());

	for (Alternative alternative : source.getAlternatives()) {
	    final Map<Category, Double> credibilities = source.getCredibilities(alternative);
	    if (credibilities.size() != 1) {
		throw new InvalidInputException("Alternative assigned to more than one categories: " + alternative
			+ ".");
	    }
	    final Category category = Iterables.getOnlyElement(credibilities.keySet());
	    target.setCategory(alternative, category);
	}
    }

    static public Equivalence<IOrderedAssignmentsToMultipleRead> getEquivalenceRelationOrderedToMultiple() {
	return new Equivalence<IOrderedAssignmentsToMultipleRead>() {
	    @Override
	    public boolean doEquivalent(IOrderedAssignmentsToMultipleRead assignments1,
		    IOrderedAssignmentsToMultipleRead assignments2) {
		if (assignments1 instanceof IOrderedAssignmentsWithCredibilitiesRead
			&& assignments2 instanceof IOrderedAssignmentsWithCredibilitiesRead) {
		    return equivalentOrderedWithCredibilities((IOrderedAssignmentsWithCredibilitiesRead) assignments1,
			    (IOrderedAssignmentsWithCredibilitiesRead) assignments2);
		}
		if (assignments1 instanceof IOrderedAssignmentsWithCredibilitiesRead
			|| assignments2 instanceof IOrderedAssignmentsWithCredibilitiesRead) {
		    return false;
		}

		final NavigableSet<Category> cats1 = assignments1.getCategories();
		final NavigableSet<Category> cats2 = assignments2.getCategories();
		if (!Iterables.elementsEqual(cats1, cats2)) {
		    return false;
		}

		for (Category category : cats1) {
		    final Set<Alternative> alternatives1 = assignments1.getAlternatives(category);
		    final Set<Alternative> alternatives2 = assignments2.getAlternatives(category);
		    if (!alternatives1.equals(alternatives2)) {
			return false;
		    }
		}

		return true;
	    }

	    @Override
	    public int doHash(IOrderedAssignmentsToMultipleRead assignments) {
		int hash = 0;
		hash += getMapView(assignments).hashCode();
		hash += Objects.hashCode(assignments.getCategories(), assignments.getAlternatives());
		return hash;
	    }
	};
    }

    static public boolean equivalentOrderedToMultiple(IOrderedAssignmentsToMultipleRead ordered1,
	    IOrderedAssignmentsToMultipleRead ordered2) {
	return getEquivalenceRelationOrderedToMultiple().equivalent(ordered1, ordered2);
    }

    static public Equivalence<IOrderedAssignmentsWithCredibilitiesRead> getEquivalenceRelationOrderedWithCredibilities() {
	return new Equivalence<IOrderedAssignmentsWithCredibilitiesRead>() {
	    @Override
	    public boolean doEquivalent(IOrderedAssignmentsWithCredibilitiesRead assignments1,
		    IOrderedAssignmentsWithCredibilitiesRead assignments2) {
		final NavigableSet<Category> cats1 = assignments1.getCategories();
		final NavigableSet<Category> cats2 = assignments2.getCategories();
		if (!Iterables.elementsEqual(cats1, cats2)) {
		    return false;
		}

		final Set<Alternative> alternatives1 = assignments1.getAlternatives();
		final Set<Alternative> alternatives2 = assignments2.getAlternatives();
		if (!alternatives1.equals(alternatives2)) {
		    return false;
		}

		for (Alternative alternative : assignments1.getAlternatives()) {
		    final Map<Category, Double> credibilities1 = assignments1.getCredibilities(alternative);
		    final Map<Category, Double> credibilities2 = assignments2.getCredibilities(alternative);
		    if (!credibilities1.equals(credibilities2)) {
			return false;
		    }
		}

		return true;
	    }

	    @Override
	    public int doHash(IOrderedAssignmentsWithCredibilitiesRead assignments) {
		int hash = 0;
		/** NB we could also use the credibilities... */
		hash += getMapView(assignments).hashCode();
		hash += Objects.hashCode(assignments.getCategories(), assignments.getAlternatives());
		return hash;
	    }
	};
    }

    static public boolean equivalentToMultiple(IAssignmentsToMultipleRead assignments1,
	    IAssignmentsToMultipleRead assignments2) {
	return getEquivalenceRelationToMultiple().equivalent(assignments1, assignments2);
    }

    /**
     * <p>
     * Overrides the target by replacing every information it contains with the information in the given source.
     * </p>
     * 
     * @param source
     *            not <code>null</code>.
     * @param target
     *            not <code>null</code>.
     */
    static public void copyAssignmentsToMultipleToTarget(IAssignmentsToMultipleRead source,
	    IAssignmentsToMultiple target) {
	checkNotNull(source);
	checkNotNull(target);

	target.clear();

	for (Alternative alternative : source.getAlternatives()) {
	    final Set<Category> assigned = source.getCategories(alternative);
	    target.setCategories(alternative, assigned);
	}
    }

    /**
     * <p>
     * Copies the given source assignments into the target. The target must have a set of categories that contain all
     * the given source assignments. The target existing assignments for alternatives not assigned in the source
     * assignments are kept and not changed by this method. The target existing assignments for alternatives that also
     * exist in the source are erased and replaced by the copy of the source assignments.
     * </p>
     * <p>
     * If the target set of categories is not a superset of the set of categories used in the source, an exception is
     * thrown, and the given target is left unchanged.
     * </p>
     * 
     * @param source
     *            not <code>null</code>.
     * @param target
     *            not <code>null</code>.
     * @throws InvalidInputException
     *             iff at least one alternative is assigned in source to a category not contained in the categories
     *             associated to the given target.
     */
    static public void copyAssignmentsWithCredibilitiesToOrderedTarget(IAssignmentsWithCredibilitiesRead source,
	    IOrderedAssignmentsWithCredibilities target) throws InvalidInputException {
	checkNotNull(source);
	checkNotNull(target);
	final Set<Category> allowed = target.getCategories();
	final Set<Category> unionCategories = CollectionUtils.union(getMapView(source).values());
	final SetView<Category> unexpected = Sets.difference(unionCategories, allowed);
	if (unexpected.size() >= 1) {
	    throw new InvalidInputException("Unexpected assignment into " + unexpected.iterator().next() + ".");
	}

	for (Alternative alternative : source.getAlternatives()) {
	    final Map<Category, Double> credibilities = source.getCredibilities(alternative);
	    target.setCredibilities(alternative, credibilities);
	}
    }

    static public Set<Category> getAllCategoriesAssignedTo(
	    Iterable<? extends IOrderedAssignmentsToMultipleRead> allAssignments, final Alternative alternative) {
	final Function<IOrderedAssignmentsToMultipleRead, NavigableSet<Category>> assignmentsToCategories = getFunctionAssignmentsToAssignedCategories(alternative);
	final Iterable<NavigableSet<Category>> allIndividualCategories = Iterables.transform(allAssignments,
		assignmentsToCategories);
	final Set<Category> allCategories = CollectionUtils.union(Iterables.filter(allIndividualCategories,
		Predicates.notNull()));
	return allCategories;
    }

    public static Function<IOrderedAssignmentsToMultipleRead, NavigableSet<Category>> getFunctionAssignmentsToAssignedCategories(
	    final Alternative alternative) {
	final Function<IOrderedAssignmentsToMultipleRead, NavigableSet<Category>> assignmentsToCategories = new Function<IOrderedAssignmentsToMultipleRead, NavigableSet<Category>>() {
	    @Override
	    public NavigableSet<Category> apply(IOrderedAssignmentsToMultipleRead input) {
		return input.getCategories(alternative);
	    }
	};
	return assignmentsToCategories;
    }

    static public IOrderedAssignments getFakeWritable(IOrderedAssignmentsRead delegateRead) {
	return new OrderedAssignmentsFromRead(delegateRead);
    }

    static public IOrderedAssignmentsWithCredibilities getFakeWriteableWithCredibilities(
	    IOrderedAssignmentsWithCredibilitiesRead delegateRead) {
	return new OrderedAssignmentsWithCredibilitiesFromRead(delegateRead);
    }

    /**
     * <p>
     * Copies the given source assignments into the target. The target must have a set of categories that contain all
     * the given source assignments. The target existing assignments for alternatives not assigned in the source
     * assignments are kept and not changed by this method. The target existing assignments for alternatives that also
     * exist in the source are erased and replaced by the copy of the source assignments.
     * </p>
     * <p>
     * If the target set of categories is not a superset of the set of categories used in the source, an exception is
     * thrown, and the given target is left unchanged.
     * </p>
     * 
     * @param source
     *            not <code>null</code>.
     * @param target
     *            not <code>null</code>.
     * @throws InvalidInputException
     *             iff at least one alternative is assigned in source to a category not contained in the categories
     *             associated to the given target.
     */
    static public void copyAssignmentsToOrderedTarget(IAssignmentsRead source, IOrderedAssignments target)
	    throws InvalidInputException {
	checkNotNull(source);
	checkNotNull(target);
	final Set<Category> allowed = target.getCategories();
	final Set<Category> unionCategories = CollectionUtils.union(getMapView(source).values());
	final SetView<Category> unexpected = Sets.difference(unionCategories, allowed);
	if (unexpected.size() >= 1) {
	    throw new InvalidInputException("Unexpected assignment into " + unexpected.iterator().next() + ".");
	}

	for (Alternative alternative : source.getAlternatives()) {
	    final Category category = source.getCategory(alternative);
	    target.setCategory(alternative, category);
	}
    }

    /**
     * <p>
     * Copies the given source assignments into the target. The target must have a set of categories that contain all
     * the given source assignments. The target existing assignments for alternatives not assigned in the source
     * assignments are kept and not changed by this method. The target existing assignments for alternatives that also
     * exist in the source are erased and replaced by the copy of the source assignments.
     * </p>
     * <p>
     * If the target set of categories is not a superset of the set of categories used in the source, an exception is
     * thrown, and the given target is left unchanged.
     * </p>
     * TODO check why exception may be thrown, consider using getCategories.
     * 
     * @param source
     *            not <code>null</code>.
     * @param target
     *            not <code>null</code>.
     * @throws InvalidInputException
     *             iff at least one alternative is assigned in source to a category not contained in the categories
     *             associated to the given target.
     */
    static public void copyAssignmentsToMultipleToOrderedTarget(IAssignmentsToMultipleRead source,
	    IOrderedAssignmentsToMultiple target) throws InvalidInputException {
	checkNotNull(source);
	checkNotNull(target);
	final Set<Category> allowed = target.getCategories();
	final Set<Category> unionCategories = CollectionUtils.union(getMapView(source).values());
	final SetView<Category> unexpected = Sets.difference(unionCategories, allowed);
	if (unexpected.size() >= 1) {
	    throw new InvalidInputException("Unexpected assignment into " + unexpected.iterator().next() + ".");
	}

	for (Alternative alternative : source.getAlternatives()) {
	    final Set<Category> categories = source.getCategories(alternative);
	    target.setCategories(alternative, categories);
	}
    }

    static public IOrderedAssignmentsToMultiple getFakeWritableToMultiple(IOrderedAssignmentsToMultipleRead delegateRead) {
	return new OrderedAssignmentsToMultipleFromRead(delegateRead);
    }

    static public IOrderedAssignmentsRead getReadView(IOrderedAssignments delegate) {
	return new OrderedAssignmentsFiltering(delegate);
    }

    static public IOrderedAssignmentsToMultipleRead getReadView(IOrderedAssignmentsToMultiple delegate) {
	return new OrderedAssignmentsToMultipleFiltering(delegate);
    }

    static public IOrderedAssignmentsToMultipleRead getEdgesView(IOrderedAssignmentsToMultipleRead delegate) {
	return new OrderedAssignmentsToMultipleEdgesView(delegate);
    }

    static public void assertEqual(IOrderedAssignmentsToMultipleRead assignments1,
	    IOrderedAssignmentsToMultipleRead assignments2, String name1, String name2) throws IllegalStateException {
	checkNotNull(assignments1, name1);
	checkNotNull(assignments2, name2);
	final NavigableSet<Category> cats1 = assignments1.getCategories();
	final NavigableSet<Category> cats2 = assignments2.getCategories();
	if (!Iterables.elementsEqual(cats1, cats2)) {
	    throw new IllegalStateException("Not same categories.");
	}

	assertEqual(assignments1.getAlternatives(), assignments2.getAlternatives(), name1, name2);
	final Set<Alternative> alternatives = assignments1.getAlternatives();
	for (Alternative alternative : alternatives) {
	    final NavigableSet<Category> categories1 = assignments1.getCategories(alternative);
	    final NavigableSet<Category> categories2 = assignments2.getCategories(alternative);
	    final Set<Category> diff = Sets.symmetricDifference(categories1, categories2);
	    if (!diff.isEmpty()) {
		final String interval1 = Categories.toIntervalString(categories1);
		final String interval2 = Categories.toIntervalString(categories2);
		final String realName2 = name2 == null ? "2" : name2;
		final String realName1 = name1 == null ? "1" : name1;
		throw new IllegalStateException("Different assignment for " + alternative + ": " + interval1
			+ " according to " + realName1 + " and " + interval2 + " according to " + realName2
			+ " (diff = " + diff + ")");
	    }
	}
    }

    static public int getSize(IOrderedAssignmentsToMultipleRead assignments) {
	int tot = 0;
	for (Alternative alternative : assignments.getAlternatives()) {
	    tot += assignments.getCategories(alternative).size();
	}
	return tot;
    }

    public static boolean addToCategories(IOrderedAssignmentsToMultiple assignments, Alternative alternative,
	    Category category) {
	final NavigableSet<Category> categories = assignments.getCategories(alternative);
	if (categories == null) {
	    return assignments.setCategories(alternative, Collections.singleton(category));
	}
	final Set<Category> augmented = Sets.newHashSet(categories);
	augmented.add(category);
	return assignments.setCategories(alternative, augmented);
    }

    public static void assertRestriction(Map<DecisionMaker, ? extends IOrderedAssignmentsToMultipleRead> assignments1,
	    Map<DecisionMaker, ? extends IOrderedAssignmentsToMultipleRead> assignments2, String name1, String name2) {
	Preconditions.checkArgument(assignments1.keySet().equals(assignments2.keySet()));
	for (DecisionMaker dm : assignments1.keySet()) {
	    final IOrderedAssignmentsToMultipleRead a2 = assignments2.get(dm);
	    final IOrderedAssignmentsToMultipleRead a1 = assignments1.get(dm);
	    AssignmentsUtils.assertRestriction(a1, a2, dm.getId() + " - " + name1, dm.getId() + " - " + name2);
	}
    }

    static public NavigableSet<Double> getCredibilityLevels(IAssignmentsWithCredibilitiesRead assignments) {
	final NavigableSet<Double> levels = new TreeSet<Double>();
	final Set<Alternative> alternatives = assignments.getAlternatives();
	for (Alternative alternative : alternatives) {
	    final Map<Category, Double> credibilities = assignments.getCredibilities(alternative);
	    levels.addAll(credibilities.values());
	}
	return levels;
    }

    /**
     * Overrides the target by replacing every information it contains with the information in the given source.
     * 
     * @param source
     *            not <code>null</code>.
     * @param target
     *            not <code>null</code>.
     * @return <code>true</code> iff the call to this method changed the contents of the given target, or equivalently,
     *         <code>false</code> iff the given target was equal to the given source.
     */
    static public boolean copyOrderedAssignmentsWithCredibilitiesToTarget(
	    IOrderedAssignmentsWithCredibilitiesRead source, IOrderedAssignmentsWithCredibilities target) {
	Preconditions.checkNotNull(source);
	Preconditions.checkNotNull(target);

	if (target.equals(source)) {
	    return false;
	}

	target.clear();

	target.setCategories(source.getCategories());

	for (Alternative alternative : source.getAlternatives()) {
	    final Map<Category, Double> credibilities = source.getCredibilities(alternative);
	    target.setCredibilities(alternative, credibilities);
	}
	return true;
    }

    public static boolean addToCredibilities(IOrderedAssignmentsWithCredibilities assignments, Alternative alternative,
	    Category category, double credibility) {
	final NavigableMap<Category, Double> credibilities = assignments.getCredibilities(alternative);
	if (credibilities == null) {
	    return assignments.setCredibilities(alternative,
		    Collections.singletonMap(category, Double.valueOf(credibility)));
	}
	final Map<Category, Double> augmented = Maps.newLinkedHashMap(credibilities);
	augmented.put(category, Double.valueOf(credibility));
	return assignments.setCredibilities(alternative, augmented);
    }

    public static IOrderedAssignmentsToMultipleRead getFilteredView(IOrderedAssignmentsToMultipleRead examples,
	    Predicate<Alternative> predicate) {
	return new OrderedAssignmentsToMultipleFiltering(examples, predicate);
    }

    public static IOrderedAssignmentsRead getFilteredView(IOrderedAssignmentsRead examples,
	    Predicate<Alternative> predicate) {
	return new OrderedAssignmentsFiltering(examples, predicate);
    }

    static public <AssignmentsType extends IOrderedAssignmentsToMultipleRead> Function<AssignmentsType, Set<Alternative>> transformAssignmentsToAlternatives() {
	return new Function<AssignmentsType, Set<Alternative>>() {
	    @Override
	    public Set<Alternative> apply(AssignmentsType input) {
		return input.getAlternatives();
	    }
	};
    }

    public static void assertEqual(Map<DecisionMaker, ? extends IOrderedAssignmentsToMultipleRead> assignments1,
	    Map<DecisionMaker, ? extends IOrderedAssignmentsToMultipleRead> assignments2, String name1, String name2) {
	Preconditions.checkArgument(assignments1.keySet().equals(assignments2.keySet()));
	for (DecisionMaker dm : assignments1.keySet()) {
	    final IOrderedAssignmentsToMultipleRead a2 = assignments2.get(dm);
	    final IOrderedAssignmentsToMultipleRead a1 = assignments1.get(dm);
	    AssignmentsUtils.assertEqual(a1, a2, dm.getId() + " - " + name1, dm.getId() + " - " + name2);
	}
    }

    /**
     * Compares the assignments of the given alternatives, and returns <code>true</code> iff the first alternative is
     * strictly better than the second one, thus, if its worst category it is assigned to is better than the best
     * category the second alternative is assigned to.
     * 
     * @param assignments
     *            not <code>null</code>.
     * @param alternative1
     *            not <code>null</code>, must be assigned.
     * @param alternative2
     *            not <code>null</code>, must be assigned.
     * @return <code>true</code> iff alternative1 is robustly better than alternative2.
     */
    static public boolean isStrictlyBetter(IOrderedAssignmentsToMultipleRead assignments, Alternative alternative1,
	    Alternative alternative2) {
	checkNotNull(assignments);
	final NavigableSet<Category> categories1 = assignments.getCategories(alternative1);
	if (categories1 == null) {
	    throw new IllegalArgumentException("Unknown " + alternative1 + ".");
	}
	final NavigableSet<Category> categories2 = assignments.getCategories(alternative2);
	if (categories2 == null) {
	    throw new IllegalArgumentException("Unknown " + alternative2 + ".");
	}
	final Category worstAlt1 = categories1.first();
	final Category bestAlt2 = categories2.last();
	final int preferred = assignments.getCategories().comparator().compare(worstAlt1, bestAlt2);
	return preferred > 0;
    }

    /**
     * Retrieves a mutable set containing the pairs of alternatives that are considered as strictly, robustly better
     * according to the given assignments.
     * 
     * @param assignments
     *            not <code>null</code>.
     * @return not <code>null</code>, may be empty.
     * @see #isStrictlyBetter(IOrderedAssignmentsToMultipleRead, Alternative, Alternative)
     */
    static public Set<Pair<Alternative, Alternative>> getStrictlyPreferredPairs(
	    IOrderedAssignmentsToMultiple assignments) {
	final Set<Alternative> alternatives = assignments.getAlternatives();
	final Set<Pair<Alternative, Alternative>> preferredPairs = Sets.newLinkedHashSet();
	for (Alternative alt1 : alternatives) {
	    for (Alternative alt2 : alternatives) {
		final boolean alt1Better = AssignmentsUtils.isStrictlyBetter(assignments, alt1, alt2);
		if (alt1Better) {
		    final Pair<Alternative, Alternative> pair = new Pair<Alternative, Alternative>(alt1, alt2);
		    preferredPairs.add(pair);
		}
	    }
	}
	return preferredPairs;
    }

    /**
     * Retrieves a description of all data contained in the given assignments object in a packed, debug, form. This
     * method is suitable for use in a {@link #toString()} method.
     * 
     * @param assignments
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public String getShortDescription(final IAssignmentsWithCredibilitiesRead assignments) {
	final ToStringHelper helper = Objects.toStringHelper(assignments);

	final Set<Category> categories = assignments.getCategories();
	final Iterable<String> categoriesAsStrings = Iterables.transform(categories,
		new ExportSettings.CategoryToName());
	final String categoriesString = Joiner.on(", ").join(categoriesAsStrings);
	helper.add("categories", "{" + categoriesString + "}");

	final Function<Alternative, String> transformAssignmentToDescription = new Function<Alternative, String>() {
	    private static final String RIGHT_ARROW = "\u2192";

	    @Override
	    public String apply(Alternative alternative) {
		final Map<Category, Double> credibilities = assignments.getCredibilities(alternative);
		final Map<String, Double> credibilitiesWithStringKeys = CollectionUtils.transformKeys(credibilities,
			new ExportSettings.CategoryToName());
		final String credibitilitiesString = "["
			+ Joiner.on(", ").withKeyValueSeparator(":").join(credibilitiesWithStringKeys) + "]";

		return alternative.getId() + RIGHT_ARROW + credibitilitiesString;
	    }
	};
	final Iterable<String> assignmentsDescriptions = Iterables.transform(assignments.getAlternatives(),
		transformAssignmentToDescription);
	final String descrAssignments = Joiner.on("; ").join(assignmentsDescriptions);
	helper.add("assignments", "{" + descrAssignments + "}");
	return helper.toString();
    }

    /**
     * Retrieves a description of all data contained in the given assignments object in a packed, debug, form. This
     * method is suitable for use in a {@link #toString()} method.
     * 
     * @param assignments
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public String getShortDescription(final IAssignmentsToMultipleRead assignments) {
	final ToStringHelper helper = Objects.toStringHelper(assignments);

	{
	    final Set<Category> categories = assignments.getCategories();
	    final Iterable<String> categoriesAsStrings = Iterables.transform(categories,
		    new ExportSettings.CategoryToName());
	    final String categoriesString = Joiner.on(", ").join(categoriesAsStrings);
	    helper.add("categories", "{" + categoriesString + "}");
	}

	final Function<Alternative, String> transformAssignmentToDescription = new Function<Alternative, String>() {
	    private static final String RIGHT_ARROW = "\u2192";

	    @Override
	    public String apply(Alternative alternative) {
		final Set<Category> categories = assignments.getCategories(alternative);
		final Iterable<String> categoriesAsStrings = Iterables.transform(categories,
			new ExportSettings.CategoryToName());
		final String categoriesString = Joiner.on(", ").join(categoriesAsStrings);
		return alternative.getId() + RIGHT_ARROW + ("[" + categoriesString + "]");
	    }
	};
	final Iterable<String> assignmentsDescriptions = Iterables.transform(assignments.getAlternatives(),
		transformAssignmentToDescription);
	final String descrAssignments = Joiner.on("; ").join(assignmentsDescriptions);
	helper.add("assignments", "{" + descrAssignments + "}");
	return helper.toString();
    }

    /**
     * <p>
     * The source must have each alternative assigned to only one category.
     * </p>
     * <p>
     * Overrides the target by replacing every information it contains with the information in the given source.
     * </p>
     * 
     * @param source
     *            not <code>null</code>.
     * @param target
     *            not <code>null</code>.
     * @throws InvalidInputException
     *             if the source contains at least one alternative assigned to more than one category.
     */
    static public void copyAssignmentsToMultipleToTargetSingle(IAssignmentsToMultipleRead source, IAssignments target)
	    throws InvalidInputException {
	Preconditions.checkNotNull(source);
	Preconditions.checkNotNull(target);

	target.clear();

	for (Alternative alternative : source.getAlternatives()) {
	    final Set<Category> assigned = source.getCategories(alternative);
	    if (assigned.size() != 1) {
		throw new InvalidInputException("Alternative assigned to more than one categories: " + alternative
			+ ".");
	    }
	    final Category category = Iterables.getOnlyElement(assigned);
	    target.setCategory(alternative, category);
	}
    }

    public static NavigableSet<Category> getUsedCategories(IOrderedAssignmentsToMultipleRead assignments) {
	final TreeSet<Category> used = new TreeSet<Category>(assignments.getCategories().comparator());
	for (Alternative alternative : assignments.getAlternatives()) {
	    final NavigableSet<Category> categories = assignments.getCategories(alternative);
	    used.addAll(categories);
	}
	return used;
    }

    static public IOrderedAssignmentsToMultiple getCompressed(Collection<IOrderedAssignmentsToMultiple> allAssignments) {
	final IOrderedAssignmentsToMultiple compressed = AssignmentsFactory.newOrderedAssignmentsToMultiple();
	final ImmutableSet<NavigableSet<Category>> allCategories = ImmutableSet.copyOf(Collections2.transform(
		allAssignments, new Function<IOrderedAssignmentsToMultiple, NavigableSet<Category>>() {
		    @Override
		    public NavigableSet<Category> apply(IOrderedAssignmentsToMultiple input) {
			return input.getCategories();
		    }
		}));
	final NavigableSet<Category> theCategories = Iterables.getOnlyElement(allCategories);
	compressed.setCategories(theCategories);
	final Set<Alternative> allAlternatives = AssignmentsUtils.getUnionAssignedAlternatives(allAssignments);
	for (Alternative alternative : allAlternatives) {
	    final Function<IOrderedAssignmentsToMultipleRead, NavigableSet<Category>> toAssignedCategories = AssignmentsUtils
		    .getFunctionAssignmentsToAssignedCategories(alternative);
	    final Collection<NavigableSet<Category>> allAssignedCategories = Collections2.transform(allAssignments,
		    toAssignedCategories);
	    final Collection<NavigableSet<Category>> allAssNonNull = Collections2.filter(allAssignedCategories,
		    Predicates.notNull());
	    assert allAssignedCategories.size() >= 1;
	    final ImmutableSet<NavigableSet<Category>> allDifferentAssignedCategories = ImmutableSet
		    .copyOf(allAssNonNull);
	    assert allDifferentAssignedCategories.size() >= 1;
	    if (allDifferentAssignedCategories.size() == 1) {
		final NavigableSet<Category> assignment = Iterables.getOnlyElement(allDifferentAssignedCategories);
		compressed.setCategories(alternative, assignment);
	    } else {
		throw new IllegalArgumentException("Conflicting assignments for " + alternative + ": "
			+ allDifferentAssignedCategories + ".");
	    }
	}

	return compressed;
    }

    static public Function<IAssignmentsToMultipleRead, Set<Alternative>> getFunctionAssignmentsToAssignedAlternatives() {
	return new Function<IAssignmentsToMultipleRead, Set<Alternative>>() {
	    @Override
	    public Set<Alternative> apply(IAssignmentsToMultipleRead input) {
		return input.getAlternatives();
	    }
	};
    }

    /**
     * <p>
     * Overrides the target by replacing every information it contains with the information in the given source.
     * </p>
     * 
     * @param source
     *            not <code>null</code>.
     * @param target
     *            not <code>null</code>.
     */
    static public void copyAssignmentsToTarget(IAssignmentsRead source, IAssignments target) {
	Preconditions.checkNotNull(source);
	Preconditions.checkNotNull(target);

	target.clear();

	for (Alternative alternative : source.getAlternatives()) {
	    final Category assigned = source.getCategory(alternative);
	    target.setCategory(alternative, assigned);
	}
    }
}
