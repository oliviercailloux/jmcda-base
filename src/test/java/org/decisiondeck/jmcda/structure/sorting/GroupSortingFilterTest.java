package org.decisiondeck.jmcda.structure.sorting;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.matrix.Evaluations;
import org.decision_deck.jmcda.structure.matrix.EvaluationsUtils;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.IGroupSortingPreferences;
import org.junit.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

public class GroupSortingFilterTest {
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
	alternativesEvaluations.put(getA3(), getG1(), 1d);
	alternativesEvaluations.put(getA2(), getG1(), 1d);
	data.setEvaluations(alternativesEvaluations);
	final IGroupSortingPreferences filtered = new GroupSortingPreferencesFiltering(data, Predicates.in(getTwoAlts()));
	assertEquals(2, filtered.getAlternatives().size());
	assertEquals(5, filtered.getAllAlternatives().size());
	assertEquals(getThreeProfiles(), data.getSharedProfilesEvaluations().getRows());
	assertEquals(3, filtered.getSharedProfilesEvaluations().getRows().size());
    }

    private Criterion getG1() {
	return new Criterion("g1");
    }

    private Set<Alternative> getTwoAlts() {
	final Set<Alternative> alts = Sets.newLinkedHashSet();
	alts.add(getA1());
	alts.add(getA2());
	return alts;
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

    private Alternative getA1() {
        return new Alternative("a1");
    }

    private Alternative getA2() {
        return new Alternative("a2");
    }

    private Alternative getA3() {
        return new Alternative("a3");
    }

    private Set<Alternative> getThreeProfiles() {
	final Set<Alternative> profiles = Sets.newLinkedHashSet();
	profiles.add(getP1());
	profiles.add(getP2());
	profiles.add(getP3());
	return profiles;
    }
}
