package nl.trifork.spring.navigation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Handles the navigation object on the users session.
 * <p/>
 * Only activates on {@link org.springframework.stereotype.Controller} that are a
 * {@link NavigationPoint} or {@link nl.trifork.spring.navigation.annotations.NavigationPoint}.
 * <p/>
 * Only takes GET requests into account.
 *
 * @author Quinten Krijger
 */
@Component
public class NavigationHandlerInterceptor extends HandlerInterceptorAdapter {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Autowired
    private List<NavigationalStateEnricher<?>> enrichers;

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

                switch (getNavigationPointType(handlerMethod)) {
                    case BASE:
                        if (enrichers != null) {
                            for (NavigationalStateEnricher<?> enricher : enrichers) {
                                Object attribute = session.getAttribute(enricher.sessionAttributeName());
                                session.setAttribute(enricher.sessionAttributeName(),
                                        enricher.updateOnBasePageVisit(attribute, request));
                            }
                        }
                        break;
                    case STEP:
                        if (enrichers != null) {
                            for (NavigationalStateEnricher<?> enricher : enrichers) {
                                Object attribute = session.getAttribute(enricher.sessionAttributeName());
                                session.setAttribute(enricher.sessionAttributeName(),
                                        enricher.updateOnStepPageVisit(attribute, request));
                            }
                        }
                        break;
                }
            }
            if (enrichers != null) {
                for (NavigationalStateEnricher<?> enricher : enrichers) {
                    Object attribute = session.getAttribute(enricher.sessionAttributeName());
                    if (modelAndView != null && attribute != null) {
                        enricher.postHandle(modelAndView.getModelMap(), attribute);
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

    private boolean isGetRequest(HttpServletRequest request) {
        return RequestMethod.valueOf(request.getMethod()) == RequestMethod.GET;
    }

    private boolean requestIsMappedToAController(Object handler) {
        return handler instanceof HandlerMethod;
    }

}
