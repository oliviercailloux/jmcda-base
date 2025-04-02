package org.decisiondeck.jmcda.structure.sorting.problem.group_results;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfsForwarder;
import org.decision_deck.utils.collection.AbstractSetView;
import org.decision_deck.utils.collection.CollectionUtils;
import org.decisiondeck.jmcda.structure.sorting.assignment.IOrderedAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.assignment.OrderedAssignmentsToMultipleForwarder;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsFactory;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;
import org.decisiondeck.jmcda.structure.sorting.problem.group_assignments.IGroupSortingAssignmentsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesForwarder;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.GroupSortingPreferencesImpl;
import org.decisiondeck.jmcda.structure.sorting.problem.group_preferences.IGroupSortingPreferences;
import org.decisiondeck.jmcda.structure.sorting.problem.results.ISortingResultsToMultiple;
import org.decisiondeck.jmcda.structure.sorting.problem.results.SortingResultsToMultipleViewGroupBacked;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.collect.Sets;

public class GroupSortingResultsToMultipleImpl extends GroupSortingPreferencesForwarder implements
	IGroupSortingResultsToMultiple {

    /**
     * Bound to a sorting object and a delegate (writeable) assignments object. When written, before writing to the
     * delegate, ensures that the sorting object contains the categories. Thus the categories used with these
     * assignments must be a subset of the categories in the sorting object. No categories are added or removed from the
     * sorting object. Alternatives are added to the sorting object when they are added to the assignments object.
     * 
     * @author Olivier Cailloux
     * 
     */
    static private class OrderedAssignmentsToMultipleWriteable extends OrderedAssignmentsToMultipleForwarder implements
	    IOrderedAssignmentsToMultiple {

	private final IGroupSortingAssignmentsToMultiple m_sorting;

	public OrderedAssignmentsToMultipleWriteable(IGroupSortingAssignmentsToMultiple assignments,
		IOrderedAssignmentsToMultiple delegate) {
	    super(delegate);
	    Preconditions.checkNotNull(assignments);
	    Preconditions.checkNotNull(delegate);
	    m_sorting = assignments;
	}

	@Override
	public boolean setCategories(Alternative alternative, Set<Category> categories) {
	    Preconditions.checkNotNull(alternative);
	    if (categories == null) {
		return delegate().setCategories(alternative, null);
	    }
	    Preconditions.checkArgument(getCategories().containsAll(categories));
	    Preconditions.checkArgument(m_sorting.getCatsAndProfs().getCategories().containsAll(categories));
	    m_sorting.getAlternatives().add(alternative);
	    return delegate().setCategories(alternative, categories);
	}

	@Override
	public boolean setCategories(SortedSet<Category> categories) {
	    if (categories == null) {
		return delegate().setCategories(null);
	    }
	    final Set<Category> theseCategories = m_sorting.getCatsAndProfs().getCategories();
	    Preconditions.checkArgument(CollectionUtils.containsInOrder(theseCategories, categories),
		    "These categories: " + theseCategories + " should contain in order : " + categories + ".");
	    return delegate().setCategories(categories);
	}

    }

    /**
     * <p>
     * To avoid wasting space, the assignments are created lazily: the key set is a subset of all decision makers, it
     * contains only those assignments which have been requested by the user. However when the user requests a map view
     * the map is populated.
     * </p>
     * <p>
     * No {@code null} values or keys (but assignments objects may be empty).
     * </p>
     * <p>
     * Once an assignment object has been created for a decision maker, it must not be replaced, but its internal state
     * must be modified. That is because external users may hold view objects to the assignments. When a decision maker
     * is removed, its assignments should be emptied before being deleted to ensure that the views do not reflect old
     * state.
     * </p>
     * <p>
     * The set of categories inside the assignment objects are maintained in sync with this object categories. When the
     * categories in this object change, they change in the underlying assignments. (This is not required by the
     * contract but is simpler.)
     * </p>
     */
    private final Map<DecisionMaker, IOrderedAssignmentsToMultiple> m_allAssignments = Maps.newLinkedHashMap();

    public GroupSortingResultsToMultipleImpl() {
	this(new GroupSortingPreferencesImpl());
    }

    /**
     * Decorates a {@link IGroupSortingPreferences} and adds assignments functionality to it. External references to the
     * delegate should <em>not</em> be kept.
     * 
     * @param delegate
     *            not {@code null}.
     */
    public GroupSortingResultsToMultipleImpl(IGroupSortingPreferences delegate) {
	super(delegate);
    }

    @Override
    public Map<DecisionMaker, IOrderedAssignmentsToMultiple> getAssignments() {
	final Map<DecisionMaker, IOrderedAssignmentsToMultiple> transformed = Maps.transformEntries(m_allAssignments,
		new EntryTransformer<DecisionMaker, IOrderedAssignmentsToMultiple, IOrderedAssignmentsToMultiple>() {
		    @Override
		    public IOrderedAssignmentsToMultiple transformEntry(DecisionMaker key,
			    IOrderedAssignmentsToMultiple value) {
			return getAssignments(key);
		    }
		});
	return Collections.unmodifiableMap(transformed);
    }

    @Override
    public IOrderedAssignmentsToMultiple getAssignments(DecisionMaker dm) {
	if (dm == null) {
	    throw new NullPointerException();
	}
	if (!getDms().contains(dm)) {
	    return null;
	}
	final IOrderedAssignmentsToMultiple assignments = lazyInitAssignments(dm);
	return new OrderedAssignmentsToMultipleWriteable(this, assignments);
    }

    /**
     * @param dm
     *            must be in this object.
     * @return not {@code null}.
     */
    private IOrderedAssignmentsToMultiple lazyInitAssignments(DecisionMaker dm) {
	if (!getDms().contains(dm)) {
	    throw new IllegalStateException("" + dm);
	}
	final IOrderedAssignmentsToMultiple assignments = m_allAssignments.get(dm);
	if (assignments != null) {
	    return assignments;
	}
	final IOrderedAssignmentsToMultiple created = AssignmentsFactory.newOrderedAssignmentsToMultiple();
	created.setCategories(getCatsAndProfs().getCategories());
	m_allAssignments.put(dm, created);
	return created;
    }

    @Override
    public boolean hasCompleteAssignments() {
	for (DecisionMaker dm : getDms()) {
	    final IOrderedAssignmentsToMultiple assignments = m_allAssignments.get(dm);
	    if (assignments.getAlternatives().size() < getAlternatives().size()) {
		return false;
	    }
	}
	return true;
    }

    private void beforeRemoveDm(DecisionMaker dm) {
	final IOrderedAssignmentsToMultiple assignments = m_allAssignments.get(dm);
	if (assignments != null) {
	    for (Alternative alternative : Sets.newHashSet(assignments.getAlternatives())) {
		assignments.setCategories(alternative, null);
	    }
	    m_allAssignments.remove(dm);
	}
    }

    private void beforeRemoveAlternative(Alternative alternative) {
	for (DecisionMaker dm : getDms()) {
	    if (m_allAssignments.get(dm) != null) {
		final IOrderedAssignmentsToMultiple assignments = m_allAssignments.get(dm);
		assignments.setCategories(alternative, null);
	    }
	}
    }

    @Override
    public ISortingResultsToMultiple getResults(DecisionMaker dm) {
	Preconditions.checkNotNull(dm);
	if (!getDms().contains(dm)) {
	    return null;
	}
	return new SortingResultsToMultipleViewGroupBacked(this, dm);
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return new AbstractSetView<Alternative>(delegate().getAlternatives()) {
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
    public Set<DecisionMaker> getDms() {
	return new AbstractSetView<DecisionMaker>(delegate().getDms()) {
	    @Override
	    public void beforeRemove(Object object) {
		if (object instanceof DecisionMaker) {
		    DecisionMaker dm = (DecisionMaker) object;
		    beforeRemoveDm(dm);
		}
	    }
	};
    }

    @Override
    public CatsAndProfs getCatsAndProfs() {
	return new CatsAndProfsForwarder(delegate().getCatsAndProfs()) {

	    @Override
	    public void setCategory(String oldName, Category newCategory) {
		delegate().setCategory(oldName, newCategory);
		final Category oldCategory = new Category(oldName);
		renameInAllAssignments(oldCategory, newCategory);
	    }

	    /**
	     * @param oldCategory
	     *            may exist or not (if not, nothing is done).
	     * @param newCategory
	     *            must not exist.
	     */
	    private void renameInAllAssignments(final Category oldCategory, Category newCategory) {
		for (DecisionMaker dm : m_allAssignments.keySet()) {
		    final IOrderedAssignmentsToMultiple assignments = m_allAssignments.get(dm);
		    if (assignments.getCategories().contains(oldCategory)) {
			AssignmentsUtils.renameCategory(assignments, oldCategory, newCategory);
		    }
		}
	    }

	    @Override
	    public void setCategoryDown(Alternative profile, Category category) {
		final Category oldCategory = delegate().getCategoryDown(profile);
		delegate().setCategoryDown(profile, category);
		renameInAllAssignments(oldCategory, category);
	    }

	    @Override
	    public void setCategoryUp(Alternative profile, Category category) {
		final Category oldCategory = delegate().getCategoryDown(profile);
		delegate().setCategoryUp(profile, category);
		renameInAllAssignments(oldCategory, category);
	    }

	    @Override
	    public boolean removeCategory(String name) {
		final boolean changed = delegate().removeCategory(name);
		if (!changed) {
		    return false;
		}
		final Category category = new Category(name);
		for (DecisionMaker dm : m_allAssignments.keySet()) {
		    final IOrderedAssignmentsToMultiple assignments = m_allAssignments.get(dm);
		    AssignmentsUtils.removeCategory(assignments, category);
		}
		return true;
	    }

	    @Override
	    public boolean clear() {
		final boolean changed = delegate().clear();
		if (!changed) {
		    return false;
		}
		for (DecisionMaker dm : m_allAssignments.keySet()) {
		    final IOrderedAssignmentsToMultiple assignments = m_allAssignments.get(dm);
		    assignments.clear();
		}
		return true;
	    }
	};
    }
}
