package org.decisiondeck.jmcda.structure.sorting.assignment;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NavigableSet;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;

import com.google.common.collect.Sets;

/**
 * A writeable view to an other {@link IOrderedAssignmentsToMultiple} that extends the set of assigned alternatives with
 * a given set of alternatives being constantly assigned to the whole set of categories contained in the delegate
 * object. The extension set may be a view to an other set.
 * 
 * @author Olivier Cailloux
 * 
 */
public class OrderedAssignmentsToMultipleExtended extends OrderedAssignmentsToMultipleForwarder implements
	IOrderedAssignmentsToMultiple {

    @Override
    public Set<Alternative> getAlternatives(Category category) {
	return Sets.union(m_extensionSet, delegate().getAlternatives(category));
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return Sets.union(m_extensionSet, delegate().getAlternatives());
    }

    @Override
    public NavigableSet<Category> getCategories(Alternative alternative) {
	final NavigableSet<Category> categories = delegate().getCategories(alternative);
	if (categories == null) {
	    return getCategories();
	}
	return categories;
    }

    private final Set<Alternative> m_extensionSet;

    public OrderedAssignmentsToMultipleExtended(IOrderedAssignmentsToMultiple delegate, Set<Alternative> extensionSet) {
	super(delegate);
	checkNotNull(extensionSet);
	m_extensionSet = extensionSet;
    }

}
