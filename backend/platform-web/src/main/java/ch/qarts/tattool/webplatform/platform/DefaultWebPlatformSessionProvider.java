package ch.qarts.tattool.webplatform.platform;

import ch.qarts.tattool.core.domain.session.SessionConfiguration;
import ch.qarts.tattool.webplatform.analysis.ContextAnalyzerServiceImpl;
import ch.qarts.tattool.webplatform.session.WebPlatformSession;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;
import java.util.UUID;

import static ch.qarts.tattool.webplatform.platform.WebPlatform.*;
import static ch.qarts.tattool.webplatform.util.DevtoolsUtils.getDevtools;

/**
 * Very basic implementation - not recommended for anything else that exploration / debugging work
 */
public class DefaultWebPlatformSessionProvider implements WebPlatformSessionProvider {
    @Override
    public WebPlatformSession provide(ProviderConfig providerConfiguration, SessionConfiguration sessionConfiguration) {
        assert providerConfiguration.getOptions() != null;
        ChromeOptions options = new ChromeOptions();
        options.addArguments(providerConfiguration.getOptions());
        WebDriverManager.chromedriver().setup();
        RemoteWebDriver webDriver = new ChromeDriver(options);
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT_IN_SECONDS));
        webDriver.manage().timeouts().setScriptTimeout(Duration.ofSeconds(SCRIPT_TIMEOUT_IN_SECONDS));
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_TIMEOUT_IN_SECONDS));
        ContextAnalyzerServiceImpl contextAnalyzerService = new ContextAnalyzerServiceImpl(getDevtools(webDriver));
        contextAnalyzerService.injectSpy(sessionConfiguration.isDebug());
        return WebPlatformSession.builder().id(UUID.randomUUID().toString()).
                webDriver(ThreadLocal.withInitial(() -> webDriver)).
                contextAnalyzerService(contextAnalyzerService).
                sessionConfiguration(sessionConfiguration).
                build();
    }
}
