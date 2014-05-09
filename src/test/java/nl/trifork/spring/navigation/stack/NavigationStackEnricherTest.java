package nl.trifork.spring.navigation.stack;

import nl.trifork.spring.navigation.AbstractNavigationTest;
import nl.trifork.spring.navigation.NavigationPointType;
import nl.trifork.spring.navigation.NavigationalStateEnricher;
import nl.trifork.spring.navigation.annotations.NavigationPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@RunWith(MockitoJUnitRunner.class)
public class NavigationStackEnricherTest extends AbstractNavigationTest {

    private final NavigationStackEnricher navigationStackEnricher = new NavigationStackEnricher();

    @Override
    protected Object[] getControllersUnderTest() {
        return new Object[]{
                new TestController(navigationStackEnricher)
        };
    }

    @Override
    protected Collection<? extends NavigationalStateEnricher> additionalNavigationStateEnrichers() {
        return Arrays.asList(navigationStackEnricher);
    }

    @Before
    public void init() throws Exception {
        getMockMvc().perform(get("/test/base").session(getSession()));
        getMockMvc().perform(get("/test/step").session(getSession()));
        getMockMvc().perform(get("/test/step_2").session(getSession()));
    }

    @Test
    public void a_controller_can_redirect_to_base() throws Exception {
        getMockMvc().perform(post("/test/to_base").session(getSession()))
            .andExpect(redirectedUrl("/test/base"));
    }

    @Test
    public void a_controller_can_redirect_to_previous() throws Exception {
        getMockMvc().perform(post("/test/to_previous").session(getSession()))
            .andExpect(redirectedUrl("/test/step"));
    }

    @Test
    public void a_controller_can_redirect_to_last() throws Exception {
        getMockMvc().perform(post("/test/to_last").session(getSession()))
            .andExpect(redirectedUrl("/test/step_2"));
    }

    @RequestMapping("/test")
    private static class TestController {

        private final NavigationStackEnricher navigationStackEnricher;

        public TestController(NavigationStackEnricher navigationStackEnricher) {
            this.navigationStackEnricher = navigationStackEnricher;
        }

        @RequestMapping("base")
        @NavigationPoint(NavigationPointType.BASE)
        public String showBase() {
            return "fake";
        }

        @RequestMapping("step")
        @NavigationPoint(NavigationPointType.STEP)
        public String showStep() {
            return "fake";
        }

        @RequestMapping("step_2")
        @NavigationPoint(NavigationPointType.STEP)
        public String showStep2() {
            return "fake";
        }

        @RequestMapping(value = "to_base", method = RequestMethod.POST)
        public String redirectToBase(HttpServletRequest request) {
            return "redirect:" + navigationStackEnricher.retrieveCurrentBaseUrl(request);
        }

        @RequestMapping(value = "to_previous", method = RequestMethod.POST)
        public String redirectToPrevious(HttpServletRequest request) {
            return "redirect:" + navigationStackEnricher.retrievePreviousNavigationPointUrl(request);
        }

        @RequestMapping(value = "to_last", method = RequestMethod.POST)
        public String redirectToCurrent(HttpServletRequest request) {
            return "redirect:" + navigationStackEnricher.retrieveLastNavigationPointUrl(request);
        }

    }

}