package nl.trifork.spring.navigation;

import nl.trifork.spring.navigation.testapp.controllers.not.NoNavigationPointController;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Quinten Krijger
 */
public class NoNavigationPointsTest extends AbstractNavigationTest {

    @Override
    protected Object[] getControllersUnderTest() {
        return new Object[] {
                new NoNavigationPointController()
        };
    }

    @Test
    public void once_the_navigation_handler_interceptor_has_been_registered_navigation_properties_are_set_on_each_get_request()
            throws Exception {
        getMockMvc().perform(get("/no_navigation_point").session(getSession()))
                .andExpect(model().attribute("navigationBack", "/"))
                .andExpect(model().attribute("navigationBase", "/"))
                .andExpect(status().isOk());
    }

    @Test
    public void the_navigation_handler_interceptor_can_be_registered_with_a_custom_default_base_uri() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(getControllersUnderTest())
                .addInterceptors(new NavigationHandlerInterceptor("/custom_base"))
                .build();
        mockMvc.perform(get("/no_navigation_point").session(getSession()))
                .andExpect(model().attribute("navigationBack", "/custom_base"))
                .andExpect(model().attribute("navigationBase", "/custom_base"))
                .andExpect(status().isOk());
    }

}
