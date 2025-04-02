package org.decisiondeck.jmcda.persist.utils;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import org.decision_deck.jmcda.structure.Alternative;
import org.decision_deck.jmcda.structure.Criterion;
import org.decision_deck.jmcda.structure.DecisionMaker;
import org.decision_deck.jmcda.structure.sorting.category.Category;
import org.decision_deck.utils.collection.extensional_order.ExtentionalTotalOrder;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class ExportSettings {

    public static class CategoryToName implements Function<Category, String> {
	@Override
	public String apply(Category input) {
	    return input.getId();
	}
    }

    public static class CriterionToId implements Function<Criterion, String> {
	@Override
	public String apply(Criterion input) {
	    return input.getId();
	}
    }

    private Set<Alternative> m_profilesOrder;
    /**
     * Never {@code null}.
     */
    private Function<Alternative, String> m_profilesToString;
    /**
     * Never {@code null}.
     */
    private Function<Criterion, String> m_criteriaToString;
    /**
     * Never {@code null}.
     */
    private Function<DecisionMaker, String> m_dmsToString;

    /**
     * @return not {@code null}.
     */
    public Function<DecisionMaker, String> getDmsToString() {
	return m_dmsToString;
    }

    public void setCriteriaToString(Function<Criterion, String> criteriaToString) {
	if (criteriaToString == null) {
	    m_criteriaToString = new Function<Criterion, String>() {
		@Override
		public String apply(Criterion input) {
		    return input.getId();
		}
	    };
	} else {
	    m_criteriaToString = criteriaToString;
	}
    }

    /**
     * @return {@code null} if no order set.
     */
    public Set<Alternative> getProfilesOrder() {
	return m_profilesOrder;
    }

    public ExportSettings() {
	m_numberFormatter = NumberFormat.getNumberInstance(Locale.ENGLISH);

	m_alternativesOrder = null;
	m_criteriaOrder = null;
	m_dmsOrder = null;
	m_profilesOrder = null;

	m_alternativesToString = Alternative.getIdFct();
	m_criteriaToString = new CriterionToId();
	m_categoriesToString = new CategoryToName();
	m_dmsToString = DecisionMaker.getIdFct();
	m_profilesToString = Alternative.getIdFct();
    }

    /**
     * @return not {@code null}.
     */
    public Function<Category, String> getCategoriesToString() {
	return m_categoriesToString;
    }

    /**
     * @return {@code null} if no order set.
     */
    public Set<Criterion> getCriteriaOrder() {
	return m_criteriaOrder;
    }

    private Set<Criterion> m_criteriaOrder;
    private Set<DecisionMaker> m_dmsOrder;
    /**
     * Never {@code null}.
     */
    private NumberFormat m_numberFormatter;
    /**
     * Never {@code null}.
     */
    private Function<Category, String> m_categoriesToString;
    private Set<Alternative> m_alternativesOrder;
    /**
     * Never {@code null}.
     */
    private Function<Alternative, String> m_alternativesToString;

    /**
     * @return {@code null} if no order set.
     */
    public Set<DecisionMaker> getDmsOrder() {
	return m_dmsOrder;
    }

    /**
     * @param dmsOrder
     *            {@code null} for no order set.
     */
    public void setDmsOrder(Collection<DecisionMaker> dmsOrder) {
	m_dmsOrder = dmsOrder == null ? null : ExtentionalTotalOrder.create(dmsOrder);
    }

    /**
     * @return not {@code null}.
     */
    public Function<Alternative, String> getProfilesToString() {
	return m_profilesToString;
    }

    public Set<Alternative> interOrderProfiles(Set<Alternative> profiles) {
	if (m_profilesOrder == null) {
	    return profiles;
	}
	return Sets.intersection(m_profilesOrder, profiles);
    }

    public Set<DecisionMaker> interOrderDms(Set<DecisionMaker> dms) {
	if (m_dmsOrder == null) {
	    return dms;
	}
	return Sets.intersection(m_dmsOrder, dms);
    }

    /**
     * @param profilesOrder
     *            {@code null} for no order set.
     */
    public void setProfilesOrder(Collection<Alternative> profilesOrder) {
	m_profilesOrder = profilesOrder == null ? null : ExtentionalTotalOrder.create(profilesOrder);
    }

    public void setProfilesToString(Function<Alternative, String> profilesToString) {
	if (profilesToString == null) {
	    m_profilesToString = new Function<Alternative, String>() {
		@Override
		public String apply(Alternative input) {
		    return input.getId();
		}
	    };
	} else {
	    m_profilesToString = profilesToString;
	}
    }

    public void setCategoriesToString(Function<Category, String> categoriesToString) {
	if (categoriesToString == null) {
	    m_categoriesToString = new Function<Category, String>() {
		@Override
		public String apply(Category input) {
		    return input.getId();
		}
	    };
	} else {
	    m_categoriesToString = categoriesToString;
	}
    }

    /**
     * @param criteriaOrder
     *            {@code null} for no order set.
     */
    public void setCriteriaOrder(Collection<Criterion> criteriaOrder) {
	m_criteriaOrder = criteriaOrder == null ? null : ExtentionalTotalOrder.create(criteriaOrder);
    }

    public Set<Criterion> interOrderCriteria(Set<Criterion> criteria) {
	if (m_criteriaOrder == null) {
	    return criteria;
	}
	return Sets.intersection(m_criteriaOrder, criteria);
    }

    /**
     * @return not {@code null}.
     */
    public NumberFormat getNumberFormatter() {
	return m_numberFormatter;
    }

    public String getNumberString(double number) {
	return m_numberFormatter.format(number);
    }

    public String getNumberString(int number) {
	return m_numberFormatter.format(number);
    }

    public void setNumberFormatter(NumberFormat numberFormatter) {
	if (numberFormatter == null) {
	    m_numberFormatter = NumberFormat.getNumberInstance(Locale.ENGLISH);
	} else {
	    m_numberFormatter = numberFormatter;
	}
    }

    /**
     * @return not {@code null}.
     */
    public Function<Criterion, String> getCriteriaToString() {
	return m_criteriaToString;
    }

    public void setDmsToString(Function<DecisionMaker, String> dmsToString) {
	if (dmsToString == null) {
	    m_dmsToString = new Function<DecisionMaker, String>() {
		@Override
		public String apply(DecisionMaker input) {
		    return input.getId();
		}
	    };
	} else {
	    m_dmsToString = dmsToString;
	}
    }

    /**
     * @return {@code null} if no order set.
     */
    public Set<Alternative> getAlternativesOrder() {
	return m_alternativesOrder;
    }

    /**
     * @return not {@code null}.
     */
    public Function<Alternative, String> getAlternativesToString() {
	return m_alternativesToString;
    }

    /**
     * @param alternative
     *            not {@code null}.
     * @return a string describing the given object, according to the function set in this exporter settings.
     */
    public String getAlternativeString(Alternative alternative) {
	Preconditions.checkNotNull(alternative);
	return m_alternativesToString.apply(alternative);
    }

    /**
     * @param profile
     *            not {@code null}.
     * @return a string describing the given object, according to the function set in this exporter settings.
     */
    public String getProfileString(Alternative profile) {
	Preconditions.checkNotNull(profile);
	return m_profilesToString.apply(profile);
    }

    /**
     * @param criterion
     *            not {@code null}.
     * @return a string describing the given object, according to the function set in this exporter settings.
     */
    public String getCriterionString(Criterion criterion) {
	Preconditions.checkNotNull(criterion);
	return m_criteriaToString.apply(criterion);
    }

    /**
     * @param category
     *            not {@code null}.
     * @return a string describing the given object, according to the function set in this exporter settings.
     */
    public String getCategoryString(Category category) {
	Preconditions.checkNotNull(category);
	return m_categoriesToString.apply(category);
    }

    /**
     * @param dm
     *            not {@code null}.
     * @return a string describing the given object, according to the function set in this exporter settings.
     */
    public String getDmString(DecisionMaker dm) {
	Preconditions.checkNotNull(dm);
	return m_dmsToString.apply(dm);
    }

    public Set<Alternative> interOrderAlternatives(Set<Alternative> alternatives) {
	if (m_alternativesOrder == null) {
	    return alternatives;
	}
	return Sets.intersection(m_alternativesOrder, alternatives);
    }

    /**
     * @param alternativesOrder
     *            {@code null} for no order set.
     */
    public void setAlternativesOrder(Collection<Alternative> alternativesOrder) {
	m_alternativesOrder = alternativesOrder == null ? null : ExtentionalTotalOrder.create(alternativesOrder);
    }

    public void setAlternativesToString(Function<Alternative, String> alternativesToString) {
	if (alternativesToString == null) {
	    m_alternativesToString = new Function<Alternative, String>() {
		@Override
		public String apply(Alternative input) {
		    return input.getId();
		}
	    };
	} else {
	    m_alternativesToString = alternativesToString;
	}
    }

}
