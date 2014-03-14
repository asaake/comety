package com.comety.servlet;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import br.eti.kinoshita.tap4j.consumer.TapConsumer;
import br.eti.kinoshita.tap4j.consumer.TapConsumerFactory;
import br.eti.kinoshita.tap4j.model.TestSet;

import com.google.common.base.Predicate;


public class CometyJsClientTest {

	private static final Log log = LogFactory.getLog(CometyJsClientTest.class);
	
	private static final String CHROME_DRIVER = "src/test/resources/chromedriver_win32/chromedriver.exe";
	
	private static final String TEST_TARGET = "src/test/resources/META-INF/resources/unit_test.html";
	
	private static ChromeDriverService service;
	
	private WebDriver driver;

	@BeforeClass
	public static void createAndStartService() throws IOException {
		service = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File(CHROME_DRIVER))
				.usingAnyFreePort()
				.build();
		service.start();
	}

	@AfterClass
	public static void createAndStopService() {
		service.stop();
	}

	@Before
	public void createDriver() {
		driver = new RemoteWebDriver(
				service.getUrl(),
				DesiredCapabilities.chrome()
		);
	}

	@After
	public void quitDriver() {
		driver.quit();
	}

	@Test
	public void Mochaの結果が成功していること() throws InterruptedException, JSONException {
		final WebDriverWait wait = new WebDriverWait(driver, 500, 100);
		final JavascriptExecutor js = (JavascriptExecutor) driver;
		
		File unitTestFile = new File(TEST_TARGET);
		driver.get(unitTestFile.getAbsolutePath());
		
		wait.until(new Predicate<WebDriver>() {
			@Override
			public boolean apply(WebDriver arg0) {
				return js.executeScript("return document.readyState;").equals("complete");
			}
		});
		
		
		wait.until(new Predicate<WebDriver>() {
			@Override
			public boolean apply(WebDriver arg0) {
				try {
					@SuppressWarnings("unchecked")
					Map<String, Object> response = (Map<String, Object>) js.executeScript("return mocha.tapResult");
					if (response.get("isEnd") == null) {
						return false;
					}
					return (boolean) response.get("isEnd");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		
		@SuppressWarnings("unchecked")
		Map<String, Object> response = (Map<String, Object>) js.executeScript("return mocha.tapResult");
		
		@SuppressWarnings("unchecked")
		List<String> logs = (List<String>) response.get("logs");
		String tapResult = StringUtils.join(logs, "\n");
		log.info(tapResult);
		
		TapConsumer consumer = TapConsumerFactory.makeTap13Consumer();
		TestSet testSet = consumer.load(tapResult);
		assertFalse(
				"Mochaのテスト結果が失敗でした。\n"
						+ TEST_TARGET + "を確認してください。\n"
						+ tapResult,
				testSet.containsNotOk()
		);
		
	}

}
