package org.decisiondeck.xmcda_oo.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.decision_deck.jmcda.structure.internal.Modifier;
import org.decision_deck.jmcda.structure.internal.SharedDirector;
import org.decision_deck.jmcda.structure.internal.SharedDirector.CopyContents;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class SharedDirectorTest {
    private static class IsEmpty implements Predicate<Date> {
	public IsEmpty() {
	    /** Public constructor. */
	}

	@Override
	public boolean apply(Date input) {
	    return input.getTime() == 0;
	}
    }

    private static class Emptier implements Modifier<Date> {
	public Emptier() {
	    /** Public constructor. */
	}

	@Override
	public boolean modify(Date t) {
	    if (t.getTime() == 0) {
		return false;
	    }
	    t.setTime(0);
	    return true;
	}
    }

    private static class Copier implements CopyContents<Date> {
	public Copier() {
	    /** Public constructor. */
	}

	@Override
	public boolean copyContents(Date source, Date target) {
	    if (target.getTime() == source.getTime()) {
		return false;
	    }
	    target.setTime(source.getTime());
	    return true;
	}
    }

    private static class Factory implements Function<Date, Date> {
	public Factory() {
	    /** Public constructor. */
	}

	@Override
	public Date apply(Date input) {
	    if (input == null) {
		return new Date(0);
	    }
	    return new Date(input.getTime());
	}
    }

    @Test
    public void testUpdateShared() throws Exception {
	final SharedDirector<String, Date> director = new SharedDirector<String, Date>(new Factory(), new Copier(),
		new Emptier(), new IsEmpty());
	director.updateShared();
	final Date sharedDate = director.getShared();
	assertEquals(0, sharedDate.getTime());
	sharedDate.setTime(1);
	director.updateShared();
	assertEquals(1, sharedDate.getTime());

	assertNull(director.get("2000"));
	director.putShared("2000");

	final Date date2000 = director.get("2000");
	assertEquals(1, date2000.getTime());

	sharedDate.setTime(2000);
	director.updateShared();

	assertEquals(2000, date2000.getTime());
    }

    @Test
    public void testSet1Key() throws Exception {
	final SharedDirector<String, Date> director = new SharedDirector<String, Date>(new Factory(), new Copier(),
		new Emptier(), new IsEmpty());
	director.putEmpty("1");
	director.apply("1", new Modifier<Date>() {
	    @Override
	    public boolean modify(Date target) {
		target.setTime(1000);
		return true;
	    }
	}, true);
	assertEquals(1000, director.getShared().getTime());

	director.putEmpty("2");
	assertEquals(0, director.getShared().getTime());
    }
}
