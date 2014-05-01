package nl.trifork.spring.navigation;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Utility methods on the navigational state.
 * <p/>
 * The navigational state is managed in {@link NavigationHandlerInterceptor}.
 *
 * TODO integrate this into a NavigationStackEnricher
 *
 * @author Quinten Krijger
 */
public final class NavigationUtil {

    private NavigationUtil() {}

    /**
     * Find out whether the user stays on the same page, i.e. whether the request is for the same url as the last page
     * the user visited.
     *
     * @param request current http request
     * @return whether the request is for the same url as the last page the user visited
     */
    public static boolean stayingOnSamePage(HttpServletRequest request) {
        String lastNavigationPointUrl = retrieveLastNavigationPointUrl(request);
        return lastNavigationPointUrl.contains(request.getRequestURI());
    }

    /**
     * Find out what the base (lowest level) url in the current user navigation is. The will be an url which is mapped
     * to a controller that is a {@link NavigationPointType#BASE}.
     *
     * @param request the http request (which includes the session, which includes the navigation)
     * @return the user navigations base url
     */
    public static String retrieveCurrentBaseUrl(HttpServletRequest request) {
        List<String> navigation = retrieveNavigation(request);
        return navigation.get(0);
    }

    /**
     * Find out what the last navigation point url in the current user navigation is. The will be the current url in
     * most cases, but not if the current url is not mapped to a method or controller that is not a navigation point.
     *
     * @param request the http request (which includes the session, which includes the navigation)
     * @return the user navigations base url
     */
    public static String retrieveLastNavigationPointUrl(HttpServletRequest request) {
        List<String> navigation = retrieveNavigation(request);
        return navigation.get(navigation.size() - 1);
    }

    /**
     * Reads the navigational state from the session. The first entry in the Deque is the root of the navigation,
     * the last is the previous screen.
     *
     * @param request the http request (which includes the session, which includes the navigation)
     * @return the users navigational state
     */
    private static List<String> retrieveNavigation(HttpServletRequest request) {
        Object navigation = request.getSession().getAttribute("navigation");
        if (navigation == null) {
            return Arrays.asList("/"); // this is a quick fix. TODO is make the navigation logic an enricher which
            // includes this Util functionality
        } else {
            return (List) navigation;
        }
    }

}
