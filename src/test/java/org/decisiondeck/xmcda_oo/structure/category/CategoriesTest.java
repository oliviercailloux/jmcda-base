package org.decisiondeck.xmcda_oo.structure.category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NavigableSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Categories;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.junit.Test;

public class CategoriesTest {
    @Test
    public void nothingAddCatAndProfs() throws Exception {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	assertEquals(0, cats.getCategories().size());
	assertEquals(0, cats.getProfiles().size());
	assertEquals(null, cats.getCategoryDown(getP1()));
	assertEquals(null, cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getProfileDown("c1"));
	assertEquals(null, cats.getProfileUp("c1"));

	cats.addCategory(getC1Full());

	assertEquals(1, cats.getCategories().size());
	assertIdentical(getC1Full(), cats.getCategories().first());
	assertEquals(2, cats.getProfiles().size());

	assertEquals(null, cats.getCategoryDown(getP1()));
	assertIdentical(getC1Full(), cats.getCategoryUp(getP1()));
	assertIdentical(getC1Full(), cats.getCategoryDown(getP2()));
	assertEquals(null, cats.getCategoryUp(getP2()));
	assertEquals(getP1(), cats.getProfileDown("c1"));
	assertEquals(getP2(), cats.getProfileUp("c1"));
    }

    @Test
    public void testRemove() throws Exception {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	assertFalse(cats.removeProfile(getP("blah")));
	cats.addCategory(getC1Full());

	assertEquals(1, cats.getCategories().size());
	assertEquals(2, cats.getProfiles().size());
	assertTrue(cats.removeCategory("c1"));
	assertFalse(cats.removeCategory("c2"));
	assertEquals(2, cats.getProfiles().size());
	assertEquals(0, cats.getCategories().size());

	assertTrue(cats.removeProfile(getP1()));
	assertFalse(cats.removeProfile(getP("blah")));
	assertEquals(1, cats.getProfiles().size());
	assertTrue(cats.removeProfile(getP2()));
	assertEquals(0, cats.getProfiles().size());
	assertTrue(cats.isEmpty());

	cats.addCategory(getC1WithP1Up());
	cats.addProfile(getP2());
	cats.setCategoryUp(getP2(), getC2());
	cats.addProfile(getP("p3"));
	/** Now we have: c1, p1, p2, c2, p3. */

	assertFalse(cats.removeProfile(getP("blah")));
	assertTrue(cats.removeProfile(getP1()));
	/** Now we have: c1, p2, c2, p3. */

	assertEquals(2, cats.getCategories().size());
	Iterator<Category> catsRd = cats.getCategories().iterator();
	assertIdentical(getC1WithP2Up(), catsRd.next());
	final Category c2Full = new Category("c2", getP("p2"), getP("p3"));
	assertIdentical(c2Full, catsRd.next());

	assertEquals(2, cats.getProfiles().size());
	Iterator<Alternative> profiles = cats.getProfiles().iterator();
	assertEquals(getP2(), profiles.next());
	assertEquals(getP("p3"), profiles.next());
	assertFalse(profiles.hasNext());

	assertIdentical(getC2Full(), cats.getCategoryUp(getP2()));
	assertIdentical(getC1WithP2Up(), cats.getCategoryDown(getP2()));
	assertEquals(getP("p3"), cats.getProfileUp("c2"));

	assertTrue(cats.removeCategory("c2"));
	/** Now we have: c1, p2, p3. */
	assertIdentical(getC1WithP2Up(), cats.getCategoryDown(getP2()));
	assertIdentical(null, cats.getCategoryUp(getP2()));
	assertEquals(null, cats.getCategoryUp(getP("p3")));
	assertEquals(null, cats.getCategoryDown(getP("p3")));
    }

    @Test
    public void testCestlbordel() throws Exception {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	final NavigableSet<Category> catsView = cats.getCategories();
	assertEquals(0, catsView.size());
	assertEquals(0, cats.getProfiles().size());

	cats.addCategory(getC1WithP1Up());
	cats.addProfile(getP2());
	cats.setCategoryUp(getP2(), getC2());
	cats.addProfile(getP("p3"));
	/** Now we have: c1, p1, p2, c2, p3. */

	assertEquals(2, catsView.size());
	Iterator<Category> catsRd = catsView.iterator();
	assertIdentical(getC1WithP1Up(), catsRd.next());
	final Category c2Full = new Category("c2", getP("p2"), getP("p3"));
	assertIdentical(c2Full, catsRd.next());

	assertEquals(3, cats.getProfiles().size());
	Iterator<Alternative> profiles = cats.getProfiles().iterator();
	assertEquals(getP1(), profiles.next());
	assertEquals(getP2(), profiles.next());
	assertEquals(getP("p3"), profiles.next());

	assertIdentical(c2Full, cats.getCategoryUp(getP2()));
	assertEquals(null, cats.getCategoryDown(getP2()));
	assertEquals(getP1(), cats.getProfileUp("c1"));

	final Category c1BisInput = new Category("c1Bis", null, getP("p4"));
	cats.setCategoryDown(getP2(), c1BisInput);
	/** Now we have: c1, p1, c1bis, p4, c2, p3. */

	assertFalse(cats.isComplete());

	assertEquals(3, catsView.size());
	catsRd = catsView.iterator();
	assertIdentical(getC1WithP1Up(), catsRd.next());
	final Category c1BisOutput = new Category("c1Bis", getP1(), getP("p4"));
	assertIdentical(c1BisOutput, catsRd.next());
	final Category c2Changed = new Category("c2", getP("p4"), getP("p3"));
	assertIdentical(c2Changed, catsRd.next());

	assertEquals(3, cats.getProfiles().size());
	profiles = cats.getProfiles().iterator();
	assertEquals(getP1(), profiles.next());
	assertEquals(getP("p4"), profiles.next());
	assertEquals(getP("p3"), profiles.next());

	assertEquals(null, cats.getCategoryUp(getP2()));
	assertIdentical(c1BisOutput, cats.getCategoryDown(getP("p4")));
	assertIdentical(c2Changed, cats.getCategoryUp(getP("p4")));
	assertEquals(getP1(), cats.getProfileUp("c1"));
    }

    private Category getC2() {
	return getC("c2");
    }

    private Category getC(final String name) {
	return new Category(name, null, null);
    }

    @Test
    public void nothingAddOneProf() throws Exception {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	assertEquals(0, cats.getCategories().size());
	assertEquals(0, cats.getProfiles().size());
	assertEquals(null, cats.getCategoryDown(getP1()));
	assertEquals(null, cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getProfileDown("c1"));
	assertEquals(null, cats.getProfileUp("c1"));

	cats.addProfile(getP1());
	assertEquals(0, cats.getCategories().size());
	assertEquals(1, cats.getProfiles().size());
	assertEquals(getP1(), cats.getProfiles().first());
	assertEquals(null, cats.getCategoryDown(getP1()));
	assertEquals(null, cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getProfileDown("c1"));
	assertEquals(null, cats.getProfileUp("c1"));
    }

    @Test
    public void oneCatAddNewCat() throws Exception {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	cats.addCategory(getC1());
	assertEquals(1, cats.getCategories().size());
	assertEquals(getC1(), cats.getCategories().first());
	assertEquals(0, cats.getProfiles().size());

	cats.addCategory("c2");
	assertEquals(2, cats.getCategories().size());
	assertIdentical(getC1(), cats.getCategories().first());
	assertIdentical(getC2(), cats.getCategories().higher(getC1()));
	assertEquals(0, cats.getProfiles().size());

	assertEquals(null, cats.getCategoryDown(getP1()));
	assertEquals(null, cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getProfileDown("c1"));
	assertEquals(null, cats.getProfileUp("c1"));
    }

    public void assertIdentical(Category expected, Category actual) {
	final boolean eNull = expected == null;
	final boolean aNull = actual == null;
	if ((aNull != eNull) || (expected != null && !expected.identicalTo(actual))) {
	    throw new IllegalStateException("Expected " + expected + ", got " + actual + ".");
	}
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplCatName() throws Exception {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	cats.addCategory(getC1());
	cats.addCategory("c1");
    }

    private Alternative getP2() {
	return new Alternative("p2");
    }

    private Category getC2P1P2() {
	return new Category("c2", getP1(), getP2());
    }

    @Test
    public void oneCatAddOneProfBefore() throws Exception {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	cats.addCategory(getC1());
	assertEquals(1, cats.getCategories().size());
	assertIdentical(getC1(), cats.getCategories().first());
	assertEquals(0, cats.getProfiles().size());

	// cats.setProfileDown("c1", getP1());
	// assertEquals(1, cats.getCategories().size());
	// assertEquals(getC1WithP1Down(), cats.getCategories().first());
	// assertEquals(1, cats.getProfiles().size());
	// assertEquals(getP1(), cats.getProfiles().last());
	// assertEquals(null, cats.getCategoryDown(getP1()));
	// assertEquals(getC1WithP1Down(), cats.getCategoryUp(getP1()));
	// assertEquals(getP1(), cats.getProfileDown("c1"));
	// assertEquals(null, cats.getProfileUp("c1"));
    }

    private Category getC1() {
	return getC("c1");
    }

    private Category getC2WithP1Down() {
	return new Category("c2", getP1(), null);
    }

    @Test
    public void oneCatAddOneProf() throws Exception {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	cats.addCategory(getC1());
	assertEquals(1, cats.getCategories().size());
	assertEquals(getC1(), cats.getCategories().first());
	assertEquals(0, cats.getProfiles().size());

	cats.addProfile(getP1());
	assertEquals(1, cats.getCategories().size());
	assertIdentical(getC1WithP1Up(), cats.getCategories().first());
	assertEquals(1, cats.getProfiles().size());
	assertEquals(getP1(), cats.getProfiles().last());
	assertIdentical(getC1WithP1Up(), cats.getCategoryDown(getP1()));
	assertEquals(null, cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getProfileDown("c1"));
	assertEquals(getP1(), cats.getProfileUp("c1"));
    }

    @Test
    public void oneProfAddOneCat() throws Exception {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	cats.addProfile(getP1());
	assertEquals(0, cats.getCategories().size());
	assertEquals(1, cats.getProfiles().size());
	assertEquals(getP1(), cats.getProfiles().first());

	cats.addCategory(getC1());
	assertEquals(1, cats.getCategories().size());
	assertIdentical(getC1WithP1Up(), cats.getCategories().first());
	assertEquals(1, cats.getProfiles().size());
	assertEquals(getP1(), cats.getProfiles().last());
	assertIdentical(getC1WithP1Up(), cats.getCategoryDown(getP1()));
	assertEquals(null, cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getProfileDown("c1"));
	assertEquals(getP1(), cats.getProfileUp("c1"));
    }

    @Test
    public void oneProfAddOneCatBefore() throws Exception {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	cats.addProfile(getP1());
	assertEquals(0, cats.getCategories().size());
	assertEquals(1, cats.getProfiles().size());
	assertEquals(getP1(), cats.getProfiles().first());

	cats.setCategoryDown(getP1(), getC1());
	assertEquals(1, cats.getCategories().size());
	assertIdentical(getC1WithP1Up(), cats.getCategories().first());
	assertEquals(1, cats.getProfiles().size());
	assertEquals(getP1(), cats.getProfiles().last());
	assertIdentical(getC1WithP1Up(), cats.getCategoryDown(getP1()));
	assertEquals(null, cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getProfileDown("c1"));
	assertEquals(getP1(), cats.getProfileUp("c1"));
    }

    @Test
    public void oneProfAddOneCatAfter() throws Exception {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	cats.addProfile(getP1());
	assertEquals(0, cats.getCategories().size());
	assertEquals(1, cats.getProfiles().size());
	assertEquals(getP1(), cats.getProfiles().first());

	cats.setCategoryUp(getP1(), getC1());
	assertEquals(1, cats.getCategories().size());
	assertIdentical(getC1WithP1Down(), cats.getCategories().first());
	assertEquals(1, cats.getProfiles().size());
	assertEquals(getP1(), cats.getProfiles().last());
	assertIdentical(getC1WithP1Down(), cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getCategoryDown(getP1()));
	assertEquals(null, cats.getProfileUp("c1"));
	assertEquals(getP1(), cats.getProfileDown("c1"));
    }

    @Test
    public void setCompleteSimple() throws Exception {
	final CatsAndProfs categories = Categories.newCatsAndProfs();
	final Alternative pBM = new Alternative("pBM");
	final Alternative pMG = new Alternative("pMG");
	categories.addCategory("bad");
	categories.addCategory("medium");
	categories.addCategory("good");
	categories.setProfileUp("bad", pBM);
	categories.setProfileUp("medium", pMG);
	assertTrue(categories.isComplete());
    }

    @Test
    public void nothingSetProfiles() throws Exception {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	cats.addCategory(getC1());
	cats.addCategory(getC2());

	assertEquals(2, cats.getCategories().size());
	assertIdentical(getC1(), cats.getCategories().first());
	assertEquals(0, cats.getProfiles().size());

	assertEquals(null, cats.getCategoryDown(getP1()));
	assertEquals(null, cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getProfileDown("c1"));
	assertEquals(null, cats.getProfileUp("c1"));

	final boolean setUp = cats.setProfileUp("c1", getP1());
	assertTrue(setUp);
	assertEquals(getP1(), cats.getProfileDown("c2"));
	final boolean setDown = cats.setProfileDown("c2", getP1());
	assertFalse(setDown);

	assertEquals(2, cats.getCategories().size());
	assertIdentical(getC1WithP1Up(), cats.getCategories().first());
	assertEquals(1, cats.getProfiles().size());

	assertIdentical(getC1WithP1Up(), cats.getCategoryDown(getP1()));
	assertIdentical(getC2WithP1Down(), cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getProfileDown("c1"));
	assertEquals(getP1(), cats.getProfileUp("c1"));
	assertEquals(getP1(), cats.getProfileDown("c2"));
	assertEquals(null, cats.getProfileUp("c2"));

	final boolean setUp2 = cats.setProfileUp("c2", getP2());
	assertTrue(setUp2);

	assertEquals(2, cats.getProfiles().size());

	assertIdentical(getC1WithP1Up(), cats.getCategoryDown(getP1()));
	assertIdentical(getC2P1P2(), cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getProfileDown("c1"));
	assertEquals(getP1(), cats.getProfileUp("c1"));
	assertEquals(getP1(), cats.getProfileDown("c2"));
	assertEquals(getP2(), cats.getProfileUp("c2"));

	cats.setCategoryUp(getP2(), getC("c3"));

	assertEquals(3, cats.getCategories().size());
	assertIdentical(getC1WithP1Up(), cats.getCategories().first());
	assertEquals(2, cats.getProfiles().size());

	assertIdentical(getC1WithP1Up(), cats.getCategoryDown(getP1()));
	assertIdentical(getC2P1P2(), cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getProfileDown("c1"));
	assertEquals(getP1(), cats.getProfileUp("c1"));
	assertEquals(getP1(), cats.getProfileDown("c2"));
	assertEquals(getP2(), cats.getProfileUp("c2"));
	assertEquals(getP2(), cats.getProfileDown("c3"));
	assertEquals(null, cats.getProfileUp("c3"));

	assertTrue(cats.isComplete());
    }

    private Category getC1WithP2Up() {
	return new Category("c1", null, getP2());
    }

    private Alternative getP(String name) {
	return new Alternative(name);
    }

    private Alternative getP1() {
	return new Alternative("p1");
    }

    private Category getC1Full() {
	return new Category("c1", getP1(), getP2());
    }

    @Test
    public void nothingAddOneCat() throws Exception {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	assertEquals(0, cats.getCategories().size());
	assertEquals(0, cats.getProfiles().size());
	assertEquals(null, cats.getCategoryDown(getP1()));
	assertEquals(null, cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getProfileDown("c1"));
	assertEquals(null, cats.getProfileUp("c1"));

	cats.addCategory(getC1());

	assertEquals(1, cats.getCategories().size());
	assertIdentical(getC1(), cats.getCategories().first());
	assertEquals(0, cats.getProfiles().size());

	assertEquals(null, cats.getCategoryDown(getP1()));
	assertEquals(null, cats.getCategoryUp(getP1()));
	assertEquals(null, cats.getProfileDown("c1"));
	assertEquals(null, cats.getProfileUp("c1"));
    }

    private Category getC1WithP1Up() {
	return new Category("c1", null, getP1());
    }

    private Category getC1WithP1Down() {
	return new Category("c1", getP1(), null);
    }

    private Category getC2Full() {
	return new Category("c2", getP2(), getP("p3"));
    }
}
