package com.hkstlr.blogbox.test.cucumber;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.hkstlr.blogbox.test.pageobject.SetupPage;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class SetupStepDefs extends BaseStepDefs {

	private SetupPage put;
	private WebElement wl;

	@Before
	public void setUp() {
		super.setUp();
		put = new SetupPage(driver);
		
		System.out.println( driver.getCurrentUrl() );
	}

	@Given("^the setup form is displayed$")
	public void the_setup_form_is_displayed() throws Throwable {
		assertNotNull(put.setupForm);
	}

	@Given("^user enters \"([^\"]*)\" in setup form siteName field$")
	public void user_enters_in_setup_form_siteName_field(String arg1) throws Throwable {
		wl = put.siteName;
		wl.clear();
        wl.sendKeys(arg1);
	}
	
	
	@Given("^user enters \"([^\"]*)\" in setup form imapHost field$")
	public void user_enters_in_setup_form_imapHost_field(String arg1) throws Throwable {
		wl = put.imapHostField;
		wl.clear();
        wl.sendKeys(arg1);
	}

	@Given("^user enters \"([^\"]*)\" in setup form username field$")
	public void user_enters_in_setup_form_username_field(String arg1) throws Throwable {
		put.usernameField.clear();
        put.usernameField.sendKeys(arg1);
	}

	@Given("^user enters \"([^\"]*)\" in setup form password field$")
	public void user_enters_in_setup_form_password_field(String arg1) throws Throwable {
		wl = put.passwordField;
		wl.clear();
        wl.sendKeys(arg1);
	}

	@Given("^user enters \"([^\"]*)\" in setup form folderName field$")
	public void user_enters_in_setup_form_folderName_field(String arg1) throws Throwable {
		wl = put.folderNameField;
		wl.clear();
        wl.sendKeys(arg1);
	}

	@Given("^user submits the setup form$")
	public void user_submits_the_setup_form() throws Throwable {
		put.setupForm.submit();
	}

	@Then("^a loading message should be displayed$")
	public void a_loading_message_should_be_displayed() throws Throwable {
		 String body = driver.findElement(By.tagName("body")).getText();
	        boolean hasSuccessMessage = body.contains("Loading");
	        assertTrue(hasSuccessMessage);
	}

}
