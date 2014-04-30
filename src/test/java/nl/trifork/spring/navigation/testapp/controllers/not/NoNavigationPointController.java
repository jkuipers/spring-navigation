package nl.trifork.spring.navigation.testapp.controllers.not;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller that is not a NavigationPoint at all.
 *
 * @author Quinten Krijger
 */
@Controller
@RequestMapping("/no_navigation_point")
public class NoNavigationPointController {

    @RequestMapping
    public String show() {
        return "fake";
    }

}
