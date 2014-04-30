package nl.trifork.spring.navigation.testapp.controllers.annotated;

import nl.trifork.spring.navigation.NavigationPointType;
import nl.trifork.spring.navigation.annotations.NavigationPoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Quinten Krijger
 */
@Controller
@NavigationPoint(NavigationPointType.BASE)
@RequestMapping("base")
public class BasePageClassAnnotatedController {

    @RequestMapping
    public String show() {
        return "fake";
    }

}
