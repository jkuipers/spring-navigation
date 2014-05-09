package nl.trifork.spring.navigation.stack;

import nl.trifork.spring.navigation.SimpleNavigationalStateEnricher;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Quinten Krijger
 */
@Component
public class NavigationStackEnricher extends SimpleNavigationalStateEnricher<NavigationStack> {

    private final String defaultBaseUri;

    /**
     * Constructs a new {@link NavigationStackEnricher} and sets the {@code defaultBaseUri} to "/".
     *
     * @see NavigationStackEnricher#NavigationStackEnricher(String)
     */
    public NavigationStackEnricher() {
        this("/");
    }

    /**
     * Constructs a new {@link NavigationStackEnricher} with the supplied {@code defaultBaseUri}.
     *
     * @param defaultBaseUri the default base uri, which is used in case no registered base uri has yet been hit in the
     *                       current session
     */
    public NavigationStackEnricher(String defaultBaseUri) {
        super("navigation", NavigationStack.class);
        this.defaultBaseUri = defaultBaseUri;
    }

    @Override
    public NavigationStack init() {
        return new NavigationStack(defaultBaseUri);
    }

    @Override
    public NavigationStack updateOnBasePageVisit(Object attribute, HttpServletRequest request) {
        NavigationStack navigationStack = assertNonNullNavigationStackAttribute(attribute);
        String requestUri = getRequestUriIncludingParams(request);
        navigationStack.rebase(requestUri);
        return navigationStack;
    }

    @Override
    public NavigationStack updateOnStepPageVisit(Object attribute, HttpServletRequest request) {
        NavigationStack navigationStack = assertNonNullNavigationStackAttribute(attribute);
        String requestUri = getRequestUriIncludingParams(request);

        boolean isBackAction = navigationStack.equalsFormerRequestUri(requestUri);
        boolean isRefreshAction = navigationStack.equalsLastRequestUri(requestUri);
        if (isBackAction) {
            navigationStack.removeLastStep();
        } else if (!isRefreshAction) {
            navigationStack.addStep(requestUri);
        }
        return navigationStack;
    }

    @Override
    public void postHandle(ModelMap modelMap, Object attribute) {
        NavigationStack navigationStack = assertNonNullNavigationStackAttribute(attribute);
        modelMap.addAttribute("navigationCurrent", navigationStack.getLastNavigationPointUri());
        modelMap.addAttribute("navigationBack", navigationStack.getPreviousNavigationPointUri());
        modelMap.addAttribute("navigationBase", navigationStack.getBaseNavigationPointUri());
    }

    /**
     * Find out whether the user stays on the same page, i.e. whether the request is for the same url as the last
     * navigation point the user visited.
     *
     * @param request current http request
     * @return whether the request is for the same url as the last page the user visited
     */
    public boolean stayingOnSamePage(HttpServletRequest request) {
        return retrieveNavigationStack(request).equalsFormerRequestUri(getRequestUriIncludingParams(request));
    }

    /**
     * Find out what the base (lowest level) url in the current user navigation is. The will be an url which is mapped
     * to a controller that is a {@link nl.trifork.spring.navigation.NavigationPointType#BASE}.
     *
     * @param request the http request (which includes the session, which includes the navigation)
     * @return the user navigations base url
     */
    public String retrieveCurrentBaseUrl(HttpServletRequest request) {
        return retrieveNavigationStack(request).getBaseNavigationPointUri();
    }

    /**
     * Find out what the previous navigation point url in the current user navigation is. Useful for going back in the
     * navigation stack
     *
     * @param request the http request (which includes the session, which includes the navigation)
     * @return the user navigations base url
     */
    public String retrievePreviousNavigationPointUrl(HttpServletRequest request) {
        return retrieveNavigationStack(request).getPreviousNavigationPointUri();
    }

    /**
     * Find out what the last navigation point url in the current user navigation is. The will be the current url in
     * most cases, but not if the current url is not mapped to a method or controller that is not a navigation point.
     *
     * @param request the http request (which includes the session, which includes the navigation)
     * @return the user navigations base url
     */
    public String retrieveLastNavigationPointUrl(HttpServletRequest request) {
        return retrieveNavigationStack(request).getLastNavigationPointUri();
    }

    /**
     * Reads the navigational state from the session.
     *
     * @param request the http request (which includes the session, which includes the navigation)
     * @return the users navigational state
     */
    private NavigationStack retrieveNavigationStack(HttpServletRequest request) {
        Object navigation = request.getSession().getAttribute("navigation");
        if (navigation == null) {
            return init();
        } else {
            return (NavigationStack) navigation;
        }
    }

    private String getRequestUriIncludingParams(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (StringUtils.hasText(request.getQueryString())) {
            requestUri += "?" + request.getQueryString();
        }
        return requestUri;
    }

    private NavigationStack assertNonNullNavigationStackAttribute(Object attribute) {
        if (attribute == null || !(attribute instanceof NavigationStack)) {
            throw new IllegalArgumentException("Expected non-null argument of type: "
                    + NavigationStack.class.getSimpleName());
        }
        return (NavigationStack) attribute;
    }
}
