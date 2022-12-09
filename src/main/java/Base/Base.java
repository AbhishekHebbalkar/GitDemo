package Base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.OutputType;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.google.common.io.Files;

import utils.ExtentReportManager;

public class Base {
	public static WebDriver driver;
	public static Properties prop;

	public ExtentReports report = ExtentReportManager.getReportInstance();
	public ExtentTest logger;

	@BeforeSuite
	public void driverSetup() {
		prop = new Properties();
		try {
			prop.load(new FileInputStream(System.getProperty("user.dir")+"/src/main/java/Config/Config.properties")); // Loading the properties
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (prop.getProperty("browserName").matches("chrome")) {
			System.setProperty("webdriver.chrome.driver", "\\C:\\Users\\shubh\\Downloads\\chromedriver_win32 (2)\\chromedriver.exe\\");
			driver = new ChromeDriver(); // Initializing the new chrome driver
		}
		if (prop.getProperty("browserName").matches("firefox")) {
			driver = new FirefoxDriver(); // Initializing the new firefox driver
		}
		driver.manage().window().maximize(); // To maximize the window
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS); // Waiting time to page the load completely
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); // Adding driver waits to timeouts

	}

	public void openUrl() // Method to open URL for smoke test
	{
		logger = report.createTest("Opening Url");
		try {
			driver.get(prop.getProperty("url"));

			reportPass("URL opened, URL is :" + prop.getProperty("url"));
		} catch (Exception e) {
			reportFail(e.getMessage());
		}
	}

	public void reportFail(String report) {
		logger.log(Status.FAIL, report);
		takeScreenShotOnFailure();
	}

	// Function to show the passed test cases in the report
	public void reportPass(String report) {
		logger.log(Status.PASS, report);
	}

	public void Screenshoot() throws IOException {
		try {
		TakesScreenshot capture = (TakesScreenshot) driver;
		File srcFile = capture.getScreenshotAs(OutputType.FILE);
		File destFile = new File(System.getProperty("user.dir") + "/Screenshot/PassedCases/Screenshot.png");
		FileUtils.copyFile(srcFile, destFile);
	}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// To take Screenshot when test gets failed
	int i=0;
	public void takeScreenShotOnFailure() {

		TakesScreenshot takeScreenshot = (TakesScreenshot) driver;
		File src=takeScreenshot.getScreenshotAs(OutputType.FILE);
		File dest = new File(System.getProperty("user.dir") + "/Screenshot/FailedCases/i.png");
		try {
			FileUtils.copyFile(src, dest);
			logger.addScreenCaptureFromPath(System.getProperty("user.dir") + "/Screenshot/FailedCases/i.png");
			i++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterSuite
	public void closeBrowser() // method to close the browser
	{
		driver.quit(); // To close the browser
		report.flush(); // To save the reports
		
	}
}
