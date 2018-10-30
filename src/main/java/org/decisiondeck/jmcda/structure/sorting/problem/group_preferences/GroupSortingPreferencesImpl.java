package org.decisiondeck.jmcda.structure.sorting.problem.group_preferences;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.matrix.Evaluations;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.thresholds.Thresholds;
import org.decision_deck.jmcda.structure.thresholds.ThresholdsUtils;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decision_deck.jmcda.structure.weights.CoalitionsUtils;
import org.decision_deck.utils.collection.AbstractSetView;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataImpl.ProvideEvaluationsView;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.GroupSortingDataForwarder;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.GroupSortingDataImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.IGroupSortingData;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.ISortingPreferences;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.SortingPreferencesViewGroupBacked;
import org.decisiondeck.xmcda_oo.structure.SharedCoalitions;
import org.decisiondeck.xmcda_oo.structure.SharedProfilesEvaluations;
import org.decisiondeck.xmcda_oo.structure.SharedThresholds;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class GroupSortingPreferencesImpl extends GroupSortingDataForwarder implements IGroupSortingPreferences {

    @Override
    public Set<Alternative> getProfiles() {
	return new AbstractSetView<Alternative>(delegate().getProfiles()) {
	    @Override
	    public void beforeRemove(Object object) {
		if (object instanceof Alternative) {
		    Alternative profile = (Alternative) object;
		    m_allProfilesEvaluations.remove(profile);
		}
	    }
	};
    }

    @Override
    public Set<Criterion> getCriteria() {
	return new AbstractSetView<Criterion>(delegate().getCriteria()) {
	    @Override
	    protected void beforeRemove(Object o) {
		if (o instanceof Criterion) {
		    Criterion criterion = (Criterion) o;
		    beforeRemoveCriterion(criterion);
		}
	    }
	};
    }

    static private class ProvideCoalitionsView implements Function<Coalitions, Coalitions> {
	public ProvideCoalitionsView() {
	    /** Public constructor. */
	}

	@Override
	public Coalitions apply(Coalitions from) {
	    return CoalitionsUtils.asReadView(from);
	}
    }

    static private class ProvideThresholdsView implements Function<Thresholds, Thresholds> {
	@Override
	public Thresholds apply(Thresholds from) {
	    return ThresholdsUtils.getReadView(from);
	}
    }

    private final SharedCoalitions m_allCoalitions;
    private final SharedProfilesEvaluations m_allProfilesEvaluations;
    private final SharedThresholds m_allThresholds;
    private final ProvideCoalitionsView m_provideCoalitionsView = new ProvideCoalitionsView();

    private final ProvideEvaluationsView m_provideEvaluationsView = new ProvideEvaluationsView();
    private final ProvideThresholdsView m_provideThresholdsView = new ProvideThresholdsView();

    public GroupSortingPreferencesImpl() {
	this(new GroupSortingDataImpl());
    }

    /**
     * Decorates a {@link IGroupSortingData} and adds preferences functionality to it. External references to the
     * delegate should <em>not</em> be kept.
     * 
     * @param delegate
     *            not <code>null</code>.
     */
    public GroupSortingPreferencesImpl(IGroupSortingData delegate) {
	super(delegate);
	Preconditions.checkNotNull(delegate);
	m_allProfilesEvaluations = new SharedProfilesEvaluations();
	m_allCoalitions = new SharedCoalitions();
	m_allThresholds = new SharedThresholds();
    }

    @Override
    public Map<DecisionMaker, Coalitions> getCoalitions() {
	final Map<DecisionMaker, Coalitions> transformed = Maps.transformValues(m_allCoalitions.getAll(),
		m_provideCoalitionsView);
	return Collections.unmodifiableMap(transformed);
    }

    @Override
    public Coalitions getCoalitions(DecisionMaker dm) {
	if (dm == null) {
	    throw new NullPointerException();
	}
	if (!getDms().contains(dm)) {
	    return null;
	}
	final Coalitions coalitions = m_allCoalitions.get(dm);
	return m_provideCoalitionsView.apply(coalitions);
    }

    @Override
    public ISortingPreferences getPreferences(DecisionMaker dm) {
	Preconditions.checkNotNull(dm);
	if (!getDms().contains(dm)) {
	    return null;
	}
	return new SortingPreferencesViewGroupBacked(this, dm);
    }

    @Override
    public Map<DecisionMaker, EvaluationsRead> getProfilesEvaluations() {
	final Map<DecisionMaker, Evaluations> all = m_allProfilesEvaluations.getAll();
	final Map<DecisionMaker, EvaluationsRead> transformed = Maps.transformValues(all, m_provideEvaluationsView);
	return Collections.unmodifiableMap(transformed);
    }

    @Override
    public EvaluationsRead getProfilesEvaluations(DecisionMaker dm) {
	if (dm == null) {
	    throw new NullPointerException();
	}
	if (!getDms().contains(dm)) {
	    return null;
	}
	return m_provideEvaluationsView.apply(m_allProfilesEvaluations.get(dm));
    }

    @Override
    public Coalitions getSharedCoalitions() {
	return m_provideCoalitionsView.apply(m_allCoalitions.getShared());
    }

    @Override
    public EvaluationsRead getSharedProfilesEvaluations() {
	return m_provideEvaluationsView.apply(m_allProfilesEvaluations.getShared());
    }

    @Override
    public Thresholds getSharedThresholds() {
	return m_provideThresholdsView.apply(m_allThresholds.getShared());
    }

    @Override
    public Map<DecisionMaker, Thresholds> getThresholds() {
	final Map<DecisionMaker, Thresholds> transformed = Maps.transformValues(m_allThresholds.getAll(),
		m_provideThresholdsView);
	return Collections.unmodifiableMap(transformed);
    }

    @Override
    public Thresholds getThresholds(DecisionMaker dm) {
	if (dm == null) {
	    throw new NullPointerException();
	}
	if (!getDms().contains(dm)) {
	    return null;
	}
	return m_provideThresholdsView.apply(m_allThresholds.get(dm));
    }

    @Override
    public Double getWeight(DecisionMaker dm, Criterion criterion) {
	if (dm == null || criterion == null) {
	    throw new NullPointerException();
	}
	if (!getDms().contains(dm) || !getCriteria().contains(criterion)) {
	    return null;
	}
	Coalitions coalitions = m_allCoalitions.get(dm);
	return coalitions.getWeights().get(criterion);
    }

    @Override
    public boolean setCoalitions(DecisionMaker dm, Coalitions coalitions) {
	Preconditions.checkNotNull(dm, coalitions);
	if (coalitions == null && !getDms().contains(dm)) {
	    throw new IllegalArgumentException("Given dm unknown: " + dm + " but null coalitions.");
	}

	if (coalitions == null) {
	    return m_allCoalitions.empty(dm);
	}

	addDm(dm);
	getCriteria().addAll(coalitions.getCriteria());

	final boolean changed = m_allCoalitions.merge(dm, coalitions);
	return changed;
    }

    @Override
    public void setKeepSharedCoalitions(boolean keepShared) {
	m_allCoalitions.setKeepShared(keepShared);
    }

    @Override
    public void setKeepSharedProfilesEvaluations(boolean keepShared) {
	m_allProfilesEvaluations.setKeepShared(keepShared);
    }

    @Override
    public void setKeepSharedThresholds(boolean keepShared) {
	m_allThresholds.setKeepShared(keepShared);
    }

    @Override
    public boolean setProfilesEvaluation(DecisionMaker dm, Alternative alternative, Criterion criterion, Double value) {
	if (dm == null || alternative == null || criterion == null) {
	    throw new NullPointerException("" + dm + alternative + criterion);
	}
	if (value == null
		&& (!getDms().contains(dm) || !getAlternatives().contains(alternative) || !getCriteria().contains(
			criterion))) {
	    throw new IllegalArgumentException("Object not found but value is null.");
	}

	final boolean changeOccurred;
	if (value == null) {
	    changeOccurred = m_allProfilesEvaluations.removeEvaluation(dm, alternative, criterion);
	} else {
	    addDm(dm);
	    getProfiles().add(alternative);
	    getCriteria().add(criterion);
	    changeOccurred = m_allProfilesEvaluations.putEvaluation(dm, alternative, criterion, value.doubleValue());
	}

	return changeOccurred;
    }

    @Override
    public boolean setProfilesEvaluations(DecisionMaker dm, EvaluationsRead evaluations) {
	if (dm == null) {
	    throw new NullPointerException("" + evaluations);
	}
	if (evaluations == null && !getDms().contains(dm)) {
	    throw new IllegalArgumentException("Null evaluations but unknown decision maker: " + dm + ".");
	}
	final boolean added = addDm(dm);

	final boolean changeOccurred;

	if (evaluations == null) {
	    changeOccurred = m_allProfilesEvaluations.empty(dm);
	} else {
	    getProfiles().addAll(evaluations.getRows());
	    getCriteria().addAll(evaluations.getColumns());
	    changeOccurred = m_allProfilesEvaluations.mergeExtended(dm, evaluations);
	}

	return changeOccurred || added;
    }

    @Override
    public boolean setSharedCoalitions(Coalitions coalitions) {
	final boolean changed = m_allCoalitions.replaceShared(coalitions);
	return changed;
    }

    @Override
    public boolean setSharedProfilesEvaluations(EvaluationsRead profilesEvaluations) {
	final boolean changed = m_allProfilesEvaluations.replaceShared(profilesEvaluations);
	return changed;
    }

    @Override
    public boolean setSharedThresholds(Thresholds thresholds) {
	final boolean changed = m_allThresholds.replaceShared(thresholds);
	return changed;
    }

    @Override
    public boolean setThresholds(DecisionMaker dm, Thresholds thresholds) {
	if (dm == null) {
	    throw new NullPointerException("" + thresholds);
	}
	if (thresholds == null && !getDms().contains(dm)) {
	    throw new IllegalArgumentException("Null thresholds but dm unknown: " + dm + ".");
	}

	final boolean changeOccurred;

	if (thresholds == null) {
	    changeOccurred = m_allThresholds.empty(dm);
	} else {
	    final boolean added = addDm(dm);
	    getCriteria().addAll(thresholds.getCriteria());
	    final boolean changed = m_allThresholds.merge(dm, thresholds);
	    changeOccurred = added || changed;
	}

	return changeOccurred;
    }

    private boolean addDm(DecisionMaker dm) {
	final boolean changed = super.getDms().add(dm);
	if (changed) {
	    m_allProfilesEvaluations.addDm(dm);
	    m_allCoalitions.addDm(dm);
	    m_allThresholds.addDm(dm);
	}
	return changed;
    }

    private void beforeRemoveCriterion(Criterion criterion) {
	m_allCoalitions.remove(criterion);
	m_allProfilesEvaluations.remove(criterion);
	m_allThresholds.remove(criterion);
    }

    @Override
    public ISortingPreferences getSharedPreferences() {
	return new SortingPreferencesViewGroupBacked(this);
    }

    @Override
    public Set<DecisionMaker> getDms() {
	return new AbstractSetView<DecisionMaker>(delegate().getDms()) {
	    @Override
	    public boolean add(DecisionMaker dm) {
		return addDm(dm);
	    }

	    @Override
	    public void beforeRemove(Object object) {
		if (object instanceof DecisionMaker) {
		    DecisionMaker dm = (DecisionMaker) object;
		    m_allCoalitions.remove(dm);
		    m_allProfilesEvaluations.remove(dm);
		    m_allThresholds.remove(dm);
		}
	    }
	};
    }

}
