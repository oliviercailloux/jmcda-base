package org.decisiondeck.jmcda.structure.sorting.assignment.credibilities;

import java.util.Map;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decisiondeck.jmcda.structure.sorting.assignment.VersatileAssignments;
import org.decisiondeck.jmcda.structure.sorting.assignment.utils.AssignmentsUtils;

import com.google.common.base.Preconditions;

public class AssignmentsWithCredibilities implements IAssignmentsWithCredibilities {
    private final VersatileAssignments m_delegate;

    private AssignmentsWithCredibilities(VersatileAssignments delegate) {
	Preconditions.checkNotNull(delegate);
	m_delegate = delegate;
    }

    public AssignmentsWithCredibilities() {
	this(new VersatileAssignments());
    }

    /**
     * Copy-constructor by value. No reference is kept to the given object.
     * 
     * @param source
     *            not <code>null</code>.
     */
    public AssignmentsWithCredibilities(IAssignmentsWithCredibilitiesRead source) {
	this(new VersatileAssignments(source));
    }

    @Override
    public Set<Category> getCategories() {
	return m_delegate.getCategories();
    }

    @Override
    public Set<Alternative> getAlternatives(Category category) {
	return m_delegate.getAlternatives(category);
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return m_delegate.getAlternatives();
    }

    public boolean setCategories(Set<Category> categories) {
	return m_delegate.setCategories(categories);
    }

    protected VersatileAssignments delegate() {
	return m_delegate;
    }

    @Override
    public boolean clear() {
	return m_delegate.clear();
    }

    @Override
    public Map<Category, Double> getCredibilities(Alternative alternative) {
	return m_delegate.getCredibilities(alternative);
    }

    @Override
    public boolean setCredibilities(Alternative alternative, Map<Category, Double> credibilities) {
	return m_delegate.setCredibilities(alternative, credibilities);
    }

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof IAssignmentsWithCredibilitiesRead)) {
	    return false;
	}
	IAssignmentsWithCredibilitiesRead a2 = (IAssignmentsWithCredibilitiesRead) obj;
	return AssignmentsUtils.equivalentWithCredibilities(this, a2);
    }

    @Override
    public int hashCode() {
	return AssignmentsUtils.getEquivalenceRelationWithCredibilities().hash(this);
    }

    @Override
    public String toString() {
	return AssignmentsUtils.getShortDescription(this);
    }

    @Override
    public Set<Category> getCategories(Alternative alternative) {
	return m_delegate.getCategories(alternative);
    }
}