package stepdefs;

import java.awt.List;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.GherkinKeyword;
import com.aventstack.extentreports.gherkin.model.Feature;
import com.aventstack.extentreports.gherkin.model.Scenario;

import allocator.ReusableMethods;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import listeners.ExtentReportListener;

public class TradingSharesInfo extends ExtentReportListener 
{ 
	ReusableMethods RM = new ReusableMethods();
	ExtentTest logInfo=null;
	@Given("^I navigate to foreign stocks website$")
	public void navigateToAmazonWebsite() throws ClassNotFoundException, IOException
	{
		try {
			test = extent.createTest(Feature.class, "Verify Trading Shares of US");                         
			test=test.createNode(Scenario.class, "Verify share information of US share market");                       
			logInfo=test.createNode(new GherkinKeyword("Given"), "Verify share trading");
			driver.get(prop.getProperty("AppURL"));
			RM.reportandscreenshot("HeadingOfthePage_TopForeignStocks", "User navigation to DailyMail Website", logInfo, captureScreenShot(driver));
		}
		catch(Exception e)
		{
			testStepHandle("FAIL",driver,logInfo,e);    

		}
	}
	@When("^I get the list of all the Ticker and Exchange$")
	public void storingKeyAndvalues() throws Exception
	{
		ArrayList<String> Percentage = new ArrayList<>();
		RM.listOfWebElementsInPair("TopForeignTicker_StocksWebsite", "TopForeignExchange_StockWebsite");
		ArrayList <String> Pairvalue=RM.createTickerExchangeString();
		driver.get(prop.getProperty("AppURL2"));
		for(int i=0;i<Pairvalue.size();i++)
		{
			RM.PerformActionOnElement("SearchBox_TradingViewWebsite", "sendkeys", Pairvalue.get(i));
			RM.PerformActionOnElement("SearchIcon_TradingViewWebsite", "click", "");
			Thread.sleep(10000);
			logInfo.info(RM.getTextFromElement("PercentageLongTitle_TradingViewwebsite") + "  -->  " + RM.getTextFromElement("Percentage_TradingViewWebsite"));
			RM.setSpecificColumnData("C:\\Users\\Raghwendra Sonu\\Desktop\\Trading_Information.xlsx", "TradingInfo","CompanyName",RM.getTextFromElement("PercentageLongTitle_TradingViewwebsite"),i+1);
			RM.setSpecificColumnData("C:\\Users\\Raghwendra Sonu\\Desktop\\Trading_Information.xlsx", "TradingInfo","DailyProfit",RM.getTextFromElement("Percentage_TradingViewWebsite"),i+1);
		}
	}
}

