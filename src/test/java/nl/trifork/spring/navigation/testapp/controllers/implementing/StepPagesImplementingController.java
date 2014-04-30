package nl.trifork.spring.navigation.testapp.controllers.implementing;

import nl.trifork.spring.navigation.NavigationPoint;
import nl.trifork.spring.navigation.NavigationPointType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Quinten Krijger
 */
@Controller
@RequestMapping("base/step")
public class StepPagesImplementingController implements NavigationPoint {

    @Override
    public NavigationPointType getNavigationPointType() {
        return NavigationPointType.STEP;
    }

    @RequestMapping("one")
    public String showOne() {
        return "fake";
    }

    @RequestMapping("two")
    public String showTwo() {
        return "fake";
    }

}
