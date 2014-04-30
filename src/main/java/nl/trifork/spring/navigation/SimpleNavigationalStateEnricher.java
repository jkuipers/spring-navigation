package nl.trifork.spring.navigation;

import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Quinten Krijger
 */
public abstract class SimpleNavigationalStateEnricher<T> implements NavigationalStateEnricher<T> {

    private final String attributeName;
    private final Class<T> domainClass;

    public SimpleNavigationalStateEnricher(String attributeName, Class<T> domainClass) {
        this.attributeName = attributeName;
        this.domainClass = domainClass;
    }

    @Override
    public String sessionAttributeName() {
        return attributeName;
    }

    @Override
    public T updateOnBasePageVisit(Object attribute, HttpServletRequest request) {
        try {
            domainClass.cast(attribute);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
        return init();
    }

    @Override
    public T updateOnStepPageVisit(Object attribute, HttpServletRequest request) {
        try {
            return domainClass.cast(attribute);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void postHandle(ModelMap modelMap, Object attribute) {
        T castAttribute;
        try {
            castAttribute = domainClass.cast(attribute);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
        modelMap.put(attributeName, castAttribute);
    }

}
