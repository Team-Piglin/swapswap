package piglin.swapswap;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(initializers = TCIntegrationTest.IntegrationTestInitializer.class)
public class TCIntegrationTest {


    private static final DockerComposeContainer<?> DOCKER_COMPOSE =
            new DockerComposeContainer<>(new File("src/test/resources/docker_compose.yml"))
                    .withExposedService("mysql", 3306, Wait.forLogMessage(".*ready for connections.*", 1))
                    .withExposedService("redis", 6379, Wait.forLogMessage(".*Ready to accept connections.*", 1));

    @BeforeAll
    public static void setupContainers() {
        DOCKER_COMPOSE.start();
    }

    static class IntegrationTestInitializer implements
            ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
            Map<String, String> properties = new HashMap<>();

            setDatabaseProperties(properties);
            setRedisProperties(properties);

            TestPropertyValues.of(properties).applyTo(applicationContext);
        }

        private void setDatabaseProperties(Map<String, String> properties) {
            String rdbmsHost = DOCKER_COMPOSE.getServiceHost("mysql", 3306);
            int rdbmsPort = DOCKER_COMPOSE.getServicePort("mysql", 3306);
            properties.put("spring.datasource.url", "jdbc:mysql://" + rdbmsHost + ":" + rdbmsPort + "/swapswap");
            properties.put("spring.datasource.username", "root");
            properties.put("spring.datasource.password", "password");

        }

        private void setRedisProperties(Map<String, String> properties) {
            String redisHost = DOCKER_COMPOSE.getServiceHost("redis", 6379);
            Integer redisPort = DOCKER_COMPOSE.getServicePort("redis", 6379);
            properties.put("spring.data.redis.host", redisHost);
            properties.put("spring.data.redis.port", redisPort.toString());
        }
    }

}
