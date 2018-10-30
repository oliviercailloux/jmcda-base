package org.decisiondeck.xmcda_oo.structure.category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decision_deck.utils.collection.extensional_order.ExtentionalTotalOrder;
import org.decisiondeck.jmcda.structure.sorting.assignment.VersatileAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.VersatileOrderedAssignments;
import org.junit.Test;

public class VersatileAssignmentsTest {
    @Test
    public void testBasics() throws Exception {
	final VersatileAssignments versatile = new VersatileAssignments();
	final Alternative a1 = new Alternative("a1");
	final Alternative a2 = new Alternative("a2");
	final Category c1 = new Category("c1");
	final Category c2 = new Category("c2");

	assertNull(versatile.getCategory(a1));
	assertNull(versatile.getCategories(a1));
	assertNull(versatile.getCredibilities(a1));
	assertTrue(versatile.isCrisp());

	versatile.setCategory(a1, c1);
	assertEquals(c1, versatile.getCategory(a1));
	assertEquals(Collections.singleton(c1), versatile.getCategories(a1));
	assertEquals(Collections.singletonMap(c1, Double.valueOf(1d)), versatile.getCredibilities(a1));
	assertTrue(versatile.isCrisp());

	versatile.setCategories(a2, Collections.singleton(c1));
	assertEquals(c1, versatile.getCategory(a2));
	assertEquals(Collections.singleton(c1), versatile.getCategories(a2));
	assertEquals(Collections.singletonMap(c1, Double.valueOf(1d)), versatile.getCredibilities(a2));
	assertTrue(versatile.isCrisp());

	final Map<Category, Double> credibilities = new HashMap<Category, Double>();
	credibilities.put(c1, Double.valueOf(0.4d));
	credibilities.put(c2, Double.valueOf(0.6d));
	versatile.setCredibilities(a1, credibilities);
	assertEquals(credibilities.keySet(), versatile.getCategories(a1));
	assertEquals(credibilities, versatile.getCredibilities(a1));
	assertEquals(Collections.singletonMap(c1, Double.valueOf(1d)), versatile.getCredibilities(a2));
	assertFalse(versatile.isCrisp());

	versatile.setCategory(a1, c2);
	assertEquals(Collections.singleton(c2), versatile.getCategories(a1));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetAsOnlyOneCategory() throws Exception {
	final VersatileAssignments versatile = new VersatileAssignments();
	final Alternative a1 = new Alternative("a1");
	final Category c1 = new Category("c1");
	final Category c2 = new Category("c2");
	final Map<Category, Double> credibilities = new HashMap<Category, Double>();
	credibilities.put(c1, Double.valueOf(0.4d));
	credibilities.put(c2, Double.valueOf(0.6d));
	versatile.setCredibilities(a1, credibilities);
	versatile.getCategory(a1);
    }

    @Test
    public void testSorted() throws Exception {
	final VersatileOrderedAssignments versatile = new VersatileOrderedAssignments();
	final Alternative a1 = new Alternative("a1");
	/** Strange names yields a different order than the alphabetical one. */
	final Category c1 = new Category("cZ1");
	final Category c2 = new Category("cA2");
	final Category c3 = new Category("cF3");
	final ExtentionalTotalOrder<Category> categories = ExtentionalTotalOrder.create();
	categories.addAsHighest(c1);
	categories.addAsHighest(c2);
	categories.addAsHighest(c3);
	versatile.setCategories(categories);

	final Map<Category, Double> credibilities = new HashMap<Category, Double>();
	credibilities.put(c1, Double.valueOf(0.4d));
	credibilities.put(c2, Double.valueOf(0.6d));

	final SortedSet<Category> catsSorted = versatile.getCategoriesSorted();
	assertTrue(catsSorted.size() == 3);
	assertEquals(c1, catsSorted.first());
	assertEquals(c3, catsSorted.last());

	versatile.setCredibilities(a1, credibilities);
	final SortedSet<Category> catsSortedOneAlt = versatile.getCategoriesSorted();
	assertTrue(catsSortedOneAlt.size() == 3);
	assertEquals(c1, catsSortedOneAlt.first());
	assertEquals(c3, catsSortedOneAlt.last());

	final SortedMap<Category, Double> credibilitiesSorted = versatile.getCredibilitiesSorted(a1);
	assertTrue(credibilitiesSorted.size() == 2);
	assertEquals(c1, credibilitiesSorted.firstKey());
	assertEquals(c2, credibilitiesSorted.lastKey());
	assertEquals(Double.valueOf(0.6d), credibilitiesSorted.get(c2));
	assertEquals(credibilities, versatile.getCredibilities(a1));
    }

    @Test
    public void testBasicsOrdered() throws Exception {
	final VersatileOrderedAssignments versatile = new VersatileOrderedAssignments();
	final Alternative a1 = new Alternative("a1");
	final Alternative a2 = new Alternative("a2");
	/** Strange names yields a different order than the alphabetical one. */
	final Category c1 = new Category("cZ1");
	final Category c2 = new Category("cA2");
	final Category c3 = new Category("cF3");
	final ExtentionalTotalOrder<Category> categories = ExtentionalTotalOrder.create();
	categories.addAsHighest(c1);
	categories.addAsHighest(c2);
	categories.addAsHighest(c3);
	versatile.setCategories(categories);

	assertNull(versatile.getCategory(a1));
	assertNull(versatile.getCategories(a1));
	assertNull(versatile.getCredibilities(a1));
	assertTrue(versatile.isCrisp());

	versatile.setCategory(a1, c1);
	assertEquals(c1, versatile.getCategory(a1));
	assertEquals(Collections.singleton(c1), versatile.getCategories(a1));
	assertEquals(Collections.singletonMap(c1, Double.valueOf(1d)), versatile.getCredibilities(a1));
	assertTrue(versatile.isCrisp());

	versatile.setCategories(a2, Collections.singleton(c1));
	assertEquals(c1, versatile.getCategory(a2));
	assertEquals(Collections.singleton(c1), versatile.getCategories(a2));
	assertEquals(Collections.singletonMap(c1, Double.valueOf(1d)), versatile.getCredibilities(a2));
	assertTrue(versatile.isCrisp());

	final Map<Category, Double> credibilities = new HashMap<Category, Double>();
	credibilities.put(c1, Double.valueOf(0.4d));
	credibilities.put(c2, Double.valueOf(0.6d));
	versatile.setCredibilities(a1, credibilities);
	assertEquals(credibilities.keySet(), versatile.getCategories(a1));
	assertEquals(credibilities, versatile.getCredibilities(a1));
	assertEquals(Collections.singletonMap(c1, Double.valueOf(1d)), versatile.getCredibilities(a2));
	assertFalse(versatile.isCrisp());

	versatile.setCategory(a1, c2);
	assertEquals(Collections.singleton(c2), versatile.getCategories(a1));
    }

    @Test
    public void testCategories() throws Exception {
	final VersatileAssignments versatile = new VersatileAssignments();
	final Alternative a1 = new Alternative("a1");
	final Alternative a2 = new Alternative("a2");
	final Category c1 = new Category("c1");
	final Category c2 = new Category("c2");
	final Set<Category> twoCategories = new HashSet<Category>();
	twoCategories.add(c1);
	twoCategories.add(c2);

	versatile.setCategory(a1, c1);
	assertEquals(Collections.singleton(c1), versatile.getCategories());
	versatile.setCategory(a2, c1);
	assertEquals(Collections.singleton(c1), versatile.getCategories());
	versatile.setCategory(a1, c1);
	assertEquals(Collections.singleton(c1), versatile.getCategories());
	versatile.setCategory(a2, c1);
	assertEquals(Collections.singleton(c1), versatile.getCategories());
	versatile.setCategory(a2, c2);
	assertEquals(twoCategories, versatile.getCategories());

	versatile.setCategory(a1, null);
	assertEquals(Collections.singleton(c2), versatile.getCategories());

	final ExtentionalTotalOrder<Category> categories = ExtentionalTotalOrder.create();
	categories.addAsHighest(c2);
	versatile.setCategories(categories);
	categories.addAsHighest(c1);
	assertEquals(Collections.singleton(c2), versatile.getCategories());

	versatile.setCategories(categories);
	assertEquals(twoCategories, versatile.getCategories());
	versatile.setCategory(a1, c1);
	assertEquals(twoCategories, versatile.getCategories());
	versatile.setCategory(a1, null);
	versatile.setCategory(a2, null);
	assertEquals(twoCategories, versatile.getCategories());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetAsOnlyOneCategoryOrdered() throws Exception {
	final VersatileOrderedAssignments versatile = new VersatileOrderedAssignments();
	final Alternative a1 = new Alternative("a1");
	/** Strange names yields a different order than the alphabetical one. */
	final Category c1 = new Category("cZ1");
	final Category c2 = new Category("cA2");
	final Category c3 = new Category("cF3");
	final ExtentionalTotalOrder<Category> categories = ExtentionalTotalOrder.create();
	categories.addAsHighest(c1);
	categories.addAsHighest(c2);
	categories.addAsHighest(c3);
	versatile.setCategories(categories);

	final Map<Category, Double> credibilities = new HashMap<Category, Double>();
	credibilities.put(c1, Double.valueOf(0.4d));
	credibilities.put(c2, Double.valueOf(0.6d));
	versatile.setCredibilities(a1, credibilities);
	versatile.getCategory(a1);
    }

    @Test(expected = IllegalStateException.class)
    public void testNoCategoriesSorting() throws Exception {
	final VersatileOrderedAssignments versatile = new VersatileOrderedAssignments();
	final Alternative a1 = new Alternative("a1");
	final Category c1 = new Category("c1");

	versatile.setCategory(a1, c1);
    }
}
