package nl.trifork.spring.navigation;

import nl.trifork.spring.navigation.stack.NavigationStackEnricher;
import org.junit.Before;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Quinten Krijger
 */
public abstract class AbstractNavigationTest {

    private MockMvc mockMvc;
    private MockHttpSession session;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        this.session = new MockHttpSession();
        NavigationHandlerInterceptor navigationHandlerInterceptor = new NavigationHandlerInterceptor();

        List<NavigationalStateEnricher> enrichers = new ArrayList<>();
        enrichers.add(new NavigationStackEnricher());
        enrichers.addAll(additionalNavigationStateEnrichers());
        Field field = navigationHandlerInterceptor.getClass().getDeclaredField("enrichers");
        field.setAccessible(true);
        field.set(navigationHandlerInterceptor, enrichers);

        this.mockMvc = MockMvcBuilders
                       .standaloneSetup(getControllersUnderTest())
                       .addInterceptors(navigationHandlerInterceptor)
                       .build();
    }

    protected Collection<? extends NavigationalStateEnricher> additionalNavigationStateEnrichers() {
        return Collections.emptySet();
    }

    protected abstract Object[] getControllersUnderTest();

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public MockHttpSession getSession() {
        return session;
    }
}
