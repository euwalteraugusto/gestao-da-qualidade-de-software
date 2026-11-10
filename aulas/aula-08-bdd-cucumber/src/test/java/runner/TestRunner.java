package runner;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test Runner do Cucumber na JUnit Platform (JUnit 5).
 * <p>
 * No slide o runner usa {@code @RunWith(Cucumber.class)} e {@code @CucumberOptions}, que sao do
 * JUnit 4 (cucumber-junit). Aqui o equivalente moderno e uma {@code @Suite} que seleciona o engine
 * "cucumber", a pasta de features no classpath ({@code src/test/resources/features}) e o pacote
 * de glue ({@code steps}).
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-reports/relatorio.html, json:target/cucumber-reports/relatorio.json")
public class TestRunner {
}
