package se.oyabun.proctor.handler.manager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;
import se.oyabun.proctor.persistence.ProctorRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProctorRouteHandlerManagerImplTest {

    private ProctorRouteHandlerManagerImpl proctorRouteHandlerManager;

    private static final String TEST_INPUT = "/test";

    private ProctorHandlerConfiguration firstMatching,secondMatching,nonMatching;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ProctorRepository proctorRepository;

    @Before
    public void init() {

        firstMatching = new MatchingConfiguration(0);
        secondMatching = new MatchingConfiguration(1);
        nonMatching = new NonMatchingConfiguration(0);

        proctorRouteHandlerManager =
                new ProctorRouteHandlerManagerImpl(applicationContext,
                                                   proctorRepository,
                                                   Collections.emptyList());

    }

    @Test
    public void getMatchingPropertiesFor() {



        when(proctorRepository.getConfigurations())
                .thenReturn(Stream.of(nonMatching))
                .thenReturn(Stream.of(nonMatching, secondMatching, firstMatching))
                .thenReturn(Stream.of(secondMatching))
                .thenReturn(Stream.of(firstMatching, secondMatching));

        Optional<ProctorHandlerConfiguration> optionalOrderedConfiguraton =
            proctorRouteHandlerManager.getMatchingPropertiesFor(TEST_INPUT);

        assertThat(optionalOrderedConfiguraton.isPresent(), is(false));

        optionalOrderedConfiguraton =
                proctorRouteHandlerManager.getMatchingPropertiesFor(TEST_INPUT);

        assertThat(optionalOrderedConfiguraton.isPresent(), is(true));
        assertThat(optionalOrderedConfiguraton.get(), is(firstMatching));

        optionalOrderedConfiguraton =
                proctorRouteHandlerManager.getMatchingPropertiesFor(TEST_INPUT);

        assertThat(optionalOrderedConfiguraton.isPresent(), is(true));
        assertThat(optionalOrderedConfiguraton.get(), is(secondMatching));

        optionalOrderedConfiguraton =
                proctorRouteHandlerManager.getMatchingPropertiesFor(TEST_INPUT);

        assertThat(optionalOrderedConfiguraton.isPresent(), is(true));
        assertThat(optionalOrderedConfiguraton.get(), is(firstMatching));

    }

    /**
     * Non matching configuration stub class
     */
    class NonMatchingConfiguration implements ProctorHandlerConfiguration {

        public static final String NON_MATCHING_CONFIGURATION_ID =
                "NON_MATCHING_CONFIGURATION_ID";

        private int priority;

        public NonMatchingConfiguration(final int priority) {

            this.priority = priority;

        }

        @Override
        public String getConfigurationID() {

            return NON_MATCHING_CONFIGURATION_ID;
        }

        @Override
        public int getPriority() {

            return priority;

        }

        @Override
        public String getHandlerType() {

            return "handlertype";
        }

        @Override
        public String getPattern() {

            return "not/matching/pattern";
        }

        @Override
        public Map<String, String> getProperties() {

            return new HashMap<>();
        }

        @Override
        public boolean isPersistent() {

            return false;

        }

        @Override
        public String toString() {

            return getConfigurationID()+"-"+priority;

        }

    }

    /**
     * Matching configuration stub class
     */
    class MatchingConfiguration implements ProctorHandlerConfiguration {

        public static final String MATCHING_CONFIGURATION_ID =
                "MATCHING_CONFIGURATION_ID";

        private int priority;

        public MatchingConfiguration(final int priority) {

            this.priority = priority;

        }

        @Override
        public String getConfigurationID() {

            return MATCHING_CONFIGURATION_ID;
        }

        @Override
        public int getPriority() {

            return priority;

        }

        @Override
        public String getHandlerType() {

            return "handlertype";
        }

        @Override
        public String getPattern() {

            return TEST_INPUT;
        }

        @Override
        public Map<String, String> getProperties() {

            return new HashMap<>();
        }

        @Override
        public boolean isPersistent() {

            return false;

        }

        @Override
        public String toString() {

            return getConfigurationID()+"-"+priority;

        }

    }

}
