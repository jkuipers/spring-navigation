package nl.trifork.spring.navigation.stack;

import java.util.ArrayList;
import java.util.List;

/**
 * The stack of all navigation points accessed, maintained by the {@link NavigationStackEnricher}.
 *
 * @author Quinten Krijger
 */
class NavigationStack {

    private List<String> navigationSteps = new ArrayList<>();

    /**
     * Creates a new {@link NavigationStack} and initializes it with the given uri as a first 'visited' navigation
     * point.
     *
     * @param baseUri the initial uri in the {@link NavigationStack}
     */
    public NavigationStack(String baseUri) {
        navigationSteps.add(baseUri);
    }

    /**
     * Check whether given request uri equals the previous navigation points uri. Returns {@literal false} in case such
     * no previous request uri exists (which is the case if this {@link NavigationStack} has been rebased, see {@link
     * NavigationStack#rebase(String)}).
     *
     * @param uri the uri to check against
     * @return whether the supplied uri equals that of the previous navigation point
     */
    public boolean equalsFormerRequestUri(String uri) {
        int navSize = navigationSteps.size();
        return navSize > 1 && uri.equals(navigationSteps.get(navSize - 2));
    }

    /**
     * Check whether given request uri equals the last navigation points uri.
     *
     * @param uri the uri to check against
     * @return whether the supplied uri equals that of the last navigation point
     */
    public boolean equalsLastRequestUri(String uri) {
        return uri.equals(navigationSteps.get(navigationSteps.size() - 1));
    }

    /**
     * Removes the last navigation point from the stack. Can be used e.g. in a 'back-action'.
     */
    public void removeLastStep() {
        int navSize = navigationSteps.size();
        if (navSize > 1) {
            navigationSteps.remove(navSize);
        }
    }

    /**
     * Resets this entire {@link NavigationStack} to contain only the newly supplied request uri.
     *
     * @param uri the uri that will the the new sole entry in the {@link NavigationStack}
     */
    public void rebase(String uri) {
        navigationSteps = new ArrayList<>();
        navigationSteps.add(uri);
    }

    /**
     * Add the supplied uri to the {@link NavigationStack}.
     *
     * @param uri the uri to add to the {@link NavigationStack}
     */
    public void addStep(String uri) {
        navigationSteps.add(uri);
    }

    /**
     * Get the uri of the last navigation point from this {@link NavigationStack}.
     *
     * @return the uri of the last navigation point from this {@link NavigationStack}
     */
    public String getLastNavigationPointUri() {
        return navigationSteps.get(navigationSteps.size() - 1);
    }

    /**
     * Get the uri of the one to last navigation point from this {@link NavigationStack} or the last in case only a
     * single one exists.
     *
     * @return the uri of the one to last navigation point from this {@link NavigationStack} or the last in case only a
     * single one exists
     */
    public String getPreviousNavigationPointUri() {
        int navSize = navigationSteps.size();
        if (navSize > 1) {
            return navigationSteps.get(navSize - 2);
        } else {
            return getLastNavigationPointUri();
        }
    }

    /**
     * Get the uri of the first navigation point from this {@link NavigationStack}.
     *
     * @return the uri of the first navigation point from this {@link NavigationStack}
     */
    public String getBaseNavigationPointUri() {
        return navigationSteps.get(0);
    }
}
