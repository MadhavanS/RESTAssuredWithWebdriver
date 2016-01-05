package com.epam.restassured;

//TODO: 1. create basic webdriver script + proper page object pattern usage  - [FINISHED]
//TODO: 2. create basic script to do the subscription with webdriver and verify with rest - [FINISHED]
//TODO: 3. create composition for subscription page with PO (using abstract factory) - [FINISHED]
//TODO: 4. cerate CSV reader with Singleton design pattern - [FINISHED]
//TODO: 5. create DDT script for REST script - [FINISHED]
//TODO: 6. create DDT script for webdriver script
//TODO: 7. create rest script without BDD style (using JUnit asssertions)

import com.epam.restassured.pageobjects.SignUpPagePageObject;
import com.epam.restassured.pageobjects.ThankYouPagePageObject;
import com.epam.restassured.pageobjects.ThankYouPageVerifier;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Created by Tamas_Csako
 */
public class BasicServiceTestWithWebDriverAndRest {
    private static Logger log = Logger.getLogger(BasicServiceTestWithWebDriverAndRest.class);
    private static final int NUMBER_OF_RESPONSE = 1;
    private static final String CONTENT_NUMBER_OF_ELEMENTS = "numberOfElements";
    private static final String CONTENT_EMAIL_ADDRESS = "content.emailAddress";

    // Test data
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String emailAddressConfirmation;
    private boolean subscribeNewsletter;
    private int httpOkStatus;
    // Verification
    private List<String> listToVerifyEmail;

    private SignUpPagePageObject signUpPagePageObject;
    private ThankYouPagePageObject thankYouPagePageObject;
    private ThankYouPageVerifier thankYouPageVerifier;
    private WebDriver driver;

    /**
     * Responsible for setting up test data and environment.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        log.info("*******************************************");
        log.info("Deleting existing records");
        if (given().delete(ServiceTestingProperties.REST_API_URL).getStatusCode() == httpOkStatus) {
            log.info("Records were deleted successfully");
        } else {
            log.info("Something went wrong! Existing records couldn't be deleted");
        }

        log.info("Setting up test data");
        firstName = "John";
        lastName = "Doe";
        emailAddress = "johndoe@freecloud.com";
        emailAddressConfirmation = "johndoe@freecloud.com";
        subscribeNewsletter = true;
        httpOkStatus =  HttpStatus.SC_OK;

        log.info("Setting up verification data");
        listToVerifyEmail = new ArrayList<String>();
        listToVerifyEmail.add(emailAddress);
        log.info("Initializing Firefox driver");
        driver = new FirefoxDriver();
        log.info("Opening subscription page");
        driver.get("https://t7-f0x.rhcloud.com/subscription/subscription.html");
        signUpPagePageObject = new SignUpPagePageObject(driver);
    }

    /**
     * Makes an automated subscription then checks the data correctness.
     *
     * @throws Exception
     */
    @Test
    public void addRecord() throws Exception {
        log.info("Filling fields and sending data");
        signUpPagePageObject.givenSignUp(firstName, lastName, emailAddress, emailAddressConfirmation,
                subscribeNewsletter);
        thankYouPagePageObject = new ThankYouPagePageObject(driver);
        log.info("Checking 'Thank you' page URL, subscriber name and e-mail");
        thankYouPageVerifier = new ThankYouPageVerifier(thankYouPagePageObject);
        thankYouPageVerifier.whenSubscribeFinishedCheckDataOnPage(firstName, emailAddress);
        when().get(ServiceTestingProperties.REST_API_URL).
                then().statusCode(httpOkStatus).
                and().content(CONTENT_NUMBER_OF_ELEMENTS, is(NUMBER_OF_RESPONSE)).
                and().content(CONTENT_EMAIL_ADDRESS, equalTo(listToVerifyEmail));
    }
    //TODO  1 webdriver version(SubscriptionWithWebDriver), 1 mixed(.this) and one unmodified;
    //TODO remove TODO-s when finished
    /**
     *  Closes the browser after test execution.
     */
    @After
    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
