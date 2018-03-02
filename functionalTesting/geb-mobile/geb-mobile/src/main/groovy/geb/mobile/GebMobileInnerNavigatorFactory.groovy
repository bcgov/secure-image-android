package geb.mobile

import geb.Browser

import geb.mobile.android.AndroidUIAutomatorNonEmptyNavigator
import geb.mobile.ios.AppiumIosInstrumentationNonEmptyNavigator
import geb.navigator.EmptyNavigator
import geb.navigator.Navigator
import geb.navigator.factory.InnerNavigatorFactory
import groovy.util.logging.Slf4j
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import org.openqa.selenium.Platform
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.CapabilityType

/**
 *
 *
 * shelly notes:! here
 * This Factory decides, which NonEmptyNavigator will be created for the WebElements from the Driver
 * This is not always clear, cause
 *
 * AppiumDriver could case 3 different implementations:
 * Android:
 *  Native-Apps:
 *  APILevel > 16 --> AndroidUIAutomator
 *  APILevel < 16 --> InstrumentationFramework
 *
 *  Mobile Site:
 *      Instrumentation Framework
 *
 *  Hybrid Apps or Apps where you call the Camera or other stuff:
 *      both   AndroidUIAutomator and InstrumentationFramework
 *
 * IOS:
 *   IosInstrumentation
 *
 * Selendroid:
 *    InstrumentationFramework
 *
 * IosDriver:
 *   IosInstrumentation
 *
 *
 *
 * Created by gmueksch on 23.06.14.
 *
 */
@Slf4j
class GebMobileInnerNavigatorFactory implements InnerNavigatorFactory {

    private static String NAV_PREFIX = 'appium'

    static Map<String, Class> _defaultNavigators = [
                                                    android            : AndroidUIAutomatorNonEmptyNavigator,
                                                    firefox            : FirefoxDriver,
                                                    ios                : AppiumIosInstrumentationNonEmptyNavigator,
                                                    'appium:NATIVE_APP': AndroidUIAutomatorNonEmptyNavigator,
                                                    'appium:WEBVIEW_0' : ChromeDriver,
                                                    'appium:WEBVIEW_1' : ChromeDriver,
                                                    'appium:CHROMIUM'  : ChromeDriver,
                                                    'appium:CHROME'    : ChromeDriver
    ]


    Map<Browser, Class> _innerNavigators = [:]

    GebMobileNavigatorFactory navigatorFactory

    public GebMobileInnerNavigatorFactory(GebMobileNavigatorFactory navigatorFactory) {
        this.navigatorFactory = navigatorFactory
        String appPkg = navigatorFactory.browser.driver.capabilities.getCapability("appPackage")
        _defaultNavigators.put "$NAV_PREFIX:WEBVIEW_$appPkg".toString(), ChromeDriver
    }

    private Class figureCorrectInnerNavigator(browser) {
        def driver = browser.driver
        String ctx = driver.getContext()

        def clazz
        //Set class null, when a context change was done
        if (driver instanceof AppiumDriver && navigatorFactory.context) {
            if (navigatorFactory.context != ctx) {
                clazz = _defaultNavigators["$NAV_PREFIX:$ctx"]
            }
        }

        if (!clazz)
            clazz = _innerNavigators[browser]

        if (!clazz) {
            synchronized (_innerNavigators) {
                String browserName = driver.capabilities.getCapability(CapabilityType.BROWSER_NAME)
                String platformName = driver.capabilities.getCapability("platformName")
                Platform platform = driver.capabilities.getCapability(CapabilityType.PLATFORM)
                log.debug("trying to figure out correct Navigator for ${driver.getClass()} , $browserName, $platformName, ${platform.name()}")
                if (driver instanceof IOSDriver) {
                    clazz = AppiumIosInstrumentationNonEmptyNavigator
                    navigatorFactory.context = ctx
                } else if (driver instanceof AndroidDriver) {
                    navigatorFactory.context = ctx
                    clazz = _defaultNavigators["$NAV_PREFIX:$navigatorFactory.context"]

                } else {
                    clazz = _defaultNavigators[browserName.toLowerCase()]
                    if (!clazz) clazz = _defaultNavigators[platformName.toLowerCase()]
                }
                if (clazz) {
                    log.info("Using $clazz.name for ${driver.getClass()}, $browserName, $platformName, ${platform.name()}")
                    _innerNavigators[browser] = clazz
                } else {
                    throw new RuntimeException("Could not determine correct InnerNavigator for $browserName, $platformName, ${platform.name()}")
                }

            }
        }
        return clazz
    }

    /**
     * If {@code elements != null && elements.size() > 0} a {@link geb.navigator.NonEmptyNavigator is returned, otherwise {@link EmptyNavigator}.
     * @param browser The browse to associate with the navigator
     * @param elements The elements to back the navigator
     * @return The newly created navigator
     */
    Navigator createNavigator(Browser browser, List<WebElement> elements) {
        if (elements!=null && elements.size()==0) return new EmptyNavigator(browser)
        return figureCorrectInnerNavigator(browser).newInstance(browser, elements==null?Collections.emptyList():elements)
    }

}
