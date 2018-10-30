package org.decision_deck.jmcda.structure.internal;

import java.util.Map;

import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.internal.SharedDirector.CopyContents;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public abstract class AbstractSharedDecisionMakerPreferences<V> {

    protected final SharedDirector<DecisionMaker, V> m_director;
    private boolean m_keepShared;

    protected abstract boolean copyContents(V source, V target);

    public AbstractSharedDecisionMakerPreferences() {
	m_director = new SharedDirector<DecisionMaker, V>(new Function<V, V>() {
	    @Override
	    public V apply(V input) {
		if (input == null) {
		    return getNew();
		}
		return copyFrom(input);
	    }
	}, new CopyContents<V>() {
	    @Override
	    public boolean copyContents(V source, V target) {
		return AbstractSharedDecisionMakerPreferences.this.copyContents(source, target);
	    }
	}, new Modifier<V>() {
	    @Override
	    public boolean modify(V value) {
		if (isEmpty(value)) {
		    return false;
		}
		empty(value);
		return true;
	    }
	}, new Predicate<V>() {
	    @Override
	    public boolean apply(V input) {
		return isEmpty(input);
	    }
	});
	m_keepShared = false;
    }

    protected abstract void empty(V value);

    protected abstract V getNew();

    protected abstract V copyFrom(V source);

    protected abstract boolean isEmpty(V value);

    public void addDm(DecisionMaker dm) {
	if (m_keepShared) {
	    m_director.putShared(dm);
	} else {
	    m_director.putEmpty(dm);
	}
    }

    public boolean empty(DecisionMaker dm) {
	return m_director.putEmpty(dm);
    }

    public V get(DecisionMaker dm) {
	return m_director.get(dm);
    }

    public Map<DecisionMaker, V> getAll() {
	return m_director.getAll();
    }

    public V getShared() {
	return m_director.getShared();
    }

    public void remove(DecisionMaker dm) {
	m_director.remove(dm, m_keepShared);
    }

    public boolean isKeepShared() {
	return m_keepShared;
    }

    public void setKeepShared(boolean keepShared) {
	m_keepShared = keepShared;
    }
}
