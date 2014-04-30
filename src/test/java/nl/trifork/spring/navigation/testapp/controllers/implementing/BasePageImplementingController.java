package nl.trifork.spring.navigation.testapp.controllers.implementing;

import nl.trifork.spring.navigation.NavigationPoint;
import nl.trifork.spring.navigation.NavigationPointType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Quinten Krijger
 */
@Controller
@RequestMapping("base")
public class BasePageImplementingController implements NavigationPoint {

    @Override
    public NavigationPointType getNavigationPointType() {
        return NavigationPointType.BASE;
    }

    @RequestMapping
    public String show() {
        return "fake";
    }

}
