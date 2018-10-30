package org.decisiondeck.jmcda.structure.sorting.problem.data;

import java.util.Collection;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.matrix.EvaluationsRead;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decision_deck.utils.IObserver;
import org.decision_deck.utils.collection.AbstractSetView;
import org.decision_deck.utils.collection.ForwardingSetChangeableDelegate;
import org.decision_deck.utils.collection.extensional_order.ExtensionalComparator;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * <p>
 * This object decorates another sorting data structure to define the ordering of objects it contains. The defined order
 * is observed when iterating over the sets this structure returns, e.g. {@link #getAlternatives()}. Alternatives,
 * profiles, criteria may be ordered in various ways, and it is possible to order each object type according to its own
 * ordering rule. Instead of using this object as a decorator of an other structure, it is possible to create a
 * structure from zero.
 * </p>
 * <p>
 * For the mathematically inclined, a bit of notations will help making the following explanations more precise.
 * Consider the set <em>S</em> of all relevant objects. Relevant object here means the set of all possible alternatives,
 * or the set of all possible profiles, or the set of all possible criteria. Consider the subset <em>S*</em> of relevant
 * objects used in this object at a given time. E.g., if this object contains three alternatives at a given time, the
 * cardinality of <em>S*</em> is three whereas the cardinality of <em>S</em> is always infinite. In the following
 * explanations, an order denotes an partial order (a poset) on <em>S</em>. Such an order is said to be complete when it
 * is, in the mathematical sense, a complete order on <em>S</em>, thus it is defined for every possible pair of relevant
 * objects. In the contrary case it is said to be incomplete. The natural ordering on alternatives is a complete order.
 * A specific order that would define the order between three specific alternatives contained in this object at a given
 * time is not complete in that sense. Furthermore, an order is said to be well-defined if, at any given time, it is
 * complete on <em>S*</em>, meaning that it defines the order between every relevant objects contained in this object.
 * This is a weaker condition than being complete. Suppose that this object is used in a specific context in such a way
 * that it never contains any alternatives different than alternatives from a given set <em>A</em> of ten alternatives.
 * Suppose that the order defined in this object for alternatives is a specific order on these ten alternatives. The
 * order is not complete in the sense defined here (because it is not complete on <em>S</em>, although it is complete on
 * <em>S*</em>). However, the order is well-defined. Now suppose that the order is rather an order defined on only three
 * alternatives among the set of ten. Then it is possible that the order is well-defined at a given time, because this
 * object would contain only these three alternatives or a subset of these. It does not guarantee however that this
 * order will always be well-defined. Adding a fourth alternative to this object makes the order not well-defined.
 * Complete orders obviously do not cause any type of problems while using incomplete orders in this object require some
 * caution. More explanations are given below.
 * </p>
 * <p>
 * The following order rules may be used for each relevant object type.
 * <ul>
 * <li>The natural ordering may be used, which will return the objects sorted by alphabetical order of their ids, as
 * defined by the default string comparator.</li>
 * <li>Order of the delegate may be used, which means this object does not change the ordering and returns the objects
 * in the order used by the delegate.</li>
 * <li>It is possible to set a specific comparator to define the order. In that case, the given comparator must be
 * defined over the set of objects used in this object, i.e. it must accept to compare any relevant object (i.e.
 * alternative, profile or criterion) defined in this object. Using the above definition, it must be well-defined.</li>
 * <li>It is also possible to use order of additions, but this can be defined only when creating this object, and only
 * when creating this object from zero (not from an other sorting data). Once changed for a different order, the order
 * of additions is lost.</li>
 * <li>It is possible to give a specific ordering using any collection or relevant objects: the collection iteration
 * order is used to define the order and this object then returns the relevant objects in the defined order. In that
 * case, the given collection defining the order must be a superset of the objects contained in this object, thus it
 * must define the order of the whole set of objects. Caution should be exerciced when using this functionality when the
 * data in this object change, as the defined order must be well-defined. The easiest way to avoid problems when using
 * such an incomplete order is to use this capability only with immutable sorting structures. This permits to ensure
 * that the order will always be well-defined, provided it is well-defined at the time it is set. After such an order
 * has been defined in this object, trying to add an object that is not in the ordered set in the set of relevant
 * objects will throw an {@link IllegalStateException}. For example, if this object contains three criteria, and a order
 * is set defining only the order on these three criteria, adding a fourth criterion to this object will throw an
 * exception.</li>
 * <li>The profiles may use yet an other order definition based on the preference order. When this is used, the first
 * profiles in the order are those that are defined through the categories object (see {@link #getCatsAndProfs()}), and
 * they are ordered from the worst to the best. Then, the natural order of the profiles is used to sort the remaining
 * profiles (those in {@link #getProfiles()} that are not in the previous object). This functionality is probably most
 * useful when the user of this object knows that the categories are fully defined, that is that it contains all
 * profiles defined in this object, in which case the whole set of profiles used in this object is ordered by
 * preference. Using such an order amounts to use a complete order, thus no problem occurs when adding a profile.</li>
 * </ul>
 * </p>
 * <p>
 * The evaluations methods (e.g. {@link #getAlternativesEvaluations()}) do not take the order into account, to iterate
 * in order it is necessary to use e.g. {@link #getAlternatives()} rather than {@link EvaluationsRead#getRows()}. Same
 * for scales, etc.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class SortingDataWithOrder extends SortingDataForwarder implements ISortingData {
    static protected enum OrderType {
	DELEGATE, COMPARATOR, ADDITIONS, GIVEN
    }

    /**
     * Creates a new object delegating to the given data. All modifications to the delegate must go through this object.
     * The default order uses the natural ordering.
     * 
     * @param delegate
     *            not <code>null</code>.
     */
    public SortingDataWithOrder(ISortingData delegate) {
	super(delegate);

	final Comparator<Alternative> alt = Ordering.natural();
	m_alternativesView = new ForwardingSetChangeableDelegate<Alternative>(getAlternativesView(alt));
	m_profilesView = new ForwardingSetChangeableDelegate<Alternative>(getProfilesView(alt));
	final Comparator<Criterion> crit = Ordering.natural();
	m_criteriaView = new ForwardingSetChangeableDelegate<Criterion>(getCriteriaView(crit));

	delegate().getCatsAndProfs().addObserverAddedProfile(new IObserver<Alternative>() {
	    @Override
	    public void update(Alternative updated) {
		if (m_profilesOrderedSet != null) {
		    if (updated != null) {
			m_profilesOrderedSet.add(updated);
		    } else {
			m_profilesOrderedSet.addAll(delegate().getProfiles());
		    }
		}
	    }
	});
    }

    /**
     * Creates a new object, with a default order reflecting the order of additions for alternatives, criteria. Profiles
     * are ordered by preference.
     */
    public SortingDataWithOrder() {
	this(new SortingDataImpl());
	setAlternativesOrderByDelegate();
	setCriteriaOrderByDelegate();
	setProfilesOrderByPreference();
    }

    /**
     * A set which duplicates the data found in the delegate sorting data. This set and the data in the delegate are
     * kept in sync by this object. Is <code>null</code> iff the delegate data order is used.
     */
    private NavigableSet<Alternative> m_alternativesOrderedSet;
    /**
     * Delegates either to this object's sorting data delegate (simple case of the delegate order), or to the ordered
     * version provided by this object (for the more complex cases of user-asked order).
     */
    private final ForwardingSetChangeableDelegate<Alternative> m_alternativesView;
    /**
     * A set which duplicates the data found in the delegate sorting data and implementing the required sorting order.
     * This set and the data in the delegate are kept in sync by this object. Is <code>null</code> iff the delegate data
     * order is used.
     */
    private NavigableSet<Alternative> m_profilesOrderedSet;
    /**
     * Delegates either to this object's sorting data delegate (simple case of the delegate order), or to the ordered
     * version provided by this object (for the more complex cases of user-asked order).
     */
    private final ForwardingSetChangeableDelegate<Alternative> m_profilesView;
    private Set<Criterion> m_criteriaOrderedSet;
    /**
     * Delegates either to this object's sorting data delegate (simple case of the delegate order), or to the ordered
     * version provided by this object (for the more complex cases of user-asked order).
     * 
     */
    private final ForwardingSetChangeableDelegate<Criterion> m_criteriaView;

    /**
     * Returns first all the alternatives in the order specified, then all the profiles in the order specified.
     * 
     * @return a read-only view of all alternatives (i.e., alternatives and profiles). The returned set is the union of
     *         the alternatives and the profiles sets.
     */
    @Override
    public Set<Alternative> getAllAlternatives() {
	return Sets.union(getAlternatives(), getProfiles());
    }

    @Override
    public Set<Criterion> getCriteria() {
	return m_criteriaView;
    }

    @Override
    public boolean setEvaluation(Alternative alternative, Criterion criterion, Double value) {
	getAlternatives().add(alternative);
	getCriteria().add(criterion);
	return delegate().setEvaluation(alternative, criterion, value);
    }

    @Override
    public boolean setEvaluations(EvaluationsRead evaluations) {
	for (Alternative alternative : evaluations.getRows()) {
	    getAlternatives().add(alternative);
	}
	for (Criterion criterion : evaluations.getColumns()) {
	    getCriteria().add(criterion);
	}
	return delegate().setEvaluations(evaluations);
    }

    public void setCriteriaComparator(Comparator<Criterion> criteriaComparator) {
	m_criteriaView.setDelegate(getCriteriaView(criteriaComparator));
    }

    public void setCriteriaOrderByDelegate() {
	m_criteriaView.setDelegate(delegate().getCriteria());
	m_criteriaOrderedSet = null;
    }

    public void setCriteriaOrder(Collection<Criterion> order) {
	setCriteriaComparator(ExtensionalComparator.create(order));
    }

    @Override
    public Set<Alternative> getProfiles() {
	return m_profilesView;
	// switch (m_profilesOrderType) {
	// case ADDITIONS:
	// /**
	// * Same as GIVEN, except that the external comparator should be updated when a profile is added. Is not
	// * really useful however, as order of the delegate (which uses order of additions) may be used.
	// */
	// throw new UnsupportedOperationException("Not implemented.");
	// case GIVEN:
	// case COMPARATOR:
	// case DELEGATE:
	// return m_profilesView;
	// }
	// throw new IllegalStateException("Unknown order type.");
    }

    public void setProfilesComparator(Comparator<Alternative> profilesComparator) {
	m_profilesView.setDelegate(getProfilesView(profilesComparator));
    }

    private Set<Criterion> getCriteriaView(Comparator<Criterion> comparator) {
	final Set<Criterion> delegateSet = delegate().getCriteria();

	final TreeSet<Criterion> orderedSet = new TreeSet<Criterion>(comparator);
	m_criteriaOrderedSet = orderedSet;
	orderedSet.addAll(delegateSet);
	return getView(orderedSet, delegateSet);
    }

    protected <T> Set<T> getView(final Set<T> orderedSet, final Set<T> setToUpdate) {
	final AbstractSetView<T> orderedView = new AbstractSetView<T>(orderedSet) {
	    @Override
	    public boolean add(T e) {
		setToUpdate.add(e);
		return super.add(e);
	    }

	    @Override
	    protected void beforeRemove(Object object) {
		setToUpdate.remove(object);
	    }
	};

	return orderedView;
    }

    public void setProfilesOrder(Collection<Alternative> order) {
	setProfilesComparator(ExtensionalComparator.create(order));
    }

    public void setProfilesOrderByPreference() {
	setProfilesComparator(new Comparator<Alternative>() {
	    @Override
	    public int compare(Alternative p1, Alternative p2) {
		final boolean p1pref = getCatsAndProfs().getProfiles().contains(p1);
		final boolean p2pref = getCatsAndProfs().getProfiles().contains(p2);
		if (p1pref && !p2pref) {
		    return -1;
		}
		if (p2pref && !p1pref) {
		    return 1;
		}
		if (!p1pref && !p2pref) {
		    final Comparator<Alternative> natural = Ordering.natural();
		    return natural.compare(p1, p2);
		}
		return getCatsAndProfs().getProfiles().comparator().compare(p1, p2);
	    }
	});
    }

    public void setProfilesOrderByDelegate() {
	m_profilesView.setDelegate(delegate().getProfiles());
	m_profilesOrderedSet = null;
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return m_alternativesView;
    }

    public void setAlternativesComparator(Comparator<Alternative> alternativesComparator) {
	m_alternativesView.setDelegate(getAlternativesView(alternativesComparator));
    }

    public void setAllNaturalOrder() {
	final Comparator<Alternative> alt = Ordering.natural();
	setAlternativesComparator(alt);
	setProfilesComparator(alt);
	final Comparator<Criterion> crit = Ordering.natural();
	setCriteriaComparator(crit);
    }

    public void setAlternativesOrder(Collection<Alternative> order) {
	setAlternativesComparator(ExtensionalComparator.create(order));
    }

    public void setAlternativesOrderByDelegate() {
	m_alternativesView.setDelegate(delegate().getAlternatives());
	m_alternativesOrderedSet = null;
    }

    @Override
    public CatsAndProfs getCatsAndProfs() {
	return delegate().getCatsAndProfs();
    }

    private Set<Alternative> getProfilesView(Comparator<Alternative> profilesComparator) {
	final Set<Alternative> delegateSet = delegate().getProfiles();

	m_profilesOrderedSet = new TreeSet<Alternative>(profilesComparator);
	m_profilesOrderedSet.addAll(delegateSet);
	return getView(m_profilesOrderedSet, delegateSet);
    }

    private Set<Alternative> getAlternativesView(Comparator<Alternative> comparator) {
	final Set<Alternative> delegateSet = delegate().getAlternatives();

	m_alternativesOrderedSet = new TreeSet<Alternative>(comparator);
	m_alternativesOrderedSet.addAll(delegateSet);
	return getView(m_alternativesOrderedSet, delegateSet);
    }

}
