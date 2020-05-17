package runner;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(glue = {"stepdefs"}, plugin = {"html:target/cucumber-reports/cucumber-pretty","json:target/json-cucumber-reports/default/cukejson.json",
		"testng:target/testng-cucumber-reports/cuketestng.xml" }, features = {"src/test/resources/features/TradingSharesUSEveryday/01_TradingShares.feature"})
public class TestRunner extends AbstractTestNGCucumberTests {
	
	private static long duration;

	@BeforeClass
	public static void before() {
		duration = System.currentTimeMillis();
		System.out.println("Thread Id  | Scenario Num       | Step Count");
	}
	
	@AfterClass
	public static void after() {
		duration = System.currentTimeMillis() - duration;
		System.out.println("DURATION - "+ duration);
	}
}
