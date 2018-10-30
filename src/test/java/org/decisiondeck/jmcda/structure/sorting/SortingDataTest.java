package org.decisiondeck.jmcda.structure.sorting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.problem.data.ISortingData;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataWithOrder;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class SortingDataTest {
    @Test
    public void testAdd() throws Exception {
	final SortingDataImpl data = new SortingDataImpl();
	data.getCriteria().add(new Criterion("ploum"));
	assertEquals(1, data.getCriteria().size());
	data.getAlternatives().add(new Alternative("ploum"));
	assertEquals(1, data.getCriteria().size());
	assertEquals(1, data.getAllAlternatives().size());
    }

    private void testWeightsPrecision() {
	final NumberFormat formatter = NumberFormat.getNumberInstance();
	formatter.setMinimumFractionDigits(50);
	final double w1 = 0.2d;
	final double w2 = 0.2d;
	final double w3 = 0.1d;
	final double w4 = 0.1d;
	final double w5 = 0.1d;
	final double w6 = 0.2d;
	final double w7 = 0.1d;
	final double w8 = 0d;
	final double sum = w1 + w2 + w3 + w4 + w5 + w6 + w7 + w8;
	final double sumNorm = (w1 / sum) + (w2 / sum) + (w3 / sum) + (w4 / sum) + (w5 / sum) + (w6 / sum) + (w7 / sum)
		+ (w8 / sum);
	System.out.println(formatter.format(w1));
	System.out.println(formatter.format(w2));
	System.out.println(formatter.format(w3));
	System.out.println(formatter.format(sum));
	System.out.println(formatter.format(sumNorm));
	System.out.println(sum == 1d);
	System.out.println(0.2d + 0.2d + 0.1d + 0.1d + 0.1d + 0.1d + 0.1d + 0.1d == 1d);
    }

    @Test
    public void testWithGivenOrder() throws Exception {
	final Alternative p1 = new Alternative("p1");
	final Alternative p2 = new Alternative("p2");
	final Alternative p3 = new Alternative("p3");
	final Category c1 = new Category("c1");
	final SortingDataWithOrder data = new SortingDataWithOrder();
	data.setProfilesOrder(Arrays.asList(new Alternative[] { p3, p1, p2 }));
	data.getProfiles().add(p3);
	data.getCatsAndProfs().addProfile(p2);
	data.getCatsAndProfs().setCategoryDown(p2, c1);
	data.getCatsAndProfs().setProfileDown("c1", p1);
	/** Now we should have: (p1, c1, p2 as cats) and p3, p1, p2. */
	assertEquals(3, data.getProfiles().size());
	assertEquals(2, data.getCatsAndProfs().getProfiles().size());
	assertEquals(3, data.getAllAlternatives().size());
	final Iterator<Alternative> i1 = data.getProfiles().iterator();
	assertEquals(p3, i1.next());
	assertEquals(p1, i1.next());
	assertEquals(p2, i1.next());
	assertFalse(i1.hasNext());

	final Iterator<Alternative> i2 = data.getProfiles().iterator();
	assertEquals(p3, i2.next());
	assertEquals(p1, i2.next());
	i2.remove();
	assertTrue(i2.hasNext());
	assertEquals(p2, i2.next());
	/** p3, p2. */

	assertEquals(2, data.getAllAlternatives().size());
	assertEquals(2, data.getProfiles().size());
	assertEquals(1, data.getCatsAndProfs().getProfiles().size());

	final Iterator<Alternative> i3 = data.getAllAlternatives().iterator();
	assertEquals(p3, i3.next());
	assertEquals(p2, i3.next());
	assertFalse(i3.hasNext());
    }

    @Test
    public void testAddThroughCatSorted() throws Exception {
	final Alternative p1 = new Alternative("p1");
	final Alternative p2 = new Alternative("p2");
	final ISortingData data = new SortingDataWithOrder();
	data.getCatsAndProfs().addProfile(p1);
	assertEquals(1, data.getProfiles().size());
	assertTrue(data.getProfiles().contains(p1));
	assertFalse(data.getProfiles().contains(p2));
	final Iterator<Alternative> ip = data.getProfiles().iterator();
	assertTrue("empty iter", ip.hasNext());
	assertNotNull(ip.next());
	assertFalse(ip.hasNext());
	assertEquals("pbl with all alternatives", 1, data.getAllAlternatives().size());
    }

    @Test
    public void testAddThroughCatAllAlts() throws Exception {
	final Alternative p1 = new Alternative("p1");
	final Alternative p2 = new Alternative("p2");
	final ISortingData data = new SortingDataImpl();
	data.getCatsAndProfs().addProfile(p1);
	assertEquals(1, data.getProfiles().size());
	assertTrue(data.getProfiles().contains(p1));
	assertFalse(data.getProfiles().contains(p2));
	final Iterator<Alternative> ip = data.getProfiles().iterator();
	assertTrue(ip.hasNext());
	assertNotNull(ip.next());
	assertFalse(ip.hasNext());
	assertEquals("pbl with all alternatives", 1, data.getAllAlternatives().size());
    }

    @Test
    public void testWithOrder() throws Exception {
	final Alternative a2 = new Alternative("a2");
	final Alternative a3 = new Alternative("a3");
	final Alternative a1 = new Alternative("a1");
	final SortingDataWithOrder data = new SortingDataWithOrder();
	data.getCriteria().add(new Criterion("ploum"));
	assertEquals(1, data.getCriteria().size());
	data.getAlternatives().add(a2);
	data.getAlternatives().add(a3);
	data.getAlternatives().add(a1);
	assertEquals(1, data.getCriteria().size());
	assertEquals(3, data.getAllAlternatives().size());
	final Iterator<Alternative> i1 = data.getAlternatives().iterator();
	assertEquals(a2, i1.next());
	assertEquals(a3, i1.next());
	assertEquals(a1, i1.next());
	assertFalse(i1.hasNext());

	final Iterator<Alternative> i2 = data.getAlternatives().iterator();
	assertEquals(a2, i2.next());
	assertEquals(a3, i2.next());
	i2.remove();
	assertTrue(i2.hasNext());

	assertEquals(2, data.getAllAlternatives().size());
	final Iterator<Alternative> i3 = data.getAlternatives().iterator();
	assertEquals(a2, i3.next());
	assertEquals(a1, i3.next());
	assertFalse(i3.hasNext());

	data.setAlternativesOrderByDelegate();

	assertEquals(2, data.getAllAlternatives().size());
	final Iterator<Alternative> i4 = data.getAlternatives().iterator();
	assertNotNull(i4.next());
	assertNotNull(i4.next());
	assertFalse(i4.hasNext());

	final Comparator<Alternative> natural = Ordering.natural();
	data.setAlternativesComparator(natural);
	assertEquals(2, data.getAllAlternatives().size());
	final Iterator<Alternative> i5 = data.getAlternatives().iterator();
	assertEquals(a1, i5.next());
	assertEquals(a2, i5.next());
	assertFalse(i5.hasNext());

	data.getAlternatives().add(a3);
	assertEquals(3, data.getAlternatives().size());
	final Iterator<Alternative> i7 = data.getAlternatives().iterator();
	assertEquals(a1, i7.next());
	assertEquals(a2, i7.next());
	assertEquals(a3, i7.next());
	assertFalse(i7.hasNext());
	i7.remove();
	assertEquals(2, data.getAllAlternatives().size());

	final Comparator<Alternative> rev = Ordering.natural().reverse();
	data.setAlternativesComparator(rev);
	assertEquals(2, data.getAlternatives().size());
	final Iterator<Alternative> i6 = data.getAlternatives().iterator();
	assertEquals(a2, i6.next());
	assertEquals(a1, i6.next());
	assertFalse(i6.hasNext());
	data.getAlternatives().add(a3);
	assertEquals(3, data.getAlternatives().size());

	final Iterator<Alternative> i8 = data.getAlternatives().iterator();
	assertEquals(a3, i8.next());
	assertEquals(a2, i8.next());
	assertEquals(a1, i8.next());
	assertFalse(i8.hasNext());
	i8.remove();
	assertEquals(2, data.getAllAlternatives().size());

	final List<Alternative> order = Lists.newLinkedList();
	order.add(a2);
	order.add(a1);
	order.add(a3);

	data.setAlternativesOrder(order);
	assertEquals(2, data.getAlternatives().size());
	final Iterator<Alternative> i9 = data.getAlternatives().iterator();
	assertEquals(a2, i9.next());
	assertEquals(a3, i9.next());
	assertFalse(i9.hasNext());
	data.getAlternatives().add(a1);
	assertEquals(3, data.getAlternatives().size());

	final Iterator<Alternative> i10 = data.getAlternatives().iterator();
	assertEquals(a2, i10.next());
	assertEquals(a1, i10.next());
	assertEquals(a3, i10.next());
	assertFalse(i10.hasNext());
	i10.remove();
	assertEquals(2, data.getAllAlternatives().size());

    }

    @Test
    public void testWithPreferenceOrder() throws Exception {
	final Alternative p1 = new Alternative("p1");
	final Alternative p2 = new Alternative("p2");
	final Alternative p3 = new Alternative("p3");
	final Category c1 = new Category("c1");
	final SortingDataWithOrder data = new SortingDataWithOrder();
	data.getCriteria().add(new Criterion("ploum"));
	assertEquals(1, data.getCriteria().size());
	data.getProfiles().add(p3);
	data.getCatsAndProfs().addProfile(p2);
	data.getCatsAndProfs().setCategoryDown(p2, c1);
	data.getCatsAndProfs().setProfileDown("c1", p1);
	/** Now we should have: p1, c1, p2; p3. */
	assertEquals(1, data.getCriteria().size());
	assertEquals(3, data.getProfiles().size());
	assertEquals(3, data.getAllAlternatives().size());
	final Iterator<Alternative> i1 = data.getProfiles().iterator();
	assertEquals(p1, i1.next());
	assertEquals(p2, i1.next());
	assertEquals(p3, i1.next());
	assertFalse(i1.hasNext());

	final Iterator<Alternative> i2 = data.getProfiles().iterator();
	assertEquals(p1, i2.next());
	assertEquals(p2, i2.next());
	i2.remove();
	assertTrue(i2.hasNext());
	assertEquals(p3, i2.next());
	/** p1, c1; p3. */

	assertEquals(2, data.getAllAlternatives().size());
	assertEquals(2, data.getProfiles().size());
	assertEquals(1, data.getCatsAndProfs().getProfiles().size());

	final Iterator<Alternative> i3 = data.getAllAlternatives().iterator();
	assertEquals(p1, i3.next());
	assertEquals(p3, i3.next());
	assertFalse(i3.hasNext());

	data.getCatsAndProfs().setProfileDown("c1", p2);
	/** p2, c1; p1, p3. */
	assertEquals(3, data.getProfiles().size());
	final Iterator<Alternative> i4 = data.getAllAlternatives().iterator();
	assertEquals(p2, i4.next());
	assertEquals(p1, i4.next());
	assertEquals(p3, i4.next());
	assertFalse(i4.hasNext());

	data.getProfiles().add(p1);
	/** p2, c1; p1, p3. */

	assertEquals(3, data.getProfiles().size());
	assertEquals(1, data.getCatsAndProfs().getProfiles().size());
	final Iterator<Alternative> i5 = data.getProfiles().iterator();
	assertEquals(p2, i5.next());
	assertEquals(p1, i5.next());
	assertEquals(p3, i5.next());
	assertFalse(i5.hasNext());
    }
}
