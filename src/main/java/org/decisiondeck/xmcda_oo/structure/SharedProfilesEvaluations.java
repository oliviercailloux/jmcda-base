package org.decisiondeck.xmcda_oo.structure;

import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.internal.AbstractSharedDecisionMakerPreferences;
import org.decision_deck.jmcda.structure.internal.Modifier;
import org.decision_deck.jmcda.structure.matrix.Evaluations;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.matrix.EvaluationsUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class SharedProfilesEvaluations extends AbstractSharedDecisionMakerPreferences<Evaluations> {
    public boolean remove(Alternative profile) {
	final Alternative toRemove = profile;
	return m_director.applyToAll(new Modifier<Evaluations>() {
	    @Override
	    public boolean modify(Evaluations evaluations) {
		if (!evaluations.getRows().contains(toRemove)) {
		    return false;
		}
		for (Criterion criterion : ImmutableSet.copyOf(evaluations.getColumns())) {
		    evaluations.remove(toRemove, criterion);
		}
		return true;
	    }
	}, false);
    }

    public boolean remove(Criterion criterion) {
	final Criterion toRemove = criterion;
	return m_director.applyToAll(new Modifier<Evaluations>() {
	    @Override
	    public boolean modify(Evaluations evaluations) {
		if (!evaluations.getColumns().contains(toRemove)) {
		    return false;
		}
		for (Alternative alternative : ImmutableSet.copyOf(evaluations.getRows())) {
		    evaluations.remove(alternative, toRemove);
		}
		return true;
	    }
	}, false);
    }

    public boolean removeEvaluation(DecisionMaker dm, Alternative profile, Criterion criterion) {
	final Alternative alternativeParam = profile;
	final Criterion criterionParam = criterion;
	final boolean changeOccurred = m_director.apply(dm, new Modifier<Evaluations>() {
	    @Override
	    public boolean modify(Evaluations target) {
		final Double previous = target.remove(alternativeParam, criterionParam);
		return previous != null;
	    }
	}, false);
	return changeOccurred;
    }

    public boolean putEvaluation(DecisionMaker dm, Alternative profile, Criterion criterion, double value) {
	final Alternative alternativeParam = profile;
	final Criterion criterionParam = criterion;
	final double valueParam = value;
	final boolean changeOccurred = m_director.apply(dm, new Modifier<Evaluations>() {
	    @Override
	    public boolean modify(Evaluations target) {
		final Double previous = target.getEntry(alternativeParam, criterionParam);
		target.put(alternativeParam, criterionParam, valueParam);
		return !Double.valueOf(valueParam).equals(previous);
	    }
	}, true);
	return changeOccurred;
    }

    public boolean mergeExtended(DecisionMaker dm, EvaluationsRead toMerge) {
	final EvaluationsRead evaluationsParam = toMerge;
	final boolean changeOccurred = m_director.apply(dm, new Modifier<Evaluations>() {
	    @Override
	    public boolean modify(Evaluations target) {
		return merge(evaluationsParam, target);
	    }
	}, true);
	return changeOccurred;
    }

    public boolean replaceShared(EvaluationsRead newShared) {
	final EvaluationsRead profilesEvaluationsParam = newShared;
	final boolean changed = m_director.applyToAll(new Modifier<Evaluations>() {
	    @Override
	    public boolean modify(Evaluations target) {
		return copyContentsExtended(profilesEvaluationsParam, target);
	    }
	}, true);
	return changed;
    }

    private boolean copyContentsExtended(EvaluationsRead source, Evaluations target) {
	boolean changed = false;

	final Set<Alternative> fromAlts = source.getRows();
	final Set<Alternative> toAlts = target.getRows();
	final Set<Criterion> fromCrits = source.getColumns();
	final Set<Criterion> toCrits = target.getColumns();

	for (Alternative alternative : Sets.union(fromAlts, toAlts).immutableCopy()) {
	    for (Criterion criterion : Sets.union(fromCrits, toCrits).immutableCopy()) {
		final Double fromEntry = source.getEntry(alternative, criterion);
		final Double toEntry = target.getEntry(alternative, criterion);
		if (fromEntry != null) {
		    target.put(alternative, criterion, fromEntry.doubleValue());
		    changed = changed || !fromEntry.equals(toEntry);
		} else if (toEntry != null) {
		    target.remove(alternative, criterion);
		    changed = true;
		}
	    }
	}

	return changed;
    }

    private boolean merge(EvaluationsRead source, Evaluations target) {
	boolean changed = false;
	for (Alternative alternative : source.getRows()) {
	    for (Criterion criterion : source.getColumns()) {
		final Double entry = source.getEntry(alternative, criterion);
		if (entry == null) {
		    continue;
		}
		final double value = entry.doubleValue();
		final Double valueHere = target.getEntry(alternative, criterion);
		changed = changed || valueHere == null || valueHere.doubleValue() != value;
		target.put(alternative, criterion, value);
	    }
	}
	return changed;
    }

    @Override
    protected boolean copyContents(Evaluations source, Evaluations target) {
	return copyContentsExtended(source, target);
    }

    @Override
    protected Evaluations copyFrom(Evaluations input) {
	return EvaluationsUtils.newEvaluationMatrix(input);
    }

    @Override
    protected Evaluations getNew() {
	return EvaluationsUtils.newEvaluationMatrix();
    }

    @Override
    protected boolean isEmpty(Evaluations evaluations) {
	return evaluations.isEmpty();
    }

    @Override
    protected void empty(Evaluations value) {
	for (Alternative alternative : ImmutableSet.copyOf(value.getRows())) {
	    for (Criterion criterion : ImmutableSet.copyOf(value.getColumns())) {
		value.remove(alternative, criterion);
	    }
	}
    }
}
