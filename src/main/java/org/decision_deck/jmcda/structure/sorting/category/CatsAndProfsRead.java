package org.decision_deck.jmcda.structure.sorting.category;

import java.util.Iterator;

import org.decision_deck.jmcda.structure.Alternative;

import com.google.common.collect.Iterators;

class CatsAndProfsRead extends CatsAndProfsForwarder implements CatsAndProfs {
    /**
     * @param delegate
     *            not <code>null</code>.
     */
    public CatsAndProfsRead(CatsAndProfs delegate) {
	super(delegate);
    }

    @Override
    public Category addCategory(String name) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public Category addCategory(Category category) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public void addProfile(Alternative profile) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public void setCategory(String oldName, Category newCategory) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public void setCategoryDown(Alternative profile, Category category) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public void setCategoryUp(Alternative profile, Category category) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean setProfileDown(String categoryName, Alternative profile) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean setProfileUp(String categoryName, Alternative profile) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean removeProfile(Alternative profile) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean removeCategory(String name) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public boolean clear() {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }

    @Override
    public Iterator<CatOrProf> iterator() {
	return Iterators.unmodifiableIterator(super.iterator());
    }

    @Override
    public void addAll(Iterable<CatOrProf> source) {
	throw new UnsupportedOperationException("This object is a read-only view.");
    }
}
