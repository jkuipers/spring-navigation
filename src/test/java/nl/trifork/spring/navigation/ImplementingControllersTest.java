package nl.trifork.spring.navigation;

import nl.trifork.spring.navigation.testapp.controllers.implementing.BasePageImplementingController;
import nl.trifork.spring.navigation.testapp.controllers.implementing.StepPagesImplementingController;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Quinten Krijger
 */
public class ImplementingControllersTest extends AbstractNavigationTest {

    @Override
    protected Object[] getControllersUnderTest() {
        return new Object[] {
                new BasePageImplementingController(),
                new StepPagesImplementingController()
        };
    }

    @Test
    public void navigation_to_base_page() throws Exception {
        getMockMvc().perform(get("/base").session(getSession()))
                .andExpect(model().attribute("navigationCurrent", "/base"))
                .andExpect(model().attribute("navigationBack", "/base"))
                .andExpect(model().attribute("navigationBase", "/base"))
                .andExpect(status().isOk());
    }

    @Test
    public void navigation_to_step_page() throws Exception {
        getMockMvc().perform(get("/base/step/one").session(getSession()))
                .andExpect(model().attribute("navigationCurrent", "/base/step/one"))
                .andExpect(model().attribute("navigationBack", "/"))
                .andExpect(model().attribute("navigationBase", "/"))
                .andExpect(status().isOk());
    }

    @Test
    public void navigation_to_step_page_via_base_page() throws Exception {
        getMockMvc().perform(get("/base").session(getSession()));
        getMockMvc().perform(get("/base/step/one").session(getSession()))
                .andExpect(model().attribute("navigationCurrent", "/base/step/one"))
                .andExpect(model().attribute("navigationBack", "/base"))
                .andExpect(model().attribute("navigationBase", "/base"))
                .andExpect(status().isOk());
    }

    @Test
    public void navigation_to_step_page_via_base_page_and_different_step_page() throws Exception {
        getMockMvc().perform(get("/base").session(getSession()));
        getMockMvc().perform(get("/base/step/one").session(getSession()));
        getMockMvc().perform(get("/base/step/two").session(getSession()))
                .andExpect(model().attribute("navigationCurrent", "/base/step/two"))
                .andExpect(model().attribute("navigationBack", "/base/step/one"))
                .andExpect(model().attribute("navigationBase", "/base"))
                .andExpect(status().isOk());
    }

    @Test
    public void refreshing_does_not_influence_navigation_state() throws Exception {
        getMockMvc().perform(get("/base").session(getSession()));
        getMockMvc().perform(get("/base/step/one").session(getSession()));
        getMockMvc().perform(get("/base/step/one").session(getSession()))
                .andExpect(model().attribute("navigationCurrent", "/base/step/one"))
                .andExpect(model().attribute("navigationBack", "/base"))
                .andExpect(model().attribute("navigationBase", "/base"))
                .andExpect(status().isOk());
    }

    @Test
    public void visiting_a_base_page_overrides_navigation_state() throws Exception {
        getMockMvc().perform(get("/base").session(getSession()));
        getMockMvc().perform(get("/base/step/one").session(getSession()));
        getMockMvc().perform(get("/base").session(getSession()))
                .andExpect(model().attribute("navigationCurrent", "/base"))
                .andExpect(model().attribute("navigationBack", "/base"))
                .andExpect(model().attribute("navigationBase", "/base"))
                .andExpect(status().isOk());
        getMockMvc().perform(get("/base/step/one").session(getSession()));
        getMockMvc().perform(get("/base/step/two").session(getSession()))
                .andExpect(model().attribute("navigationCurrent", "/base/step/two"))
                .andExpect(model().attribute("navigationBack", "/base/step/one"))
                .andExpect(model().attribute("navigationBase", "/base"))
                .andExpect(status().isOk());
    }
}
