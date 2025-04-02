package org.decisiondeck.jmcda.structure.sorting.problem.group_data;

import java.util.Collection;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.utils.collection.ForwardingSetChangeableDelegate;
import org.decision_deck.utils.collection.extensional_order.ExtensionalComparator;
import org.decisiondeck.jmcda.structure.sorting.problem.data.SortingDataWithOrder;

import com.google.common.collect.Ordering;

public class GroupSortingDataWithOrder extends SortingDataWithOrder implements IGroupSortingData {

    /**
     * A set which duplicates the data found in the delegate sorting data and implementing the required sorting order.
     * This set and the data in the delegate are kept in sync by this object. Is {@code null} iff the delegate data
     * order is used.
     */
    private NavigableSet<DecisionMaker> m_dmsOrderedSet;
    /**
     * Delegates either to this object's sorting data delegate (simple case of the delegate order), or to the ordered
     * version provided by this object (for the more complex cases of user-asked order).
     */
    private final ForwardingSetChangeableDelegate<DecisionMaker> m_dmsView;

    @Override
    public void setAllNaturalOrder() {
	super.setAllNaturalOrder();
	final Comparator<DecisionMaker> dm = Ordering.natural();
	setDmsComparator(dm);
    }



    /**
     * Creates a new object delegating to the given data. All modifications to the delegate must go through this object.
     * The default order uses the natural ordering.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public GroupSortingDataWithOrder(IGroupSortingData delegate) {
	super(delegate);
	final Comparator<DecisionMaker> dms = Ordering.natural();
	m_dmsView = new ForwardingSetChangeableDelegate<DecisionMaker>(getDmsView(dms));
    }

    @Override
    protected IGroupSortingData delegate() {
	return (IGroupSortingData) super.delegate();
    }

    @Override
    public Set<DecisionMaker> getDms() {
	return m_dmsView;
    }

    public void setDmsComparator(Comparator<DecisionMaker> dmsComparator) {
	m_dmsView.setDelegate(getDmsView(dmsComparator));
    }

    public void setDmsOrder(Collection<DecisionMaker> order) {
	setDmsComparator(ExtensionalComparator.create(order));
    }

    /**
     * Retrieves the comparator defining the ordering on the decision makers used in this object. The comparator may not
     * define a total order, i.e. it may be defined over only a subset of the possible decision makers. However, the
     * returned comparator, if not {@code null}, is defined over the whole set of decision makers used in this
     * object at the time this method returns.
     * 
     * @return {@code null} iff no comparator set (uses delegate order).
     */
    public Comparator<? super DecisionMaker> getDmsComparator() {
	return m_dmsOrderedSet == null ? null : m_dmsOrderedSet.comparator();
    }

    public void setDmsOrderByDelegate() {
	m_dmsView.setDelegate(delegate().getDms());
	m_dmsOrderedSet = null;
    }

    private Set<DecisionMaker> getDmsView(Comparator<DecisionMaker> dmsComparator) {
	final Set<DecisionMaker> delegateSet = delegate().getDms();

	m_dmsOrderedSet = new TreeSet<DecisionMaker>(dmsComparator);
	m_dmsOrderedSet.addAll(delegateSet);
	final NavigableSet<DecisionMaker> orderedSet = m_dmsOrderedSet;

	return getView(orderedSet, delegateSet);
    }
}
