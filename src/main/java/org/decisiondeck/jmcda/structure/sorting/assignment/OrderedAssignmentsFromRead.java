package org.decisiondeck.jmcda.structure.sorting.assignment;

import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Category;

import com.google.common.base.Preconditions;

public class OrderedAssignmentsFromRead implements IOrderedAssignments {

    private final IOrderedAssignmentsRead m_delegate;

    public OrderedAssignmentsFromRead(IOrderedAssignmentsRead delegate) {
	Preconditions.checkNotNull(delegate);
	m_delegate = delegate;
    }

    @Override
    public boolean clear() {
	throw new UnsupportedOperationException();
    }

    protected IOrderedAssignmentsRead delegate() {
	return m_delegate;
    }

    @Override
    public boolean equals(Object obj) {
	return m_delegate.equals(obj);
    }

    @Override
    public Set<Alternative> getAlternatives() {
	return m_delegate.getAlternatives();
    }

    @Override
    public Set<Alternative> getAlternatives(Category category) {
	return m_delegate.getAlternatives(category);
    }

    @Override
    public NavigableSet<Category> getCategories() {
	return m_delegate.getCategories();
    }

    @Override
    public NavigableSet<Category> getCategories(Alternative alternative) {
	return m_delegate.getCategories(alternative);
    }

    @Override
    public Category getCategory(Alternative alternative) {
	return m_delegate.getCategory(alternative);
    }

    @Override
    public int hashCode() {
	return m_delegate.hashCode();
    }

    @Override
    public boolean setCategories(SortedSet<Category> categories) {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean setCategory(Alternative alternative, Category category) {
	throw new UnsupportedOperationException();
    }

}
