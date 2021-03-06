package nl.trifork.spring.navigation;

import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This class hooks into the {@link NavigationHandlerInterceptor} to enrich the session and model in a logical way with
 * respect to the users navigation.
 *
 * @author Quinten Krijger
 */
public interface NavigationalStateEnricher<T> {

    /**
     * Get the name with which the attribute is set on the user session.
     *
     * @return the name with which the attribute is set on the user session
     */
    String sessionAttributeName();

    /**
     * Creates a new object of type {@code T}.
     * <p/>
     * This method is called by {@link NavigationHandlerInterceptor} before visiting a
     * {@link org.springframework.stereotype.Controller} in case no attribute with given
     * {@link #sessionAttributeName()} exists yet.
     *
     * @return a new object of type {@code T}
     */
    T init();

    /**
     * Updates and returns the attribute object. Note that {@code attribute} should be of type {@code T}.
     * <p/>
     * This method is called by {@link NavigationHandlerInterceptor} when visiting a base navigation point request
     * mapping.
     *
     * @param attribute the object to mutate
     * @param request the request
     * @return updated object
     * @throws java.lang.IllegalArgumentException in case {@code attribute} is not {@code null} and not of type
     *         {@code T}
     */
    T updateOnBasePageVisit(Object attribute, HttpServletRequest request);

    /**
     * Updates and returns the attribute object. Note that {@code attribute} should be of type {@code T}.
     * <p/>
     * This method is called by {@link NavigationHandlerInterceptor} when visiting a step navigation point request
     * mapping.
     *
     * @param attribute the object to mutate
     * @param request the request
     * @return updated object
     * @throws java.lang.IllegalArgumentException in case {@code attribute} is not {@code null} and not of type
     *         {@code T}
     */
    T updateOnStepPageVisit(Object attribute, HttpServletRequest request);

    /**
     * This method is called by {@link NavigationHandlerInterceptor} when visiting a controller, but after
     * {@link #updateOnBasePageVisit(Object, HttpServletRequest)} or
     * {@link #updateOnStepPageVisit(Object, HttpServletRequest)}.
     *
     * @param modelMap the model map for the current request
     * @param attribute the session attributes current value
     * @throws java.lang.IllegalArgumentException in case {@code attribute} is not {@code null} and not of type
     *         {@code T}
     */
    void postHandle(ModelMap modelMap, Object attribute);

    /**
     * Provides an entry for a controller with access to the session to update the attribute object
     *
     * @param updater function that updates the attribute
     * @param session the user session containing the attribute
     * @return the updated attribute value
     */
    T update(SessionAttributeUpdater<T> updater, HttpSession session);

}
