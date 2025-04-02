package org.decision_deck.jmcda.structure.sorting.category;

import java.util.Iterator;
import java.util.NavigableSet;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.utils.IObserver;

public class CatsAndProfsForwarder implements CatsAndProfs {
    @Override
    public String toString() {
	return m_delegate.toString();
    }

    final private CatsAndProfs m_delegate;

    @Override
    public NavigableSet<Category> getCategories() {
	return m_delegate.getCategories();
    }

    @Override
    public NavigableSet<Category> getCategoriesFromBest() {
	return m_delegate.getCategoriesFromBest();
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
    public Category getCategoryDown(Alternative profile) {
	return m_delegate.getCategoryDown(profile);
    }

    @Override
    public Category getCategoryUp(Alternative profile) {
	return m_delegate.getCategoryUp(profile);
    }

    @Override
    public Alternative getProfileDown(String categoryName) {
	return m_delegate.getProfileDown(categoryName);
    }

    @Override
    public NavigableSet<Alternative> getProfiles() {
	return m_delegate.getProfiles();
    }

    @Override
    public Alternative getProfileUp(String categoryName) {
	return m_delegate.getProfileUp(categoryName);
    }

    @Override
    public boolean isComplete() {
	return m_delegate.isComplete();
    }

    /**
     * @param delegate
     *            not {@code null}.
     */
    public CatsAndProfsForwarder(CatsAndProfs delegate) {
	if (delegate == null) {
	    throw new NullPointerException();
	}
	m_delegate = delegate;
    }

    @Override
    public Category addCategory(String name) {
	return m_delegate.addCategory(name);
    }

    @Override
    public Category addCategory(Category category) {
	return m_delegate.addCategory(category);
    }

    @Override
    public void addProfile(Alternative profile) {
	m_delegate.addProfile(profile);
    }

    @Override
    public void setCategory(String oldName, Category newCategory) {
	m_delegate.setCategory(oldName, newCategory);
    }

    @Override
    public void setCategoryDown(Alternative profile, Category category) {
	m_delegate.setCategoryDown(profile, category);
    }

    @Override
    public void setCategoryUp(Alternative profile, Category category) {
	m_delegate.setCategoryUp(profile, category);
    }

    @Override
    public boolean setProfileDown(String categoryName, Alternative profile) {
	return m_delegate.setProfileDown(categoryName, profile);
    }

    @Override
    public boolean setProfileUp(String categoryName, Alternative profile) {
	return m_delegate.setProfileUp(categoryName, profile);
    }

    @Override
    public boolean isEmpty() {
	return m_delegate.isEmpty();
    }

    @Override
    public Category getCategory(String categoryName) {
	return m_delegate.getCategory(categoryName);
    }

    @Override
    public boolean removeProfile(Alternative profile) {
	return m_delegate.removeProfile(profile);
    }

    @Override
    public boolean removeCategory(String name) {
	return m_delegate.removeCategory(name);
    }

    @Override
    public boolean clear() {
	return m_delegate.clear();
    }

    protected CatsAndProfs delegate() {
	return m_delegate;
    }

    @Override
    public void addObserverAddedProfile(IObserver<Alternative> observer) {
	m_delegate.addObserverAddedProfile(observer);
    }

    @Override
    public void addObserverRemovedProfile(IObserver<Alternative> observer) {
	m_delegate.addObserverRemovedProfile(observer);
    }

    @Override
    public void addAll(Iterable<CatOrProf> source) {
	m_delegate.addAll(source);
    }

    @Override
    public Iterator<CatOrProf> iterator() {
	return m_delegate.iterator();
    }
}
