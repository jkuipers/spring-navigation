package nl.trifork.spring.navigation;

/**
 * Denotes a controller to be a point of interest for the navigation of a user, enabling functionality such as a
 * logical back button.
 *
 * @author Quinten Krijger
 * @see nl.trifork.spring.navigation.annotations.NavigationPoint
 */
public interface NavigationPoint {

    NavigationPointType getNavigationPointType();

}
