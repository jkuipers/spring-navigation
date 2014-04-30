package nl.trifork.spring.navigation;

import org.junit.Before;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * @author Quinten Krijger
 */
public abstract class AbstractNavigationTest {

    private MockMvc mockMvc;
    private MockHttpSession session;

    @Before
    public void setUp() {
        this.session = new MockHttpSession();
        this.mockMvc = MockMvcBuilders
                       .standaloneSetup(getControllersUnderTest())
                       .addInterceptors(new NavigationHandlerInterceptor())
                       .build();
    }

    protected abstract Object[] getControllersUnderTest();

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public MockHttpSession getSession() {
        return session;
    }
}
