package org.decisiondeck.jmcda.structure.sorting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.matrix.Evaluations;
import org.decision_deck.jmcda.structure.matrix.EvaluationsUtils;
import org.decision_deck.jmcda.structure.sorting.category.Categories;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decisiondeck.jmcda.structure.sorting.problem.ProblemFactory;
import org.decisiondeck.jmcda.structure.sorting.problem.data.ISortingData;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.IGroupSortingPreferences;
import org.decisiondeck.xmcda_oo.structure.sorting.SortingProblemUtils;
import org.junit.Test;

public class GroupSortingTest {
    @Test
    public void testStart() throws Exception {
	ISortingData res = ProblemFactory.newSortingData();
	Alternative a1 = new Alternative("a1");
	res.getAlternatives().clear();
	res.getAlternatives().addAll(Collections.singleton(a1));
	assertEquals(1, res.getAllAlternatives().size());
	res.getAlternatives().clear();
	res.getAlternatives().addAll(Collections.singleton(new Alternative("a2")));
	assertEquals(1, res.getAllAlternatives().size());
	res.getAlternatives().retainAll(res.getAllAlternatives());
	assertEquals(1, res.getAllAlternatives().size());
	Criterion g1 = new Criterion("g1");
	res.setEvaluation(a1, g1, Double.valueOf(10d));
	assertEquals(2, res.getAllAlternatives().size());
	assertEquals(1, res.getCriteria().size());
    }

    @Test
    public void testRetain() throws Exception {
	ISortingData res = ProblemFactory.newSortingData();
	Alternative a1 = new Alternative("a1");
	final Alternative a2 = new Alternative("a2");
	final Alternative a3 = new Alternative("a3");
	res.getAlternatives().add(a1);
	res.getAlternatives().add(a2);
	res.getAlternatives().add(a3);
	assertEquals(3, res.getAllAlternatives().size());
	assertEquals(3, res.getAlternatives().size());
	res.getAlternatives().retainAll(Collections.singleton(a2));
	assertEquals(1, res.getAllAlternatives().size());
	assertEquals(1, res.getAlternatives().size());
    }

    @Test
    public void testProfiles() throws Exception {
	ISortingData data = ProblemFactory.newSortingData();
	final Alternative p1 = new Alternative("p1");
	final Alternative p2 = new Alternative("p2");
	final Alternative p3 = new Alternative("p3");

	final Set<Alternative> profiles = data.getProfiles();
	/** p1, p2 */
	assertTrue(profiles.add(p1));
	assertTrue(profiles.add(p2));
	assertTrue(profiles.remove(p1));
	assertFalse(profiles.add(p2));
	assertTrue(profiles.add(p1));
	/** p2, p1 */

	final Iterator<Alternative> iteratorFullProfiles = profiles.iterator();
	assertTrue(iteratorFullProfiles.hasNext());
	assertEquals(p2, iteratorFullProfiles.next());
	assertTrue(iteratorFullProfiles.hasNext());
	assertEquals(p1, iteratorFullProfiles.next());
	assertFalse(iteratorFullProfiles.hasNext());

	assertTrue(profiles.remove(p1));
	/** p2 */

	final Iterator<Alternative> iteratorHalfProfiles = data.getProfiles().iterator();
	assertTrue(iteratorHalfProfiles.hasNext());
	assertEquals(p2, iteratorHalfProfiles.next());
	assertFalse(iteratorHalfProfiles.hasNext());

	assertTrue(profiles.remove(p2));
	assertTrue(profiles.isEmpty());
	assertFalse(profiles.iterator().hasNext());

	data.getCatsAndProfs().clear();
	data.getCatsAndProfs().addAll(getThreeProfilesCategories());

	/** p1, p2, p3 */
	final Iterator<Alternative> iteratorThreeProfiles = profiles.iterator();
	assertTrue(iteratorThreeProfiles.hasNext());
	assertEquals(p1, iteratorThreeProfiles.next());
	assertTrue(iteratorThreeProfiles.hasNext());
	assertEquals(p2, iteratorThreeProfiles.next());
	iteratorThreeProfiles.remove();
	assertEquals(p3, iteratorThreeProfiles.next());
	assertFalse(iteratorThreeProfiles.hasNext());
	/** p1, p3 */

	assertEquals(2, data.getProfiles().size());
	assertEquals(2, data.getCatsAndProfs().getProfiles().size());
    }

    @Test
    public void testSharedProfilesEvaluations() throws Exception {
	IGroupSortingPreferences res = SortingProblemUtils.newGroupPreferences();
	Alternative p1 = new Alternative("p1");
	Alternative p2 = new Alternative("p2");
	Criterion g1 = new Criterion("g1");
	Criterion g2 = new Criterion("g2");
	final DecisionMaker dm1 = new DecisionMaker("dm1");
	final DecisionMaker dm2 = new DecisionMaker("dm2");
	final DecisionMaker dm3 = new DecisionMaker("dm3");
	final DecisionMaker dm4 = new DecisionMaker("dm4");
	boolean changed = res.setProfilesEvaluation(dm1, p1, g1, Double.valueOf(1d));
	assertTrue(changed);
	assertEquals(1, res.getSharedProfilesEvaluations().getValueCount());
	changed = res.setProfilesEvaluation(dm1, p1, g2, Double.valueOf(2d));
	assertTrue(changed);
	changed = res.setProfilesEvaluation(dm1, p2, g1, Double.valueOf(3d));
	assertTrue(changed);
	changed = res.setProfilesEvaluation(dm1, p2, g1, Double.valueOf(3d));
	assertFalse(changed);
	assertEquals(3, res.getSharedProfilesEvaluations().getValueCount());
	changed = res.setProfilesEvaluation(dm1, p2, g2, Double.valueOf(4d));
	assertTrue(changed);
	assertEquals(4, res.getSharedProfilesEvaluations().getValueCount());
	changed = res.setProfilesEvaluation(dm2, p1, g1, Double.valueOf(1d));
	assertTrue(changed);
	assertEquals(0, res.getSharedProfilesEvaluations().getValueCount());
	changed = res.setProfilesEvaluation(dm2, p1, g2, Double.valueOf(2d));
	assertTrue(changed);
	changed = res.setProfilesEvaluation(dm2, p2, g1, Double.valueOf(3d));
	assertTrue(changed);
	changed = res.setProfilesEvaluation(dm2, p2, g2, Double.valueOf(4d));
	assertTrue(changed);

	assertEquals(4, res.getProfilesEvaluations(dm1).getValueCount());
	assertEquals(4, res.getProfilesEvaluations(dm2).getValueCount());
	assertEquals(4, res.getSharedProfilesEvaluations().getValueCount());

	final Evaluations dm3Evals = EvaluationsUtils.newEvaluationMatrix();
	dm3Evals.put(p1, g1, 1d);
	dm3Evals.put(p1, g2, 2d);
	dm3Evals.put(p2, g1, 3d);
	dm3Evals.put(p2, g2, 4d);
	changed = res.setProfilesEvaluations(dm3, dm3Evals);
	assertTrue(changed);

	assertEquals(4, res.getProfilesEvaluations(dm1).getValueCount());
	assertEquals(4, res.getProfilesEvaluations(dm2).getValueCount());
	assertEquals(4, res.getProfilesEvaluations(dm3).getValueCount());
	assertEquals(4, res.getSharedProfilesEvaluations().getValueCount());

	final Evaluations dm4Evals = EvaluationsUtils.newEvaluationMatrix();
	dm4Evals.put(p1, g1, 1d);
	dm4Evals.put(p1, g2, 2d);
	dm4Evals.put(p2, g1, 3d);
	dm4Evals.put(p2, g2, 100d);
	changed = res.setProfilesEvaluations(dm4, dm4Evals);
	assertTrue(changed);

	assertEquals(4, res.getProfilesEvaluations(dm1).getValueCount());
	assertEquals(4, res.getProfilesEvaluations(dm2).getValueCount());
	assertEquals(4, res.getProfilesEvaluations(dm3).getValueCount());
	assertEquals(4, res.getProfilesEvaluations(dm4).getValueCount());
	assertEquals(0, res.getSharedProfilesEvaluations().getValueCount());
    }

    @Test
    public void testCategories() throws Exception {
	ISortingData res = ProblemFactory.newSortingData();

	setThreeProfiles(res.getCatsAndProfs());
	assertEquals(3, res.getProfiles().size());
    }

    @Test
    public void testGroupPreferences() throws Exception {
	final IGroupSortingPreferences data = SortingProblemUtils.newGroupPreferences();
	final DecisionMaker dm1 = new DecisionMaker("dm1");
	final Alternative a1 = new Alternative("a1");
	final Criterion g1 = new Criterion("g1");
	final Criterion g2 = new Criterion("g2");
	data.setProfilesEvaluation(dm1, a1, g1, Double.valueOf(1d));
	data.setProfilesEvaluation(dm1, a1, g2, Double.valueOf(2d));
	assertEquals(2, data.getProfilesEvaluations(dm1).getValueCount());
	data.getCriteria().remove(g2);
	assertEquals(1, data.getProfilesEvaluations(dm1).getValueCount());
    }

    private CatsAndProfs getThreeProfilesCategories() {
	final CatsAndProfs cats = Categories.newCatsAndProfs();
	setThreeProfiles(cats);
	return cats;
    }

    private void setThreeProfiles(CatsAndProfs cats) {
	cats.addProfile(new Alternative("p1"));
	cats.addProfile(new Alternative("p2"));
	cats.addProfile(new Alternative("p3"));
    }

    @Test
    public void testCopyProfsAndCats() throws Exception {
	ISortingData res = ProblemFactory.newSortingData();
	final int nbCategories = 3;
	final CatsAndProfs cats = Categories.newCatsAndProfs();

	final NumberFormat fmt = NumberFormat.getIntegerInstance(Locale.ENGLISH);
	fmt.setMinimumIntegerDigits((int) Math.floor(Math.log10(nbCategories)) + 1);
	for (int i = 1; i <= nbCategories; ++i) {
	    final String nb = fmt.format(i);
	    cats.addCategory("Cat" + nb);
	}
	res.getCatsAndProfs().clear();
	res.getCatsAndProfs().addAll(cats);
	res.getCatsAndProfs().addProfile(new Alternative("p1"));
	res.getCatsAndProfs().addProfile(new Alternative("p2"));

	final Iterator<Category> catsIter = res.getCatsAndProfs().getCategories().iterator();
	final Category c1 = catsIter.next();
	assertTrue("Exp: " + getExpC1() + ", got: " + c1, getExpC1().identicalTo(c1));
	final Category c2 = catsIter.next();
	assertTrue("Exp: " + getExpC2() + ", got: " + c2, getExpC2().identicalTo(c2));
	final Category c3 = catsIter.next();
	assertTrue("Exp: " + getExpC3() + ", got: " + c3, getExpC3().identicalTo(c3));
    }

    private Category getExpC1() {
	return new Category("Cat1", null, new Alternative("p1"));
    }

    private Category getExpC2() {
	return new Category("Cat2", new Alternative("p1"), new Alternative("p2"));
    }

    private Category getExpC3() {
	return new Category("Cat3", new Alternative("p2"), null);
    }

    @Test
    public void testPreferences() throws Exception {
	final GroupSortingPreferencesImpl data = new GroupSortingPreferencesImpl();
	data.getAlternatives().add(getA1());
	data.getAlternatives().add(getA3());
	data.getAlternatives().add(getA2());
	data.getProfiles().add(getP1());
	data.getProfiles().add(getP2());
	data.getProfiles().add(getP3());
	final Evaluations profilesEvaluations = EvaluationsUtils.newEvaluationMatrix();
	profilesEvaluations.put(getP1(), getG1(), 1d);
	profilesEvaluations.put(getP2(), getG1(), 1d);
	profilesEvaluations.put(getP3(), getG1(), 1d);
	data.setSharedProfilesEvaluations(profilesEvaluations);
	final Evaluations alternativesEvaluations = EvaluationsUtils.newEvaluationMatrix();
	alternativesEvaluations.put(getA1(), getG1(), 1d);
	data.setEvaluations(alternativesEvaluations);
	assertEquals(3, data.getAlternatives().size());
	assertEquals(6, data.getAllAlternatives().size());
	assertEquals(3, data.getSharedProfilesEvaluations().getRows().size());
    }

    private Alternative getA1() {
	return new Alternative("a1");
    }

    private Alternative getA2() {
	return new Alternative("a2");
    }

    private Alternative getA3() {
	return new Alternative("a3");
    }

    private Criterion getG1() {
	return new Criterion("g1");
    }

    private Alternative getP1() {
	return new Alternative("p1");
    }

    private Alternative getP2() {
	return new Alternative("p2");
    }

    private Alternative getP3() {
	return new Alternative("p3");
    }

}
