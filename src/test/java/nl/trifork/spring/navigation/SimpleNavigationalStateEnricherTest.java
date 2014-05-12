package nl.trifork.spring.navigation;

import nl.trifork.spring.navigation.testapp.controllers.annotated.BaseAndStepPagesMethodAnnotatedController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

/**
 * @author Quinten Krijger
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleNavigationalStateEnricherTest extends AbstractNavigationTest {

    private final NavigationalStateEnricher<String> navigationalStateEnricher = new NavigationalStateEnricherImpl();

    @Mock
    private List<String> updatedValueReceiver;

    @InjectMocks
    private AlteringController alteringController = new AlteringController();

    @Override
    protected Object[] getControllersUnderTest() {
        return new Object[] {
                new BaseAndStepPagesMethodAnnotatedController(),
                alteringController
        };
    }

    @Override
    protected Collection<? extends NavigationalStateEnricher> additionalNavigationStateEnrichers() {
        return Arrays.asList(navigationalStateEnricher);
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
    public void a_controller_can_alter_the_sessions_attribute() throws Exception {
        getMockMvc().perform(get("/alter").session(getSession()))
                .andExpect(model().attribute("nav_attr", "altered in controller"));

        // where the update operation returns the altered value
        verify(updatedValueReceiver).add("altered in controller");
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

    private class AlteringController {

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        @Autowired
        private List<String> updatedValueReceiver;

        @RequestMapping("alter")
        public String alter(HttpSession session) {
            String updatedValue = navigationalStateEnricher.update(new SessionAttributeUpdater<String>() {
                @Override
                public String update(Object attributeToUpdate, Class<String> domainClass) {
                    try {
                        domainClass.cast(attributeToUpdate);
                    } catch (ClassCastException e) {
                        throw new IllegalArgumentException(e);
                    }
                    return "altered in controller";
                }
            }, session);
            updatedValueReceiver.add(updatedValue);
            return "fake";
        }
    }

}
