package org.decisiondeck.xmcda_oo.structure.category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decision_deck.jmcda.structure.sorting.mess.CowOrderedAssignmentsWithCredibilities;
import org.decision_deck.utils.collection.extensional_order.ExtentionalTotalOrder;
import org.junit.Test;

public class CowOrderedAssignmentsTest {
    @Test
    public void testProtect() throws Exception {
	final CowOrderedAssignmentsWithCredibilities versatile = new CowOrderedAssignmentsWithCredibilities();
	final Alternative a1 = new Alternative("a1");
	final Alternative a2 = new Alternative("a2");
	final Category c1 = new Category("c1");
	final Category c2 = new Category("c2");
	/** Strange names yields a different order than the alphabetical one. */
	final ExtentionalTotalOrder<Category> categories = ExtentionalTotalOrder.create();
	categories.addAsHighest(c1);
	categories.addAsHighest(c2);
	versatile.setCategories(categories);

	final Map<Category, Double> mapC1 = Collections.singletonMap(c1, Double.valueOf(1d));
	final Map<Category, Double> mapC2 = Collections.singletonMap(c2, Double.valueOf(1d));

	assertNull(versatile.getCredibilities(a1));
	assertTrue(versatile.isCrisp());

	final CowOrderedAssignmentsWithCredibilities empty = versatile.getProtectedCopy();

	versatile.setCredibilities(a1, mapC1);
	versatile.setCredibilities(a2, mapC1);

	assertEquals(Collections.singleton(c1), versatile.getCredibilities(a1).keySet());
	assertEquals(Collections.singleton(c1), versatile.getCredibilities(a2).keySet());
	assertEquals(mapC1, versatile.getCredibilities(a1));
	assertEquals(mapC1, versatile.getCredibilities(a2));
	assertTrue(versatile.isCrisp());
	final CowOrderedAssignmentsWithCredibilities stillCrisp = versatile.getProtectedCopy();

	final Map<Category, Double> credibilities = new HashMap<Category, Double>();
	credibilities.put(c1, Double.valueOf(0.4d));
	credibilities.put(c2, Double.valueOf(0.6d));
	versatile.setCredibilities(a1, credibilities);
	assertEquals(credibilities.keySet(), versatile.getCredibilities(a1).keySet());
	assertEquals(credibilities, versatile.getCredibilities(a1));
	assertEquals(Collections.singletonMap(c1, Double.valueOf(1d)), versatile.getCredibilities(a2));
	assertFalse(versatile.isCrisp());

	assertNull(empty.getCredibilities(a1));
	assertTrue(empty.isCrisp());

	assertEquals(Collections.singleton(c1), stillCrisp.getCredibilities(a1).keySet());
	assertEquals(Collections.singleton(c1), stillCrisp.getCredibilities(a2).keySet());
	assertEquals(Collections.singletonMap(c1, Double.valueOf(1d)), stillCrisp.getCredibilities(a1));
	assertEquals(Collections.singletonMap(c1, Double.valueOf(1d)), stillCrisp.getCredibilities(a2));
	assertTrue(stillCrisp.isCrisp());

	final CowOrderedAssignmentsWithCredibilities empty2 = empty.getProtectedCopy();

	empty.setCredibilities(a1, mapC2);
	assertEquals(Collections.singleton(c2), empty.getCredibilities(a1).keySet());

	assertNull(empty2.getCredibilities(a1));
	assertTrue(empty2.isCrisp());
    }

    @Test(expected = IllegalStateException.class)
    public void testNoOrder() throws Exception {
	final CowOrderedAssignmentsWithCredibilities versatile = new CowOrderedAssignmentsWithCredibilities();
	final Alternative a1 = new Alternative("a1");
	final Category c1 = new Category("c1");
	final Map<Category, Double> mapC1 = Collections.singletonMap(c1, Double.valueOf(1d));

	versatile.setCredibilities(a1, mapC1);
    }
}
