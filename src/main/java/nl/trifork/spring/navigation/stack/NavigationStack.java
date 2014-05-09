package nl.trifork.spring.navigation.stack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Quinten Krijger
 */
public class NavigationStack {

    private List<String> navigationSteps = new ArrayList<>();

    public NavigationStack(String baseUri) {
        navigationSteps.add(baseUri);
    }

    public boolean equalsFormerRequestUri(String requestUri) {
        int navSize = navigationSteps.size();
        return navSize > 1 && requestUri.equals(navigationSteps.get(navSize - 2));
    }

    public boolean equalsLastRequestUri(String requestUri) {
        return requestUri.equals(navigationSteps.get(navigationSteps.size() - 1));
    }

    public void removeLastStep() {
        int navSize = navigationSteps.size();
        if (navSize > 1) {
            navigationSteps.remove(navSize);
        }
    }

    public void rebase(String requestUri) {
        navigationSteps = new ArrayList<>();
        navigationSteps.add(requestUri);
    }

    public void addStep(String requestUri) {
        navigationSteps.add(requestUri);
    }

    public String getLastNavigationPointUri() {
        return navigationSteps.get(navigationSteps.size() - 1);
    }

    public String getPreviousNavigationPointUri() {
        int navSize = navigationSteps.size();
        if (navSize > 1) {
            return navigationSteps.get(navSize - 2);
        } else {
            return getLastNavigationPointUri();
        }
    }

    public String getBaseNavigationPointUri() {
        return navigationSteps.get(0);
    }
}
