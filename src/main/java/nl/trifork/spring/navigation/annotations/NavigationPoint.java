package nl.trifork.spring.navigation.annotations;

import nl.trifork.spring.navigation.NavigationPointType;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * When used on a {@link org.springframework.stereotype.Controller} this annotation means all
 * {@link org.springframework.web.bind.annotation.RequestMapping} methods that have type
 * {@link org.springframework.web.bind.annotation.RequestMethod#GET} will become navigation points of the given type,
 * which means that the mapped requests will be picked up by the
 * {@link nl.trifork.spring.navigation.NavigationHandlerInterceptor}.
 * <p/>
 * Also specific methods may be annotated this way, allowing for different types of navigation points in the same
 * controller, or choosing only a sub-selection.
 *
 * @author Quinten Krijger
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RUNTIME)
@Inherited
@Documented
public @interface NavigationPoint {

    /**
     * The type of navigation point.
     *
     * @return the type of navigation point
     */
    NavigationPointType value();

}
