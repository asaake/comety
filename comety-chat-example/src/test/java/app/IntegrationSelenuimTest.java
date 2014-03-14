package app;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@RunWith(Arquillian.class)
public class IntegrationSelenuimTest {

	private static final Log log = LogFactory.getLog(IntegrationSelenuimTest.class);
	
	private static final String CHROME_DRIVER = "src/test/resources/chromedriver_win32/chromedriver.exe";
	
	private static final String SERVER_HOST = "http://localhost:8080";
	
	private static final String CONTEXT_NAME = "comety-chat-example-test";
	
	private static final String TEST_TARGET = "integration-test.html";
	
	private static ChromeDriverService service;
	
	private WebDriver driver;

	@Deployment
	public static WebArchive createDeployment() {
		
		// POMの設定からWARを作成
		InputStream zipStream = ShrinkWrap
			.create(MavenImporter.class)
			.loadPomFromFile("pom.xml")
			.importBuildOutput()
			.as(ZipExporter.class)
			.exportAsInputStream();
		
		WebArchive archive = ShrinkWrap
				.create(ZipImporter.class, CONTEXT_NAME + ".war")
				.importFrom(zipStream)
				.as(WebArchive.class);
		
		// プロファイルの設定が未対応なので自分でPOMのとおりに設定する
		archive.addPackages(true, "app/inject");
		addResources(archive, new File("src/test/resources"));
		addWebResources(archive, new File("src/test/webapp"));
				
		log.debug(archive.toString(true));
		return archive;
	}
	
	public static void addResources(WebArchive archive, File dir) {
		@SuppressWarnings("unchecked")
		Collection<File> resources = FileUtils.listFiles(
				dir,
				TrueFileFilter.INSTANCE,
				FileFilterUtils.andFileFilter(
						TrueFileFilter.INSTANCE,
						TrueFileFilter.INSTANCE
				)
		);
		for (File resource : resources) {
			String target = resource.getAbsolutePath();
			target = target.replace(dir.getAbsolutePath(), "");
			target = target.replace("\\", "/");
			archive.addAsResource(resource, target);
		}
	}
	
	public static void addWebResources(WebArchive archive, File dir) {
		@SuppressWarnings("unchecked")
		Collection<File> resources = FileUtils.listFiles(
				dir,
				TrueFileFilter.INSTANCE,
				FileFilterUtils.andFileFilter(
						TrueFileFilter.INSTANCE,
						FileFilterUtils.notFileFilter(new WildcardFileFilter("WEB-INF"))
				)
		);
		for (File resource : resources) {
			String target = resource.getAbsolutePath();
			target = target.replace(dir.getAbsolutePath(), "");
			target = target.replace("\\", "/");
			archive.addAsWebResource(resource, target);
		}
	}
	
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
	@RunAsClient
	public void Mochaの結果が成功していること() throws InterruptedException, JSONException {
		final WebDriverWait wait = new WebDriverWait(driver, 500, 100);
		final JavascriptExecutor js = (JavascriptExecutor) driver;
		
		String url = SERVER_HOST + "/" + CONTEXT_NAME + "/" + TEST_TARGET;
		driver.get(url);
		
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
						+ url + "を確認してください。\n"
						+ tapResult,
				testSet.containsNotOk()
		);
		
	}
}
