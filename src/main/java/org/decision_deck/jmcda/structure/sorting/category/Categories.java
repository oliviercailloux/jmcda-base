package org.decision_deck.jmcda.structure.sorting.category;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NavigableSet;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Iterables;

public class Categories {

    static public CatsAndProfs getReadView(CatsAndProfs delegate) {
	return new CatsAndProfsRead(delegate);
    }
    static public CatsAndProfs newCatsAndProfs() {
	return new CatsAndProfsImpl();
    }

    static public CatsAndProfs newCatsAndProfs(CatsAndProfs source) {
	return new CatsAndProfsImpl(source);
    }

    static public CatsAndProfs newCatsAndProfs(Set<Category> source) {
	return new CatsAndProfsImpl(source);
    }

    static public Equivalence<Category> getEquivalence() {
	return new Equivalence<Category>() {
	    @Override
	    public boolean doEquivalent(Category c1, Category c2) {
		if (!c1.getId().equals(c2.getId())) {
		    return false;
		}
		return true;
	    }

	    @Override
	    public int doHash(Category t) {
		return 97 + t.getId().hashCode();
	    }
	};
    }

    /**
     * @param category
     *            may be <code>null</code>.
     * @return a detailed string representation of the object; the string "null" if the given object is
     *         <code>null</code>.
     */
    static public String toStringDetailed(Category category) {
	if (category == null) {
	    return "null";
	}
	final ToStringHelper stringHelper = Objects.toStringHelper(category);
	stringHelper.addValue(category.getId());
	stringHelper.add("down", category.getProfileDown());
	stringHelper.add("up", category.getProfileUp());
	return stringHelper.toString();
    }

    /**
     * @param categories
     *            not <code>null</code>.
     * @return a string describing this set of categories as an interval.
     */
    static public String toIntervalString(NavigableSet<Category> categories) {
	final String interval;
	if (categories.size() == 0) {
	    interval = "[]";
	} else if (categories.size() == 1) {
	    interval = "[" + categories.first() + "]";
	} else {
	    interval = "[" + categories.first() + " to " + categories.last() + "]";
	}
	return interval;
    }

    static public CatsAndProfs newRenamed(CatsAndProfs source, Function<Category, Category> renameCategories,
	    Function<Alternative, Alternative> renameProfiles) {
	checkNotNull(source);
	checkNotNull(renameCategories);
	checkNotNull(renameProfiles);
	final CatsAndProfs newCats = newCatsAndProfs();
	final Function<Category, Category> renameCategories2 = renameCategories;
	final Function<Alternative, Alternative> renameProfiles2 = renameProfiles;
	final Function<CatOrProf,CatOrProf> renamer = new Function<CatOrProf,CatOrProf>() {
	    @Override
	    public CatOrProf apply(CatOrProf input) {
		if (input.hasCategory()) {
		    return new CatOrProf(renameCategories2.apply(input.getCategory()));
		}
		return new CatOrProf(renameProfiles2.apply(input.getProfile()));
	    }};
	newCats.addAll(Iterables.transform(source, renamer));
	return newCats;
    }
}
