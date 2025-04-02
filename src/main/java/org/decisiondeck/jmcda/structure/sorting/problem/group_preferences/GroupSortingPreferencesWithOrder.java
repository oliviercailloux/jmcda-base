package org.decisiondeck.jmcda.structure.sorting.problem.group_preferences;

import java.util.Map;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.thresholds.Thresholds;
import org.decision_deck.jmcda.structure.weights.Coalitions;
import org.decision_deck.utils.collection.SetBackedMap;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.GroupSortingDataWithOrder;
import org.decisiondeck.jmcda.structure.sorting.problem.group_data.IGroupSortingData;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.ISortingPreferences;
import org.decisiondeck.jmcda.structure.sorting.problem.preferences.SortingPreferencesViewGroupBacked;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * <p>
 * A group preferences object that provides ordering possibilities over its sets of criteria, alternatives, etc. The
 * maps (e.g. {@link #getAllEvaluations()}) returned by this object are also iterated in the order determined by this
 * object.
 * </p>
 * <p>
 * This object is a (partially) read-only view. Implementing the setter methods has not been done because it is (a bit)
 * tricky and it is probably not useful. Implementing would probably require adding the possibility to observe changes
 * in the sets of criteria, decision makers, alternatives in the delegate {@link IGroupSortingPreferences}. It is
 * <em>not</em> permitted to change the data through the delegate object, doing this would yield unpredictable results
 * on this object state. It is permitted to change the data this object contains through the setter methods in
 * {@link IGroupSortingData}: those methods are implemented and this object will keep in sync with this data if the
 * modification is done through this object. E.g. calling {@link #getDms()} then add a dm through this object is
 * permitted, but not calling {@link #setCoalitions(DecisionMaker, Coalitions)}.
 * </p>
 * 
 * @see GroupSortingDataWithOrder
 * 
 * @author Olivier Cailloux
 * 
 */
public class GroupSortingPreferencesWithOrder extends GroupSortingDataWithOrder implements IGroupSortingPreferences {

    /**
     * Creates a new object delegating to the given data. All modifications to the delegate must go through this object.
     * The default order uses the natural ordering.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public GroupSortingPreferencesWithOrder(IGroupSortingPreferences delegate) {
	super(delegate);
    }

    @Override
    protected IGroupSortingPreferences delegate() {
	return (IGroupSortingPreferences) super.delegate();
    }



    @Override
    public Map<DecisionMaker, Coalitions> getCoalitions() {
	return new SetBackedMap<DecisionMaker, Coalitions>(super.getDms(), new Function<DecisionMaker, Coalitions>() {
	    @Override
	    public Coalitions apply(DecisionMaker input) {
		return delegate().getCoalitions(input);
	    }
	});
    }

    @Override
    public boolean setProfilesEvaluation(DecisionMaker dm, Alternative alternative, Criterion criterion, Double value) {
	throw new UnsupportedOperationException("Writing to this ordered object is unsupported.");
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
    public Coalitions getCoalitions(DecisionMaker dm) {
	return delegate().getCoalitions(dm);
    }

    @Override
    public Double getWeight(DecisionMaker dm, Criterion criterion) {
	return delegate().getWeight(dm, criterion);
    }

    @Override
    public boolean setThresholds(DecisionMaker dm, Thresholds thresholds) {
	throw new UnsupportedOperationException("Writing to this ordered object is unsupported.");
    }

    @Override
    public Map<DecisionMaker, Thresholds> getThresholds() {
	return new SetBackedMap<DecisionMaker, Thresholds>(super.getDms(),
		new Function<DecisionMaker, Thresholds>() {
		    @Override
		    public Thresholds apply(DecisionMaker input) {
			return delegate().getThresholds(input);
		    }
		});
    }

    @Override
    public Thresholds getThresholds(DecisionMaker dm) {
	return delegate().getThresholds(dm);
    }

    @Override
    public Map<DecisionMaker, EvaluationsRead> getProfilesEvaluations() {
	return new SetBackedMap<DecisionMaker, EvaluationsRead>(super.getDms(),
		new Function<DecisionMaker, EvaluationsRead>() {
		    @Override
		    public EvaluationsRead apply(DecisionMaker input) {
			return delegate().getProfilesEvaluations(input);
		    }
		});
    }



    @Override
    public EvaluationsRead getProfilesEvaluations(DecisionMaker dm) {
	return delegate().getProfilesEvaluations(dm);
    }

    @Override
    public EvaluationsRead getSharedProfilesEvaluations() {
	return delegate().getSharedProfilesEvaluations();
    }

    @Override
    public void setKeepSharedThresholds(boolean keepShared) {
	throw new UnsupportedOperationException("Writing to this ordered object is unsupported.");
    }

    @Override
    public void setKeepSharedCoalitions(boolean keepShared) {
	throw new UnsupportedOperationException("Writing to this ordered object is unsupported.");
    }

    @Override
    public void setKeepSharedProfilesEvaluations(boolean keepShared) {
	throw new UnsupportedOperationException("Writing to this ordered object is unsupported.");
    }

    @Override
    public boolean setSharedProfilesEvaluations(EvaluationsRead profilesEvaluations) {
	throw new UnsupportedOperationException("Writing to this ordered object is unsupported.");
    }

    @Override
    public boolean setCoalitions(DecisionMaker dm, Coalitions coalitions) {
	throw new UnsupportedOperationException("Writing to this ordered object is unsupported.");
    }

    @Override
    public boolean setProfilesEvaluations(DecisionMaker dm, EvaluationsRead evaluations) {
	throw new UnsupportedOperationException("Writing to this ordered object is unsupported.");
    }



    @Override
    public boolean setSharedThresholds(Thresholds thresholds) {
	throw new UnsupportedOperationException("Writing to this ordered object is unsupported.");
    }

    @Override
    public Coalitions getSharedCoalitions() {
	return delegate().getSharedCoalitions();
    }

    @Override
    public ISortingPreferences getSharedPreferences() {
	return new SortingPreferencesViewGroupBacked(this);
    }

    @Override
    public boolean setSharedCoalitions(Coalitions coalitions) {
	throw new UnsupportedOperationException("Writing to this ordered object is unsupported.");
    }

    @Override
    public Thresholds getSharedThresholds() {
	return delegate().getSharedThresholds();
    }
}
