package nl.trifork.spring.navigation.testapp.controllers.annotated;

import nl.trifork.spring.navigation.NavigationPointType;
import nl.trifork.spring.navigation.annotations.NavigationPoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Quinten Krijger
 */
@Controller
public class BaseAndStepPagesMethodAnnotatedController {

    @NavigationPoint(NavigationPointType.BASE)
    @RequestMapping
    public String showBasePage() {
        return "fake";
    }

    @NavigationPoint(NavigationPointType.STEP)
    @RequestMapping("base_2/step")
    public String showStepPage() {
        return "fake";
    }

    @NavigationPoint(NavigationPointType.STEP)
    @RequestMapping("base_2/step_2")
    public String showStep2Page() {
        return "fake";
    }

    @RequestMapping("no_navigation_point")
    public String showNeutralPage() {
        return "fake";
    }

}
