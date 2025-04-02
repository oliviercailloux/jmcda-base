package org.decisiondeck.jmcda.structure.sorting.problem.group_data;

import java.util.Collections;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataFiltering;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataForwarder;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * <p>
 * A read-only view. Restricting to a subset of alternatives is possible when the object is created. The filtering only
 * (possibly) impacts the alternatives. Profiles are untouched.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class GroupSortingDataFiltering extends SortingDataForwarder implements IGroupSortingData {

    private final IGroupSortingData m_delegate;

    /**
     * Creates a view that only view the alternatives admitted by the given filter, or equivalently, that filters out
     * the alternatives that do not pass the given filter. This only concerns the real alternatives, the profiles are
     * untouched. To filter a constant set of alternatives, use {@link Predicates#in(java.util.Collection)}.
     * 
     * @param delegate
     *            not {@code null}.
     * @param filterAlternatives
     *            {@code null} to allow everything (equivalent to {@link Predicates#alwaysTrue()}, i.e. to not
     *            filter.
     */
    public GroupSortingDataFiltering(IGroupSortingData delegate, Predicate<Alternative> filterAlternatives) {
	/**
	 * It is not necessary to wrap the delegate into a read-only view because the delegate of the delegate is
	 * already a read-only view for all the data methods.
	 */
	// super(new GroupSortingDataRead(new GroupSortingDataImpl(new SortingDataRestrict(restricted))));
	super(new SortingDataFiltering(delegate, filterAlternatives, null));
	m_delegate = delegate;
    }

    /**
     * Creates a read-only view of the given data.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public GroupSortingDataFiltering(IGroupSortingData delegate) {
	this(delegate, Predicates.<Alternative> alwaysTrue());
    }

    @Override
    protected SortingDataFiltering delegate() {
	return (SortingDataFiltering) super.delegate();
    }

    @Override
    public Set<DecisionMaker> getDms() {
	return Collections.unmodifiableSet(m_delegate.getDms());
    }



    /**
     * @return {@code null} for everything allowed.
     */
    public Predicate<Alternative> getAlternativesFilter() {
	return delegate().getAlternativesPredicate();
    }

    /**
     * NB currently not really implemented, restricts nothing.
     * 
     * @return always {@code true}.
     */
    public Predicate<Alternative> getProfilesFilter() {
	return Predicates.alwaysTrue();
    }

}
