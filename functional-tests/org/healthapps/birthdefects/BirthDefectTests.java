package org.healthapps.birthdefects;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;

public class BirthDefectTests extends TestCase {

    private Selenium browser;
    private static final String APP_URL = System.getProperty("base.url");
    private static final String BROWSER_STRING = System.getProperty("browser.string");
    private static final String TIMEOUT = "10000";
    private static final String defectName = "adefect1";
    private static final String PERSON_NAME = "Baby2";
    private static final String LAT = "12.94";
    private static final String LON = "80.09";
    private static final String DOB = "2001-01-01";
    private static final String HAZARD_NAME = "leather factory";
    private static final String HAZARD_VERTICES = "12.96,79.7;12.83,79.15;12.95,79.31;12.96,79.7";
    private static final String HAZARD_BBOX = "12.96,79.31,12.83,79.7";
    private static final String INDUSTRY_CLASSIFICATION = "Soybean Farming";
    private static final String TEST_USERNAME = "seltest";
    private static final String TEST_USER_PWD = "testtest";

    public void setUp() {
        browser = new DefaultSelenium("localhost", 4444, BROWSER_STRING, APP_URL);
        browser.start();
    }

    public void testShouldAddABirthDefectType() {
        login();
        browser.click("link=Add defect");
        browser.type("birth_defect_name", defectName);
        browser.type("birth_defect_code", "743");
        browser.click("//input[@value='Add Defect']");
        browser.waitForCondition("selenium.isSomethingSelected('//select[@id=\"person_defects\"]')", TIMEOUT);
        assertTrue("could not add birth defect", arrayContains(browser.getSelectOptions("//select[@id=\"person_defects\"]"), defectName));
    }

    private void login() {
        browser.open("/index.htm");
        browser.type("username", TEST_USERNAME);
        browser.type("password", TEST_USER_PWD);
        browser.click("//input[@value='Login']");
        browser.waitForCondition("selenium.isElementPresent('//div[@id=\"person_menu\"]')", TIMEOUT);
    }

    public void testShouldAddAPersonWithBirthDefect() {
        login();
        browser.type("person_name", PERSON_NAME);
        browser.type("person_lat", LAT);
        browser.type("person_lon", LON);
        browser.type("person_dateOfBirth", DOB);
        browser.addSelection("person_defects", "label=" + defectName);
        browser.click("//input[@value='Save Person']");
        browser.waitForCondition("selenium.isElementPresent('//div[@id=\"add_person_errors\"]/font')", TIMEOUT);
        assertEquals("could not add a person", "Saved.", browser.getText("//div[@id=\"add_person_errors\"]/font"));
    }

    public void testShouldErrorWhenAddingAPersonWithSameName() {
        login();
        browser.type("person_name", PERSON_NAME);
        browser.type("person_lat", "13.94");
        browser.type("person_lon", "80.91");
        browser.type("person_dateOfBirth", "2004-01-01");
        browser.addSelection("person_defects", "label=" + defectName);
        browser.click("//input[@value='Save Person']");
        browser.waitForCondition("selenium.isElementPresent('//div[@id=\"add_person_errors\"]/font')", TIMEOUT);
        assertEquals("allowed adding person with the same name", "name exists.", browser.getText("//div[@id=\"add_person_errors\"]/font"));
    }

    public void testShouldReturnPersonAfterSearch() {
        login();
        browser.click("link=Search Person");
        browser.select("search_defects", "label=" + defectName);
        browser.click("//input[@value='Search Persons']");
        browser.waitForCondition("selenium.isElementPresent('//div[@id=\"search_results\"]/table')", TIMEOUT);
        assertEquals("did not return person on search", PERSON_NAME, browser.getText("//div[@id=\"search_results\"]/table/tbody/tr[2]/td[1]"));
    }

    public void testShouldEditPersonAfterSearch() {
        login();
        browser.click("link=Search Person");
        browser.select("search_defects", "label=" + defectName);
        browser.click("//input[@value='Search Persons']");
        browser.waitForCondition("selenium.isElementPresent('//div[@id=\"search_results\"]/table')", TIMEOUT);

        browser.type("person_name", "");
        browser.click("link=Edit");
        browser.waitForCondition("selenium.getValue('person_name')=='" + PERSON_NAME + "'", TIMEOUT);
        assertEquals(LAT, browser.getValue("person_lat"));
        assertEquals(LON, browser.getValue("person_lon"));
        assertEquals(DOB, browser.getValue("person_dateOfBirth"));
        assertTrue(arrayContains(browser.getSelectOptions("//select[@id=\"person_defects\"]"), defectName));
        browser.click("//input[@value='Save Person']");
        browser.waitForCondition("selenium.isElementPresent('//div[@id=\"add_person_errors\"]/font')", TIMEOUT);
        assertEquals("failed to edit person", "Saved.", browser.getText("//div[@id=\"add_person_errors\"]/font"));
    }

    public void testShouldAddAnEnvHazard() {
        login();
        browser.click("link=Add Env Hazard");
        browser.type("envhazard_name", HAZARD_NAME);
        browser.type("envhazard_desc", "hazard desc");
        browser.type("envhazard_vertices", HAZARD_VERTICES);
        browser.type("envhazard_naic", INDUSTRY_CLASSIFICATION);
        browser.type("envhazard_bbox", HAZARD_BBOX);
        browser.click("//input[@value='Save']");
        browser.waitForCondition("selenium.isElementPresent('//div[@id=\"add_envhazard_errors\"]/font')", TIMEOUT);
        assertEquals("Saved.", browser.getText("//div[@id=\"add_envhazard_errors\"]/font"));
    }

    public void testSearchShouldReturnEnvHazards() {
        login();
        // partial name search should work
        browser.type("search_hazards_text", "leather");
        browser.click("//input[@value='Search Hazards']");
        browser.waitForCondition("selenium.isElementPresent('//div[@id=\"search_results\"]/table')", TIMEOUT);
        assertEquals(HAZARD_NAME, browser.getText("//div[@id=\"search_results\"]/table/tbody/tr[2]/td[1]"));
        assertEquals(INDUSTRY_CLASSIFICATION, browser.getText("//div[@id=\"search_results\"]/table/tbody/tr[2]/td[2]"));
    }

    public void tearDown() {
        browser.click("link=Logout");
        browser.stop();
    }

    private boolean arrayContains(String[] items, String name) {
        for (String s : items) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
