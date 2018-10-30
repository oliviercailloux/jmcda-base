package org.decision_deck.jmcda.structure.sorting.category.mess;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.sorting.category.Categories;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfs;
import org.decision_deck.jmcda.structure.sorting.category.CatsAndProfsForwarder;
import org.decision_deck.utils.IObserver;
import org.decision_deck.utils.ObservableTyped;

import com.google.common.base.Preconditions;

public class CatsAndProfsWithObserverPriorities extends CatsAndProfsForwarder implements CatsAndProfs {

    public CatsAndProfsWithObserverPriorities() {
	this(Categories.newCatsAndProfs());
    }

    /**
     * @param delegate
     *            must have no observers. This object has an unpredicted behavior with respect to notification of these
     *            existing observers.
     */
    public CatsAndProfsWithObserverPriorities(CatsAndProfs delegate) {
	super(delegate);
	delegate.addObserverAddedProfile(new IObserver<Alternative>() {
	    @Override
	    public void update(Alternative updated) {
		m_observableAddedProfilePrior.notifyObserversChanged(updated);
		m_observableAddedProfile.notifyObserversChanged(updated);
	    }
	});
	delegate.addObserverRemovedProfile(new IObserver<Alternative>() {
	    @Override
	    public void update(Alternative updated) {
		m_observableRemovedProfilePrior.notifyObserversChanged(updated);
		m_observableRemovedProfile.notifyObserversChanged(updated);
	    }
	});
    }

    private final ObservableTyped<Alternative> m_observableAddedProfilePrior = new ObservableTyped<Alternative>();
    private final ObservableTyped<Alternative> m_observableAddedProfile = new ObservableTyped<Alternative>();
    private final ObservableTyped<Alternative> m_observableRemovedProfile = new ObservableTyped<Alternative>();
    private final ObservableTyped<Alternative> m_observableRemovedProfilePrior = new ObservableTyped<Alternative>();

    public void addPriorityObserverAddedProfile(IObserver<Alternative> observer) {
	Preconditions.checkNotNull(observer);
	m_observableAddedProfilePrior.addObserver(observer);
    }

    public void addPriorityObserverRemovedProfile(IObserver<Alternative> observer) {
	Preconditions.checkNotNull(observer);
	m_observableRemovedProfilePrior.addObserver(observer);
    }

    @Override
    public void addObserverAddedProfile(IObserver<Alternative> observer) {
	Preconditions.checkNotNull(observer);
	m_observableAddedProfile.addObserver(observer);
    }

    @Override
    public void addObserverRemovedProfile(IObserver<Alternative> observer) {
	Preconditions.checkNotNull(observer);
	m_observableRemovedProfile.addObserver(observer);
    }

}
