package nl.trifork.spring.navigation.annotations;

import nl.trifork.spring.navigation.NavigationPointType;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Quinten Krijger
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RUNTIME)
@Inherited
@Documented
public @interface NavigationPoint {

    NavigationPointType value();

}
