package org.decisiondeck.jmcda.structure.sorting.assignment.credibilities;

import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;

import com.google.common.base.Preconditions;

public class OrderedAssignmentsWithCredibilitiesForwarder implements IOrderedAssignmentsWithCredibilities {
    private final IOrderedAssignmentsWithCredibilities m_delegate;

    public OrderedAssignmentsWithCredibilitiesForwarder(IOrderedAssignmentsWithCredibilities delegate) {
	Preconditions.checkNotNull(delegate);
	m_delegate = delegate;
    }

    @Override
    public NavigableSet<Category> getCategories() {
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

    @Override
    public boolean setCategories(SortedSet<Category> categories) {
	return m_delegate.setCategories(categories);
    }

    protected IOrderedAssignmentsWithCredibilities delegate() {
	return m_delegate;
    }

    @Override
    public boolean clear() {
	return m_delegate.clear();
    }

    @Override
    public NavigableMap<Category, Double> getCredibilities(Alternative alternative) {
	return m_delegate.getCredibilities(alternative);
    }

    @Override
    public boolean setCredibilities(Alternative alternative, Map<Category, Double> credibilities) {
	return m_delegate.setCredibilities(alternative, credibilities);
    }

    @Override
    public boolean equals(Object obj) {
	return m_delegate.equals(obj);
    }

    @Override
    public int hashCode() {
	return m_delegate.hashCode();
    }

    @Override
    public String toString() {
	return m_delegate.toString();
    }

    @Override
    public NavigableSet<Category> getCategories(Alternative alternative) {
	return m_delegate.getCategories(alternative);
    }
}