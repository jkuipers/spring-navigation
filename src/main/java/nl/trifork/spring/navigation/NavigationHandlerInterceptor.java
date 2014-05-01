package nl.trifork.spring.navigation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the navigation object on the users session.
 * <p/>
 * Only activates on {@link org.springframework.stereotype.Controller} that are a
 * {@link NavigationPoint} or {@link nl.trifork.spring.navigation.annotations.NavigationPoint}.
 * <p/>
 * Only takes GET requests into account.
 *
 * TODO move the navigation logic to a NavigationStackEnricher
 *
 * @author Quinten Krijger
 */
@Component
public class NavigationHandlerInterceptor extends HandlerInterceptorAdapter {

    public static final String NAVIGATION = "navigation";

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Autowired
    private List<NavigationalStateEnricher<?>> enrichers;

    private final String defaultBaseUri;

    /**
     * Constructs a new {@link nl.trifork.spring.navigation.NavigationHandlerInterceptor} and sets the
     * {@code defaultBaseUri} to "/".
     *
     * @see nl.trifork.spring.navigation.NavigationHandlerInterceptor#NavigationHandlerInterceptor(String)
     */
    public NavigationHandlerInterceptor() {
        this("/");
    }

    /**
     * Constructs a new {@link nl.trifork.spring.navigation.NavigationHandlerInterceptor} with the supplied
     * {@code defaultBaseUri}.
     *
     * @param defaultBaseUri the default base uri, which is used in case no registered base uri was hit in the current
     *                       session before
     */
    public NavigationHandlerInterceptor(String defaultBaseUri) {
        this.defaultBaseUri = defaultBaseUri;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation defines a default navigation object in case none exists.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (requestIsMappedToAController(handler) && isGetRequest(request)) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            HttpSession session = request.getSession();

            if (isNavigationPoint(handlerMethod)) {

                if (session.getAttribute(NAVIGATION) == null) {
                    setNavigation(session, constructNavigationWithBase(defaultBaseUri));
                }

                if (enrichers != null) {
                    for (NavigationalStateEnricher<?> enricher : enrichers) {
                        Object attribute = session.getAttribute(enricher.sessionAttributeName());
                        if (attribute == null) {
                            session.setAttribute(enricher.sessionAttributeName(), enricher.init());
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation extends the navigation object with the new navigation action. It also puts "navigationBack"
     * and "navigationBase" on the model.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        if (requestIsMappedToAController(handler) && isGetRequest(request)) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            HttpSession session = request.getSession();

            if (isNavigationPoint(handlerMethod)) {

                String requestUri = getRequestUriIncludingParams(request);
                switch (getNavigationPointType(handlerMethod)) {
                    case BASE:
                        setNavigation(session, constructNavigationWithBase(requestUri));
                        if (enrichers != null) {
                            for (NavigationalStateEnricher<?> enricher : enrichers) {
                                Object attribute = session.getAttribute(enricher.sessionAttributeName());
                                session.setAttribute(enricher.sessionAttributeName(),
                                        enricher.updateOnBasePageVisit(attribute, request));
                            }
                        }
                        break;
                    case STEP:
                        List<String> navigation = getNavigation(session);
                        int navSize = navigation.size();
                        boolean isBackAction = navSize > 1 && requestUri.equals(navigation.get(navSize - 2));
                        boolean isRefreshAction = requestUri.equals(navigation.get(navSize - 1));
                        if (isBackAction) {
                            getNavigation(session).remove(navSize - 1);
                        } else if (!isRefreshAction) {
                            getNavigation(session).add(requestUri);
                        }

                        if (enrichers != null) {
                            for (NavigationalStateEnricher<?> enricher : enrichers) {
                                Object attribute = session.getAttribute(enricher.sessionAttributeName());
                                session.setAttribute(enricher.sessionAttributeName(),
                                        enricher.updateOnStepPageVisit(attribute, request));
                            }
                        }
                        break;
                }
                List<String> navigation = getNavigation(request.getSession());
                int navSize = navigation.size();
                String navigationBack = (navSize > 1)
                        ? navigation.get(navSize - 2)
                        : navigation.get(navSize - 1); // same as navigation.get(0)
                if (modelAndView != null) {
                    modelAndView.addObject("navigationBack", navigationBack);
                    modelAndView.addObject("navigationBase", navigation.get(0));
                }

                if (enrichers != null) {
                    for (NavigationalStateEnricher<?> enricher : enrichers) {
                        if (modelAndView != null) {
                            Object attribute = session.getAttribute(enricher.sessionAttributeName());
                            enricher.postHandle(modelAndView.getModelMap(), attribute);
                        }
                    }
                }
            }
        }
    }

    private boolean isNavigationPoint(HandlerMethod handlerMethod) {
        return handlerMethod.getBean() instanceof NavigationPoint
                || handlerMethod.getMethod()
                    .isAnnotationPresent(nl.trifork.spring.navigation.annotations.NavigationPoint.class)
                || handlerMethod.getBeanType()
                    .isAnnotationPresent(nl.trifork.spring.navigation.annotations.NavigationPoint.class);
    }

    private NavigationPointType getNavigationPointType(HandlerMethod handlerMethod) {
        if (handlerMethod.getBean() instanceof NavigationPoint) {
            return ((NavigationPoint) handlerMethod.getBean()).getNavigationPointType();
        } else if (handlerMethod.getMethod()
                    .isAnnotationPresent(nl.trifork.spring.navigation.annotations.NavigationPoint.class)) {
            return handlerMethod.getMethod()
                    .getAnnotation(nl.trifork.spring.navigation.annotations.NavigationPoint.class).value();
        } else if (handlerMethod.getBeanType()
                    .isAnnotationPresent(nl.trifork.spring.navigation.annotations.NavigationPoint.class)) {
            return handlerMethod.getBeanType()
                    .getAnnotation(nl.trifork.spring.navigation.annotations.NavigationPoint.class).value();
        }
        throw new IllegalArgumentException("HandlerMethod not of type NavigationPoint");
    }

    private String getRequestUriIncludingParams(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (StringUtils.hasText(request.getQueryString())) {
            requestUri += "?" + request.getQueryString();
        }
        return requestUri;
    }

    private boolean isGetRequest(HttpServletRequest request) {
        return RequestMethod.valueOf(request.getMethod()) == RequestMethod.GET;
    }

    private void setNavigation(HttpSession session, List<String> navigation) {
        session.setAttribute(NAVIGATION, navigation);
    }

    /**
     * Get the navigation from the session. Creates a new navigation {@link java.util.Deque} with the default base uri
     * in case no navigation exists or it has size 0 or it is of incorrect type (corrupted somehow).
     *
     * @param session the user http session
     * @return the users navigational state
     */
    private List<String> getNavigation(HttpSession session) {
        Object attribute = session.getAttribute(NAVIGATION);
        if (attribute == null || !(attribute instanceof List)) {
            return constructNavigationWithBase(defaultBaseUri);
        }
        List<String> navigation = (List<String>) attribute;
        if (navigation.isEmpty()) {
            return constructNavigationWithBase(defaultBaseUri);
        } else {
            //noinspection unchecked
            return navigation;
        }
    }

    private List<String> constructNavigationWithBase(String baseUri) {
        List<String> navigationSteps = new ArrayList<>();
        navigationSteps.add(baseUri);
        return navigationSteps;
    }

    private boolean requestIsMappedToAController(Object handler) {
        return handler instanceof HandlerMethod;
    }

}
