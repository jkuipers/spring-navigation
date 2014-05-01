package nl.trifork.spring.navigation;

import nl.trifork.spring.navigation.testapp.controllers.annotated.BaseAndStepPagesMethodAnnotatedController;
import org.junit.Test;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

/**
 * @author Quinten Krijger
 */
public class SimpleNavigationalStateEnricherTest extends AbstractNavigationTest {

    @Override
    protected Object[] getControllersUnderTest() {
        return new Object[] {
                new BaseAndStepPagesMethodAnnotatedController()
        };
    }

    @Override
    protected Collection<? extends NavigationalStateEnricher> getNavigationStateEnrichers() {
        return Arrays.asList(new NavigationalStateEnricherImpl());
    }

    @Test
    public void an_enricher_creates_and_updates_a_session_attribute_which_can_be_put_on_the_model() throws Exception {
        getMockMvc().perform(get("/no_navigation_point").session(getSession()))
                .andExpect(model().attributeDoesNotExist("nav_attr"));

        getMockMvc().perform(get("/base_2").session(getSession()))
                .andExpect(model().attribute("nav_attr", "base page visited"));

        getMockMvc().perform(get("/base_2/step").session(getSession()))
                .andExpect(model().attribute("nav_attr", "step page visited"));
    }

    @Test
    public void an_ericher_can_enrich_the_model_based_on_the_request() throws Exception {
        getMockMvc().perform(get("/base_2").session(getSession()))
                .andExpect(model().attribute("nav_attr", "base page visited"));

        getMockMvc().perform(get("/base_2").session(getSession())
                .param("name", "Quinten"))
                .andExpect(model().attribute("nav_attr", "base page visited by Quinten"));
    }


    private static class NavigationalStateEnricherImpl extends SimpleNavigationalStateEnricher<String> {

        public NavigationalStateEnricherImpl() {
            super("nav_attr", String.class);
        }

        @Override
        public String init() {
            return "no navigation point visited yet";
        }

        @Override
        public String updateOnBasePageVisit(Object attribute, HttpServletRequest request) {
            String name = request.getParameter("name");
            if (StringUtils.hasText(name)) {
                return "base page visited by Quinten";
            }
            return "base page visited";
        }

        @Override
        public String updateOnStepPageVisit(Object attribute, HttpServletRequest request) {
            return "step page visited";
        }

    }

}
