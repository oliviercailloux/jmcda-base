package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.NavigableSet;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decision_deck.utils.collection.CollectionUtils;
import org.decision_deck.utils.collection.extensional_order.ExtentionalTotalOrder;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

/**
 * <p>
 * A view of an ordered assignments object that views only the edges of the assignments. If an alternative is assigned,
 * in the delegate assignments, to a set of categories C1 to C4, for example, and the delegate assignments has
 * categories (in order) C1, C2, C3, C4, then this view only views the alternative as assigned to C1 and C4. If an
 * alternative is assigned to a single category in the underlying assignments object, this views also shows it as being
 * assigned to a unique category.
 * </p>
 * <p>
 * This view requires that the underlying assignments has contiguous assignments, meaning that, in the previous example,
 * an alternative can't be assigned to C1, C2, and C4, with a “hole” in C3. In such a case, requesting the assignments
 * of the said alternative would throw an exception. This requirement guarantees that all assigned alternatives are seen
 * through this object as being assigned to one or two categories, never more.
 * </p>
 * <p>
 * The view could be writable, accepting assignments to one or two categories and delegating a write to all categories
 * in between (although should not implement {@link IOrderedAssignmentsToMultiple} because the contracts are not
 * compatible) but this has not been implemented yet.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 */
public class OrderedAssignmentsToMultipleEdgesView implements IOrderedAssignmentsToMultipleRead {

    private final IOrderedAssignmentsToMultipleRead m_delegate;

    /**
     * Creates a view of the given delegate that views only the edges assignments. It should be made certain that the
     * delegate will always contain contiguous assignments.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public OrderedAssignmentsToMultipleEdgesView(IOrderedAssignmentsToMultipleRead delegate) {
	Preconditions.checkNotNull(delegate);
	m_delegate = delegate;
    }

    @Override
    public Set<Alternative> getAlternatives(final Category category) {
	return Sets.filter(m_delegate.getAlternatives(category), new Predicate<Alternative>() {
	    @Override
	    public boolean apply(Alternative input) {
		final NavigableSet<Category> assignedTo = m_delegate.getCategories(input);
		assert (assignedTo != null);
		if (!CollectionUtils.isContiguous(assignedTo, m_delegate.getCategories())) {
		    throw new IllegalStateException("Assigned to a non contiguous set of categories: " + input + " to "
			    + assignedTo + ".");
		}
		final boolean isEdge = assignedTo.first().equals(category) || assignedTo.last().equals(category);
		return isEdge;
	    }
	});
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return m_delegate.getAlternatives();
    }

    @Override
    public NavigableSet<Category> getCategories(Alternative alternative) {
	final NavigableSet<Category> assignedTo = m_delegate.getCategories(alternative);
	if (assignedTo == null) {
	    return null;
	}
	if (!CollectionUtils.isContiguous(assignedTo, m_delegate.getCategories())) {
	    throw new IllegalStateException("Assigned to a non contiguous set of categories: " + alternative + " to "
		    + assignedTo + ".");
	}
	Set<Category> edges = Sets.newLinkedHashSet();
	edges.add(assignedTo.first());
	edges.add(assignedTo.last());
	return ExtentionalTotalOrder.create(edges);
    }

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof IOrderedAssignmentsToMultipleRead)) {
	    return false;
	}
	IOrderedAssignmentsToMultipleRead a2 = (IOrderedAssignmentsToMultipleRead) obj;
	return AssignmentsUtils.equivalentOrderedToMultiple(this, a2);
    }

    @Override
    public int hashCode() {
	return AssignmentsUtils.getEquivalenceRelationOrderedToMultiple().hash(this);
    }

    protected IOrderedAssignmentsToMultipleRead delegate() {
	return m_delegate;
    }

    @Override
    public NavigableSet<Category> getCategories() {
	return m_delegate.getCategories();
    }

}
