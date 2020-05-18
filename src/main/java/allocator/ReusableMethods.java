package allocator;
import static allocator.WebConnector.driver;
import static allocator.WebConnector.prop;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.GherkinKeyword;
import com.aventstack.extentreports.gherkin.model.Feature;

import io.cucumber.core.api.Scenario;

public class ReusableMethods {
	public static String randomProduct;
	public static String productDetails;
	public String RunTimeValue;
	String parentWindow;
	Set<String> allWindows;
	List<String> obtainedList1 = new ArrayList<>(); 
	List<String> obtainedList2= new ArrayList<>();


	public void saveScreenshotsForScenario(final Scenario scenario) {
		final byte[] screenshot = ((TakesScreenshot) driver)
				.getScreenshotAs(OutputType.BYTES);
		scenario.embed(screenshot, "image/png");
	}

	public void waitForPageLoad(int timeout){
		ExpectedConditions.jsReturnsValue("return document.readyState==\"complete\";");
	}

	public String getSpecificColumnData(String FilePath, String SheetName, String ColumnName) throws InvalidFormatException, IOException {
		FileInputStream fis = new FileInputStream(FilePath);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheet(SheetName);
		XSSFRow row = sheet.getRow(0);
		int col_num = -1;
		for(int i=0; i < row.getLastCellNum(); i++)
		{
			if(row.getCell(i).getStringCellValue().trim().equals(ColumnName))
				col_num = i;
		}
		row = sheet.getRow(1);
		XSSFCell cell = row.getCell(col_num);
		String value = cell.getStringCellValue();
		fis.close();
		System.out.println("Value of the Excel Cell is - "+ value);    	 
		return value;
	}

	public void setSpecificColumnData(String FilePath, String SheetName, String ColumnName, String CellValue, int RowNumber) throws IOException{
		FileInputStream fis=new FileInputStream(FilePath);
		FileOutputStream fos = null;
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheet(SheetName);
		XSSFRow row = null;
		XSSFCell cell = null;
		XSSFFont font = workbook.createFont();
		XSSFCellStyle style = workbook.createCellStyle();
		int col_Num = -1;
		row = sheet.getRow(0);
		for(int i = 0; i < row.getLastCellNum(); i++)
		{
			if(row.getCell(i).getStringCellValue().trim().equals(ColumnName))
			{
				col_Num = i;
			}
		}
		row = sheet.getRow(RowNumber);
		if(row == null)
			row = sheet.createRow(RowNumber);
		cell = row.getCell(col_Num);
		if(cell == null)
			cell = row.createCell(col_Num);
		font.setFontName("Comic Sans MS");
		font.setFontHeight(9.0);
		font.setBold(true);
		font.setColor(HSSFColor.WHITE.index);
		style.setFont(font);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		if(((CellValue.contains("+")) && (col_Num!=0))) {
			style.setFillForegroundColor(HSSFColor.GREEN.index);
			cell.setCellStyle(style);
			cell.setCellValue(CellValue);
		}
		else{
			style.setFillForegroundColor(HSSFColor.BLACK.index);
			cell.setCellStyle(style);
			cell.setCellValue(CellValue);	
		}
		fos = new FileOutputStream(FilePath);
		workbook.write(fos);
		fos.close();
	}
	public void generateRandomIndex() {
		Random random = new Random();
		ArrayList<String> ProductsList = new ArrayList<String>();
		String ProductsArr[] = prop.getProperty("ProductList").split(",");
		ProductsList.addAll(Arrays.asList(ProductsArr));
		int index = random.nextInt(ProductsList.size());
		randomProduct = ProductsList.get(index).trim();
	}
	public void reportandscreenshot(String AppElement, String ReportMessage, ExtentTest logInfo, String imagePath) throws IOException
	{
		if(waitForCondition("Presence", AppElement, 5))
		{
			logInfo.pass(ReportMessage +" is working as expected");
			logInfo.addScreenCaptureFromPath(imagePath);  
		}
		else
		{
			logInfo.fail(ReportMessage +" is not working as expected");
			logInfo.addScreenCaptureFromPath(imagePath);  
		}	
	}


	public void reportandscreenshot1(String AppElement, String ReportMessage, ExtentTest logInfo, String imagePath) throws IOException
	{
		if(waitForCondition("NotPresent", AppElement, 5))
		{
			logInfo.pass(ReportMessage +" is working as expected");
			logInfo.addScreenCaptureFromPath(imagePath);  
		}
		else
		{
			logInfo.fail(ReportMessage +" is not working as expected");
			logInfo.addScreenCaptureFromPath(imagePath);  
		}	
	}


	public void compareAndreport(String actual, String Expected, String ReportMessage, ExtentTest logInfo, String imagePath) throws IOException
	{
		if(actual.contains(Expected))
		{
			logInfo.pass(ReportMessage +" is matching");
			System.out.println(ReportMessage +" is matching");
			logInfo.addScreenCaptureFromPath(imagePath);  
		}
		else
		{
			logInfo.fail(ReportMessage +" is not matching");
			logInfo.addScreenCaptureFromPath(imagePath);  
		}

	}

	public By getElementWithLocator(String AppElement) throws Exception {
		String locatorTypeAndValue = prop.getProperty(AppElement);
		String[] locatorTypeAndValueArray = locatorTypeAndValue.split(",",2);
		String locatorType = locatorTypeAndValueArray[0].trim();
		String locatorValue = locatorTypeAndValueArray[1].trim();
		if(locatorValue.contains("ObjectToken")){
			locatorValue = locatorValue.replaceAll("ObjectToken", RunTimeValue);
		}
		switch (locatorType.toUpperCase()) {
		case "ID":
			return By.id(locatorValue);
		case "NAME":
			return By.name(locatorValue);
		case "XPATH":
			return By.xpath(locatorValue);
		case "CLASS":
			return By.className(locatorValue);
		default:
			return null;
		}
	}



	public WebElement FindAnElement(String AppElement) throws Exception{
		return (WebElement) driver.findElement(getElementWithLocator(AppElement));
	}

	public List<WebElement> FindElements(String AppElement) throws Exception{
		return (List<WebElement>) driver.findElements(getElementWithLocator(AppElement));
	}


	public String getTextFromElement(String AppElement) throws Exception{
		String elementText;
		if(FindAnElement(AppElement).getText()!=null){
			elementText=FindAnElement(AppElement).getText();
		}
		else{
			elementText=FindAnElement(AppElement).getAttribute("text").trim(); 
		}
		return elementText;
	}


	public String getDynamicTextFromElement(String AppElement, String Text) throws Exception{
		String elementText;
		RunTimeValue=Text;		
		if(FindAnElement(AppElement).getText()!=null){
			elementText=FindAnElement(AppElement).getText();
		}
		else{
			elementText=FindAnElement(AppElement).getAttribute("text").trim(); 
		}
		return elementText;
	}


	public int numberOfElements(String AppElement) throws Exception
	{
		List<WebElement> we=driver.findElements(getElementWithLocator(AppElement));
		return we.size();
	}

	public void PerformActionOnElement(String AppElement, String Action, String Text) throws Exception {
		try {
			RunTimeValue=Text;
			switch (Action.toLowerCase()) {
			case "click":
				FindAnElement(AppElement).click();
				break;
			case "clickwithactionclass":
				Actions actions = new Actions(driver);
				WebElement mainMenu = FindAnElement(AppElement);
				actions.moveToElement(mainMenu).click().build().perform();
				break;
			case "clickondropdownwithmouseover":
				Actions action = new Actions(driver);
				WebElement menu = FindAnElement(AppElement);
				Thread.sleep(2000);
				action.moveToElement(menu).moveToElement(FindAnElement(AppElement)).click().build().perform();
				break;
			case "sendkeys":
				FindAnElement(AppElement).sendKeys(Text);
				Thread.sleep(5000);
				break;
			case "enterkeys":
				FindAnElement(AppElement).sendKeys(Keys.ENTER);
				Thread.sleep(5000);
				break;
			case "clear":
				FindAnElement(AppElement).clear();
				break;
			case "gettext":
				FindAnElement(AppElement).getText();
				break;
			case "select":
				Select dropdown = new Select(FindAnElement(AppElement));
				dropdown.selectByValue(Text);
			case "selectbyindex":
				Select selectbyindexDropDown = new Select(FindAnElement(AppElement));
				selectbyindexDropDown.selectByIndex(Integer.parseInt(Text));
				break;
			case "waitforelementdisplay":
				waitForCondition("Presence",AppElement,60);
				break;
			case "waitforelementclickable":
				waitForCondition("Clickable",AppElement,60);
				break;
			case "elementnotdisplayed":
				waitForCondition("NotPresent",AppElement,60);
				break;
			default:
				throw new IllegalArgumentException("Action \"" + Action + "\" isn't supported.");
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	public String getMouseOverText(String AppElement ) throws Exception{
		Actions actions = new Actions(driver);
		WebElement mainMenu = FindAnElement(AppElement);
		actions.moveToElement(mainMenu);
		String MouseOverText = mainMenu.getAttribute("Title");
		return MouseOverText;
	}

	public boolean waitForCondition(String TypeOfWait, String AppElement, int Time){
		boolean conditionMeet = false;
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Time, TimeUnit.SECONDS).pollingEvery(5, TimeUnit.SECONDS).ignoring(Exception.class);
			switch (TypeOfWait)
			{
			case "Clickable":
				wait.until(ExpectedConditions.elementToBeClickable(FindAnElement(AppElement)));
				conditionMeet=true;
				break;
			case "Presence":
				wait.until(ExpectedConditions.presenceOfElementLocated(getElementWithLocator(AppElement)));
				conditionMeet=true;
				break;
			case "Visibility":
				wait.until(ExpectedConditions.visibilityOfElementLocated(getElementWithLocator(AppElement)));
				conditionMeet=true;
				break;
			case "NotPresent":
				wait.until(ExpectedConditions.invisibilityOfElementLocated(getElementWithLocator(AppElement)));
				conditionMeet=true;
				break;
			}
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("wait For Condition \"" + TypeOfWait + "\" isn't supported.");
			//System.out.println(AppElement+" Element is not displayed");
		}
		return conditionMeet;
	}
	public String getCssProps(String AppElement, String property) throws Exception 
	{
		String value = null;
		switch (property) {
		case "color":
			value = FindAnElement(AppElement).getCssValue("color");
			break;
		case "backgroundColor":
			value = FindAnElement(AppElement).getCssValue("background-color");
			break;
		case "size":
			value = FindAnElement(AppElement).getCssValue("font-size");
			break;
		case "alignment":
			value = FindAnElement(AppElement).getCssValue("text-align");
			break;
		case "name":
			value = FindAnElement(AppElement).getCssValue("font-family");
			break;
		default:
			break;
		}
		return value;
	}
	public String getTextOnRandomElement(String AppElement) throws Exception
	{
		Random random = new Random();
		int i = random.nextInt(15)+1;  
		RunTimeValue=i+"";
		String ProductName=getTextFromElement(AppElement);
		FindAnElement(AppElement).click();
		return ProductName;
	}

	public void scrollToView(String AppElement) throws Exception
	{
		WebElement element = FindAnElement(AppElement);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public void selectvalueFromDropdown(String AppElement, String Value) throws Exception
	{
		try {
			Select dropdown = new Select(FindAnElement(AppElement));
			dropdown.selectByValue(Value);
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

	public void SwitchToChildWindow()
	{
		parentWindow= driver.getWindowHandle();
		allWindows = driver.getWindowHandles();
		for(String curWindow : allWindows){
			driver.switchTo().window(curWindow);
		}	
	}
	public String getTitle()
	{
		String actualTitle = driver.getTitle();
		return actualTitle;
	}
	public String getUrl()
	{
		String url = driver.getCurrentUrl();
		return url;
	}

	public void closebrowser() {
		try
		{
			driver.close();
		}
		catch(Exception e)
		{
			System.out.println("Window doesn't exist");
		}
	}

	public void switchBackToParentWindow()
	{
		driver.switchTo().window(parentWindow);
	}
	public void switchToFrame(String AppElement) throws Exception
	{
		WebElement iFrame=FindAnElement(AppElement);
		driver.switchTo().frame(iFrame);

	}
	public void switchToDefaultFrame()
	{
		driver.switchTo().defaultContent();
	}
	public int stringToIntConversion(String Text)
	{
		int Value = Integer.parseInt(Text);
		return Value;
	}
	public boolean listOfWebElements(String AppElement) throws Exception
	{
		boolean flag=false;
		//First we need to initialize an List to store getText of all the elements as integer
		List<Integer> obtainedList = new ArrayList<>(); 
		//Initialize again another list to store all the web elements
		List<WebElement> elementList = FindElements(AppElement);
		//doing for each loop to add the getText of each the webelement into obtainedlist
		for(WebElement i:elementList){
			obtainedList.add(Integer.parseInt(i.getText().trim()));
		}
		//Now initializing another list to store sorted webelements
		ArrayList<Integer> sortedList = new ArrayList<>();  
		//doing for loop to add all the elements from obtained list into sorted list
		for(Integer j:obtainedList){
			sortedList.add(j);
		}
		//sortedList.stream().map(Integer::parseInt).collect(Collectors.toList());

		//now we are sorting the elements in sortedList 
		Collections.sort(sortedList);
		//Comapre two lists if the elements are already sorted via price low to high which we have selected from dropdown earlier than the below function will return true because the obtained list having the sorted element earlier and the sorted list we have sorted with java method both needs to match
		flag= obtainedList.equals(sortedList); 
		return flag;

	}
	public void listOfWebElementsInPair(String AppElement1, String AppElement2) throws Exception
	{
		List<WebElement> elementList = FindElements(AppElement1);
		//doing for each loop to add the getText of each the webelement into obtainedlist
		for(WebElement i:elementList){
			obtainedList1.add(i.getText().trim());
		}
		//Now initializing another list to store another row webelements
		List<WebElement> elementList2 = FindElements(AppElement2);
		for(WebElement j:elementList2){
			obtainedList2.add((j.getText().trim()));
		}

	}








	public boolean SortOfWebElementsAsString(String AppElement) throws Exception
	{
		boolean flag=false;
		//First we need to initialize an List to store getText of all the elements as integer
		List<String> obtainedList = new ArrayList<>(); 
		//Initialize again another list to store all the web elements
		List<WebElement> elementList = FindElements(AppElement);
		//doing for each loop to add the getText of each the webelement into obtainedlist
		for(WebElement i:elementList){
			obtainedList.add(i.getText().split("\\,")[3].trim());
		}
		for(int i=0;i<obtainedList.size();i++)
		{
			if((obtainedList.get(i).contains("about an hour ago") ||(obtainedList.get(i).contains("about a minute ago") )||(obtainedList.get(i).contains("minutes ago"))))
			{
				obtainedList.set(i, "about 1 hour ago");
			}
		}

		//Now initializing another list to store sorted webelements
		ArrayList<String> sortedList = new ArrayList<>();  
		//doing for loop to add all the elements from obtained list into sorted list
		for(String j:obtainedList){
			sortedList.add(j);
		}
		//sortedList.stream().map(Integer::parseInt).collect(Collectors.toList());

		//now we are sorting the elements in sortedList 
		Collections.sort(sortedList);
		//Comapre two lists if the elements are already sorted via price low to high which we have selected from dropdown earlier than the below function will return true because the obtained list having the sorted element earlier and the sorted list we have sorted with java method both needs to match
		flag= obtainedList.equals(sortedList); 
		return flag;
	}

	public boolean SortOfWebElementsInDecOrder(String AppElement) throws Exception
	{
		boolean flag=false;
		//First we need to initialize an List to store getText of all the elements as integer
		List<String> obtainedList = new ArrayList<>(); 
		//Initialize again another list to store all the web elements
		List<WebElement> elementList = FindElements(AppElement);
		//doing for each loop to add the getText of each the webelement into obtainedlist
		for(WebElement i:elementList){
			obtainedList.add(i.getText().split("\\,")[3].trim());
		}
		for(int i=0;i<obtainedList.size();i++)
		{
			if((obtainedList.get(i).contains("about an hour ago") ||(obtainedList.get(i).contains("about a minute ago") )||(obtainedList.get(i).contains("minutes ago"))))
			{
				obtainedList.set(i, "about 1 hour ago");
			}
		}

		//Now initializing another list to store sorted webelements
		ArrayList<String> sortedList = new ArrayList<>();  
		//doing for loop to add all the elements from obtained list into sorted list
		for(String j:obtainedList){
			sortedList.add(j);
		}
		//sortedList.stream().map(Integer::parseInt).collect(Collectors.toList());

		//now we are sorting the elements in sortedList 
		//Collections.sort(sortedList);
		sortedList.sort(Comparator.reverseOrder());
		//Comapre two lists if the elements are already sorted via price low to high which we have selected from dropdown earlier than the below function will return true because the obtained list having the sorted element earlier and the sorted list we have sorted with java method both needs to match
		flag= obtainedList.equals(sortedList); 
		return flag;
	}


	public boolean listOfWebelementAsString(String AppElement, String Text) throws Exception
	{
		RunTimeValue=Text;
		boolean flag= true;
		List<String> obtainedlist= new ArrayList<>();
		List<WebElement> elementlist= FindElements(AppElement);
		for(WebElement i:elementlist )
		{
			obtainedlist.add(i.getText());
			flag =obtainedlist.stream().anyMatch(text -> Text.equals(text));
			if(flag==false)
			{
				break;
			}
		}
		return flag;
	}
	public ArrayList<String> webElementList(String AppElement) throws Exception
	{
		ArrayList<String> obtainedlist= new ArrayList<>();
		List<WebElement> elementlist= FindElements(AppElement);
		for(WebElement i:elementlist )
		{
			obtainedlist.add(i.getText());
		}
		return obtainedlist;
	}
	public void simpleListwebelement(String AppElement) throws Exception
	{
		// if you have two elements or more elements with same properties you can use below logic 
		List<WebElement> element = FindElements(AppElement);
		WebElement Ad = element.get(0);
		Ad.click();

		//Or element.get(0).click();
	}

	public void verifyingPagination(String PageCountElement,String NextElement, ExtentTest logInfo, String  imagePath) throws Exception
	{
		//Find the text of Page Count Element
		String PageCount = getTextFromElement(PageCountElement); 
		//Convert into integer
		int NumberOfPages = Integer.parseInt(PageCount);
		//Uses this number of pages to loop through each page
		for(int i=0;i<NumberOfPages-1;i++)
			//On each page click on next button
		{
			//Page URL before navigation
			String CurrentUrl=getUrl();
			//Click on next button
			PerformActionOnElement(NextElement,"click","");
			//page URL after navigation
			String AfterclickUrl=getUrl();
			//And finally Verify the navigation to each page
			if(CurrentUrl.contains(AfterclickUrl))
			{
				logInfo.fail("Pagination is not working as expected for page:"+i);
				logInfo.addScreenCaptureFromPath(imagePath);  

			}
			else
			{
				logInfo.pass("Pagination is working as expected for page:"+i);
				logInfo.addScreenCaptureFromPath(imagePath);
			}
		}
	}

	public void totalNumOfLink(String AppElement, ExtentTest logInfo, String imagePath ) throws Exception
	{
		List<WebElement> resultdisplyed = FindElements(AppElement);
		int TotalNum = resultdisplyed.size();
		String TotalRsltCnt = "" + TotalNum;
		System.out.println("Total result displayed: " + TotalRsltCnt);
		compareAndreport(TotalRsltCnt, "10", "Total 10 results dispayed after click on 10", logInfo, imagePath);

	}
	public void refreshWebPage()
	{
		driver.navigate().refresh();

	}

	public ArrayList <String> createTickerExchangeString() {
		ArrayList <String> TickerExchange = new ArrayList<>();
		for(int i=0; i<obtainedList1.size();i++)
		{
			if(!obtainedList1.get(i).trim().contains("PNRL")) {
				TickerExchange.add(obtainedList2.get(i) +":"  +obtainedList1.get(i));
			}
		}
		return TickerExchange;
	}

}



















