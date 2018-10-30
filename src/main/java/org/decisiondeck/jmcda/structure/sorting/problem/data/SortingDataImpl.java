package org.decisiondeck.jmcda.structure.sorting.problem.data;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.interval.Interval;
import org.decision_deck.jmcda.structure.interval.Intervals;
import org.decision_deck.jmcda.structure.matrix.Evaluations;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.matrix.EvaluationsUtils;
import org.decision_deck.jmcda.structure.matrix.EvaluationsView;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decision_deck.jmcda.structure.sorting.category.mess.CatsAndProfsWithObserverPriorities;
import org.decision_deck.utils.IObserver;
import org.decision_deck.utils.collection.AbstractSetView;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.collect.Sets;

public class SortingDataImpl implements ISortingData {

    public SortingDataImpl() {
	m_allAlternativesView = Sets.union(m_alternatives, m_profiles);
	/**
	 * As this object observe these categories to maintain its consistency, we must have priority notifications over
	 * external observers, and expose only a possibility to observe these categories with a lower priority, to make
	 * sure that this object is consistent at the time external observers are notified.
	 */
	m_categories.addPriorityObserverAddedProfile(new IObserver<Alternative>() {
	    @Override
	    public void update(Alternative updated) {
		SortingDataImpl.this.getProfiles().add(updated);
	    }
	});
    }

    public static class ProvideEvaluationsView implements Function<EvaluationsRead, EvaluationsRead> {
	@Override
	public EvaluationsRead apply(EvaluationsRead from) {
	    return new EvaluationsView(from);
	}
    }

    private final Set<Alternative> m_alternatives = Sets.newLinkedHashSet();
    private final Evaluations m_alternativesEvaluations = EvaluationsUtils.newEvaluationMatrix();
    private final CatsAndProfsWithObserverPriorities m_categories = new CatsAndProfsWithObserverPriorities();
    private final Set<Criterion> m_criteria = Sets.newLinkedHashSet();
    private final Set<Alternative> m_profiles = Sets.newLinkedHashSet();
    private final ProvideEvaluationsView m_provideEvaluationsView = new ProvideEvaluationsView();
    private final Map<Criterion, Interval> m_scales = Maps.newLinkedHashMap();
    private final Set<Alternative> m_allAlternativesView;

    @Override
    public EvaluationsRead getAlternativesEvaluations() {
	return m_provideEvaluationsView.apply(m_alternativesEvaluations);
    }

    @Override
    public CatsAndProfs getCatsAndProfs() {
	return m_categories;
    }

    public Interval getScale(Criterion criterion) {
	if (!m_criteria.contains(criterion)) {
	    return null;
	}
	final Interval scale = m_scales.get(criterion);
	if (scale != null) {
	    return scale;
	}
	throw new IllegalStateException("Should have a scale.");
	// m_scales.put(criterion, Intervals.newRealsInterval());
	// return m_scales.get(criterion);
    }

    @Override
    public Map<Criterion, Interval> getScales() {
	final Map<Criterion, Interval> transformed = Maps.transformEntries(m_scales,
		new EntryTransformer<Criterion, Interval, Interval>() {
		    @Override
		    public Interval transformEntry(Criterion key, Interval value) {
			if (value != null) {
			    return value;
			}
			throw new IllegalStateException("Should have a scale.");
			// return Intervals.newRealsInterval();
		    }
		});
	return Collections.unmodifiableMap(transformed);
    }

    private void beforeRemoveAlternative(Alternative alternative) {
	for (Criterion criterion : m_criteria) {
	    m_alternativesEvaluations.remove(alternative, criterion);
	}
    }

    private void beforeRemoveCriterion(Criterion criterion) {
	for (Alternative alternative : m_alternatives) {
	    m_alternativesEvaluations.remove(alternative, criterion);
	}

	m_scales.remove(criterion);
    }

    private void beforeRemoveProfile(Alternative profile) {
	if (m_categories.getProfiles().contains(profile)) {
	    m_categories.removeProfile(profile);
	}
    }

    @Override
    public boolean setEvaluation(Alternative alternative, Criterion criterion, Double value) {
	if (alternative == null || criterion == null) {
	    throw new NullPointerException("" + alternative + criterion + value);
	}
	if (value == null) {
	    return m_alternativesEvaluations.remove(alternative, criterion) != null;
	}
	final Double current = m_alternativesEvaluations.getEntry(alternative, criterion);
	if (value.equals(current)) {
	    return false;
	}
	m_alternatives.add(alternative);
	getCriteria().add(criterion);
	m_alternativesEvaluations.put(alternative, criterion, value.doubleValue());
	return true;
    }

    @Override
    public boolean setEvaluations(EvaluationsRead evaluations) {
	boolean changed = false;
	for (Alternative alternative : evaluations.getRows()) {
	    m_alternatives.add(alternative);
	    for (Criterion criterion : evaluations.getColumns()) {
		getCriteria().add(criterion);
		final Double value = evaluations.getEntry(alternative, criterion);
		if (value != null) {
		    final Double current = m_alternativesEvaluations.getEntry(alternative, criterion);
		    if (!value.equals(current)) {
			m_alternativesEvaluations.put(alternative, criterion, value.doubleValue());
			changed = true;
		    }
		}
	    }
	}
	return changed;
    }

    @Override
    public boolean setScale(Criterion criterion, Interval scale) {
	if (criterion == null) {
	    throw new NullPointerException("" + criterion + scale);
	}
	if (scale == null) {
	    return m_scales.remove(criterion) != null;
	}
	getCriteria().add(criterion);
	final Interval previousScale = m_scales.put(criterion, scale);
	return !scale.equals(previousScale);
    }

    @Override
    public Set<Alternative> getAllAlternatives() {
	return m_allAlternativesView;
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return new AbstractSetView<Alternative>(m_alternatives) {
	    @Override
	    public boolean add(Alternative e) {
		Preconditions.checkArgument(!getProfiles().contains(e));
		return super.add(e);
	    }

	    @Override
	    public void beforeRemove(Object object) {
		if (object instanceof Alternative) {
		    Alternative alternative = (Alternative) object;
		    beforeRemoveAlternative(alternative);
		}
	    }
	};
    }

    @Override
    public Set<Criterion> getCriteria() {
	return new AbstractSetView<Criterion>(m_criteria) {
	    @Override
	    protected void beforeRemove(Object o) {
		if (o instanceof Criterion) {
		    Criterion criterion = (Criterion) o;
		    beforeRemoveCriterion(criterion);
		}
	    }

	    @Override
	    public boolean add(Criterion criterion) {
		final boolean added = super.add(criterion);
		if (added) {
		    m_scales.put(criterion, Intervals.newRealsInterval());
		}
		return added;
	    }
	};
    }

    @Override
    public Set<Alternative> getProfiles() {
	return new AbstractSetView<Alternative>(m_profiles) {
	    @Override
	    public boolean add(Alternative e) {
		Preconditions.checkArgument(!getAlternatives().contains(e), "The profile to be added: " + e
			+ " is already known as an alternative.");
		return super.add(e);
	    }

	    @Override
	    public void beforeRemove(Object object) {
		if (object instanceof Alternative) {
		    Alternative profile = (Alternative) object;
		    beforeRemoveProfile(profile);
		}
	    }
	};
    }

}
