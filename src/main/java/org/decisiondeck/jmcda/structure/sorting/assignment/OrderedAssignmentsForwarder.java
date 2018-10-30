package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;

import com.google.common.base.Preconditions;

public class OrderedAssignmentsForwarder implements IOrderedAssignments {
    @Override
    public String toString() {
	return m_delegate.toString();
    }

    private final IOrderedAssignments m_delegate;

    public OrderedAssignmentsForwarder(IOrderedAssignments delegate) {
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
    public Category getCategory(Alternative alternative) {
	return m_delegate.getCategory(alternative);
    }

    @Override
    public NavigableSet<Category> getCategories(Alternative alternative) {
	return m_delegate.getCategories(alternative);
    }

    @Override
    public boolean setCategory(Alternative alternative, Category category) {
	return m_delegate.setCategory(alternative, category);
    }

    @Override
    public boolean setCategories(SortedSet<Category> categories) {
	return m_delegate.setCategories(categories);
    }

    protected IOrderedAssignments delegate() {
	return m_delegate;
    }

    @Override
    public boolean clear() {
	return m_delegate.clear();
    }

    @Override
    public boolean equals(Object obj) {
	return m_delegate.equals(obj);
    }

    @Override
    public int hashCode() {
	return m_delegate.hashCode();
    }
}