package tests;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.github.bonigarcia.wdm.WebDriverManager;

public class EcommercePurchaseTest {
	
	    private WebDriver driver;
	    private ExtentReports extent;
	    private ExtentTest test;

	    @BeforeClass
	    public void setUp() {
	    	 ExtentSparkReporter sparkReporter = new ExtentSparkReporter("extentReport.html");
	         extent = new ExtentReports();
	         extent.attachReporter(sparkReporter);

	        String browser = System.getProperty("browser", "chrome");
	        if (browser.equalsIgnoreCase("firefox")) {
	            WebDriverManager.firefoxdriver().setup();
	            driver = new FirefoxDriver();
	        } else {
	            WebDriverManager.chromedriver().setup();
	            driver = new ChromeDriver();
	        }
	        driver.manage().window().maximize();
	        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	        driver.get("https://www.saucedemo.com/v1/");
	    }

	    @Test
	    public void testEcommercePurchase() throws InterruptedException {
	        test = extent.createTest("Ecommerce Purchase Test");

	        // Login
	        try {
	            driver.findElement(By.id("user-name")).sendKeys("standard_user");
	            driver.findElement(By.id("password")).sendKeys("secret_sauce");
	            driver.findElement(By.id("login-button")).click();
	            test.pass("Login successful");
	        } catch (NoSuchElementException e) {
	            test.fail("Login element not found: " + e.getMessage());
	            captureScreenshot();
	        } catch (TimeoutException e) {
	            test.fail("Login timed out: " + e.getMessage());
	            captureScreenshot();
	        }

	        // Search for a Product
	        try {
	            driver.findElement(By.id("search-box")).sendKeys("Sauce Labs Backpack");
	            driver.findElement(By.id("search-button")).click();
	            test.pass("Product searched successfully");
	        } catch (NoSuchElementException e) {
	            test.fail("Search input not found: " + e.getMessage());
	            captureScreenshot();
	        } catch (ElementNotInteractableException e) {
	            test.fail("Search button not clickable: " + e.getMessage());
	            captureScreenshot();
	        }

	        // Add to Cart
	        try {
	            driver.findElement(By.xpath("//button[contains(text(),'Add to cart')]")).click();
	            test.pass("Product added to cart");
	        } catch (StaleElementReferenceException e) {
	            test.fail("Cart icon became stale: " + e.getMessage());
	            captureScreenshot();
	        } catch (WebDriverException e) {
	            test.fail("WebDriver error while adding to cart: " + e.getMessage());
	            captureScreenshot();
	        }

	        // Checkout
	        try {
	            driver.findElement(By.id("shopping_cart_container")).click();
	            driver.findElement(By.id("checkout")).click();
	            driver.findElement(By.id("first-name")).sendKeys("John");
	            driver.findElement(By.id("last-name")).sendKeys("Doe");
	            driver.findElement(By.id("postal-code")).sendKeys("12345");
	            driver.findElement(By.id("continue")).click();
	            test.pass("Checkout successful");
	        } catch (NoSuchFrameException e) {
	            test.fail("Frame not found for checkout: " + e.getMessage());
	            captureScreenshot();
	        }
	    }

	    private void captureScreenshot() {
	        try {
	            String screenshotPath = "screenshot_" + System.currentTimeMillis() + ".png";
	            TakesScreenshot ts = (TakesScreenshot) driver;
	            File source = ts.getScreenshotAs(OutputType.FILE);
	            FileUtils.copyFile(source, new File(screenshotPath));
	            test.addScreenCaptureFromPath(screenshotPath);
	        } catch (IOException e) {
	            test.fail("Failed to capture screenshot: " + e.getMessage());
	        }
	    }

	    @AfterClass
	    public void tearDown() {
	        driver.quit();
	        extent.flush();
	    }
	}


