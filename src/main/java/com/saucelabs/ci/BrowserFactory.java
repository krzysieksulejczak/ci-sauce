/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.saucelabs.ci;

import com.saucelabs.saucerest.SauceREST;
import com.saucelabs.saucerest.objects.Platform;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles invoking the Sauce REST API to retrieve the list of valid Browsers.  The list of browser is cached for
 * an hour.
 *
 * @author Ross Rowe
 */
public class BrowserFactory {

    private static final Logger logger = Logger.getLogger(BrowserFactory.class.getName());

    public static final int ONE_HOUR_IN_MILLIS = 1000 * 60 * 60;

    private SauceREST sauceREST;

    private Map<String, Browser> seleniumLookup = new HashMap<String, Browser>();
    private Map<String, Browser> appiumLookup = new HashMap<String, Browser>();
    private Map<String, Browser> webDriverLookup = new HashMap<String, Browser>();
    protected Timestamp lastLookup = null;
    private static final String IEHTA = "iehta";
    private static final String CHROME = "chrome";
    private static BrowserFactory instance;

    private enum OperatingSystemDescription {
        WINDOWS_10("Windows 2015", "Windows 10"),
        WINDOWS_8_1("Windows 2012 R2", "Windows 8.1"),
        WINDOWS_8("Windows 2012", "Windows 8"),
        WINDOWS_7("Windows 2008", "Windows 7"),
        WINDOWS_XP("Windows 2003", "Windows XP"),
        OSX_EL_CAPITAN("Mac 10.11", "OS X El Capitan"),
        OSX_YOSEMITE("Mac 10.10", "OS X Yosemite"),
        OSX_MAVERICKS("Mac 10.9", "OS X Mavericks"),
        OSX_MOUNTAIN_LION("Mac 10.8", "OS X Mountain Lion"),
        ;
        private final String key;
        private final String description;

        private static final Map<String,String> descriptionMap = new HashMap<String,String>();

        static
        {
            for (OperatingSystemDescription operatingSystemDescription : OperatingSystemDescription.values()) {
                descriptionMap.put(operatingSystemDescription.key, operatingSystemDescription.description);
            }
        }

        OperatingSystemDescription(String key, String description) {
            this.key = key;
            this.description = description;
        }


        public static String getDescription(String osName) {
            return descriptionMap.get(osName);
        }
    }

    public BrowserFactory() {
        this(null);
    }

    public BrowserFactory(SauceREST sauceREST) {
        if (sauceREST == null) {
            this.sauceREST = new SauceREST(null, null);
        } else {
            this.sauceREST = sauceREST;
        }
        try {
            initializeSeleniumBrowsers();
            initializeWebDriverBrowsers();
            initializeAppiumBrowsers();
        } catch (IOException e) {
            //TODO exception could mean we're behind firewall
            logger.log(Level.WARNING, "Error retrieving browsers, attempting to continue", e);
        }
    }

    public List<Browser> getSeleniumBrowsers() throws IOException {
        List<Browser> browsers;
        if (shouldRetrieveBrowsers()) {
            browsers = initializeSeleniumBrowsers();
        } else {
            browsers = new ArrayList<Browser>(seleniumLookup.values());
        }
        Collections.sort(browsers);

        return browsers;
    }

    public List<Browser> getAppiumBrowsers() throws IOException {
        List<Browser> browsers;
        if (shouldRetrieveBrowsers()) {
            browsers = initializeAppiumBrowsers();
        } else {
            browsers = new ArrayList<Browser>(appiumLookup.values());
        }
        Collections.sort(browsers);

        return browsers;
    }

    public List<Browser> getWebDriverBrowsers() throws IOException {
        List<Browser> browsers;
        if (shouldRetrieveBrowsers()) {
            browsers = initializeWebDriverBrowsers();
        } else {
            browsers = new ArrayList<Browser>(webDriverLookup.values());
        }
        Collections.sort(browsers);

        return browsers;
    }

    public boolean shouldRetrieveBrowsers() {
        return lastLookup == null || CacheTimeUtil.pastAcceptableDuration(lastLookup, ONE_HOUR_IN_MILLIS);
    }

    private List<Browser> initializeSeleniumBrowsers() throws IOException {
        return null;
/*        List<Browser> browsers = getSeleniumBrowsersFromSauceLabs();
        seleniumLookup = new HashMap<String, Browser>();
        for (Browser browser : browsers) {
            seleniumLookup.put(browser.getKey(), browser);
        }
        lastLookup = new Timestamp(new Date().getTime());
        return browsers;*/
    }

    private List<Browser> initializeAppiumBrowsers() throws IOException {
        return null;

        /*List<Browser> browsers = getAppiumBrowsersFromSauceLabs();
        appiumLookup = new HashMap<String, Browser>();
        for (Browser browser : browsers) {
            appiumLookup.put(browser.getKey(), browser);
        }
        lastLookup = new Timestamp(new Date().getTime());
        return browsers;*/
    }

    private List<Browser> initializeWebDriverBrowsers() throws IOException {
        return null;

        /*List<Browser> browsers = getWebDriverBrowsersFromSauceLabs();
        webDriverLookup = new HashMap<String, Browser>();
        for (Browser browser : browsers) {
            webDriverLookup.put(browser.getKey(), browser);
        }
        lastLookup = new Timestamp(new Date().getTime());
        return browsers;*/
    }

    private List<Platform> getWebDriverBrowsersFromSauceLabs() {
        return sauceREST.getSupportedPlatforms("webdriver");
    }

    private List<Platform> getAppiumBrowsersFromSauceLabs() throws IOException {
        return sauceREST.getSupportedPlatforms("appium");
    }

    public SauceFactory getSauceAPIFactory() {
        return new SauceFactory();
    }


    //iOS devices should include 'Simulator' in the device name (not currently included in the Sauce REST API response.
    // The platform should also be set to iOS (as per instructions at https://docs.saucelabs.com/reference/platforms-configurator
/* FIXME               if (device.equalsIgnoreCase("ipad") || device.equalsIgnoreCase("iphone")) {
                    device = device + " Simulator";
                    osName = "iOS";
                    //JENKINS-29047 set the browserName to 'Safari'
                    seleniumName = "Safari";
                }
                Browser browser = createBrowser(seleniumName, longName, longVersion, osName, device, deviceType, shortVersion, "portrait");
                browsers.add(browser);
                browser = createBrowser(seleniumName, longName, longVersion, osName, device, deviceType, shortVersion, "landscape");
                browsers.add(browser);



        return browsers;
    }*/

    /**
     * The Sauce REST API returns the server operating system name (eg. Windows 2003) rather than the public name
     * (eg. Windows XP), so if we've defined a mapping for the description, we use that here.
     * @param osName
     * @return
     */
    private String getOperatingSystemName(String osName) {
        String description = OperatingSystemDescription.getDescription(osName);
        if (description != null)
        {
            return description;
        }
        return osName;
    }

    private Browser createBrowser(String seleniumName, String longName, String longVersion, String osName, String device, String deviceType, String shortVersion, String orientation) {
        String browserKey = device + orientation + seleniumName + longVersion;
        //replace any spaces with _s
        browserKey = browserKey.replaceAll(" ", "_");
        //replace any . with _
        browserKey = browserKey.replaceAll("\\.", "_");
        StringBuilder label = new StringBuilder();
        label.append(longName).append(' ');
        if (deviceType != null) {
            label.append(deviceType).append(' ');
        }
        label.append(shortVersion);
        label.append(" (").append(orientation).append(')');
        //add browser for both landscape and portrait orientation
        Browser browser = new Browser(browserKey, osName, seleniumName, longName, shortVersion, longVersion, label.toString());
        browser.setDevice(device);
        browser.setDeviceType(deviceType);
        browser.setDeviceOrientation(orientation);
        return browser;
    }

    /**
     * Return the selenium rc browser which matches the key.
     *
     * @param key the key
     * @return the selenium rc browser which matches the key.
     */
    public Browser seleniumBrowserForKey(String key) {
        return seleniumLookup.get(key);
    }

    public Browser seleniumBrowserForKey(String key, boolean useLatestVersion) {
        Browser browser = webDriverBrowserForKey(key);
        if (useLatestVersion) {
            return getLatestSeleniumBrowserVersion(browser);
        } else {
            return browser;
        }
    }

    private Browser getLatestSeleniumBrowserVersion(Browser originalBrowser) {
        Browser candidateBrowser = originalBrowser;
        for (Browser browser : seleniumLookup.values()) {
            try {
                if (browser.getBrowserName().equals(originalBrowser.getBrowserName())
                        && browser.getOs().equals(originalBrowser.getOs())
                        && Integer.parseInt(browser.getLongVersion()) > Integer.parseInt(candidateBrowser.getLongVersion())) {
                    candidateBrowser = browser;
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
        return candidateBrowser;
    }

    /**
     * Return the web driver browser which matches the key.
     *
     * @param key the key
     * @return the web driver browser which matches the key.
     */
    public Browser webDriverBrowserForKey(String key) {
        return webDriverLookup.get(key);
    }

    public Browser webDriverBrowserForKey(String key, boolean useLatestVersion) {
        Browser browser = webDriverBrowserForKey(key);
        if (useLatestVersion) {
            return getLatestWebDriverBrowserVersion(browser);
        } else {
            return browser;
        }
    }

    private Browser getLatestWebDriverBrowserVersion(Browser originalBrowser) {
        Browser candidateBrowser = originalBrowser;
        for (Browser browser : webDriverLookup.values()) {

            try {
                if (browser.getBrowserName().equals(originalBrowser.getBrowserName())
                        && browser.getOs().equals(originalBrowser.getOs())
                        && Integer.parseInt(browser.getLongVersion()) > Integer.parseInt(candidateBrowser.getLongVersion())) {
                    candidateBrowser = browser;
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
        return candidateBrowser;
    }

    /**
     * Return the appium browser which matches the key.
     *
     * @param key the key
     * @return the appium browser which matches the key.
     */

    public Browser appiumBrowserForKey(String key) {
        return appiumLookup.get(key);
    }

    /**
     * Returns a singleton instance of SauceFactory.  This is required because
     * remote agents don't have the Bamboo component plugin available, so the Spring
     * auto-wiring doesn't work.
     *
     * @return the Browser Factory
     */
    public static BrowserFactory getInstance() {
        return getInstance(null);
    }

    public static BrowserFactory getInstance(SauceREST sauceREST) {
        if (instance == null) {
            instance = new BrowserFactory(sauceREST);
        }
        return instance;
    }

}
