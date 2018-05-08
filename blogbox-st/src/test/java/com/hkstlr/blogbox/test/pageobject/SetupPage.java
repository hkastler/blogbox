package com.hkstlr.blogbox.test.pageobject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.LoadableComponent;

import com.hkstlr.blogbox.test.util.TestUtils;

public class SetupPage extends LoadableComponent<SetupPage> {
	
	public WebDriver driver;
	   
    public final String pageURL;

    @FindBy(id = "setupForm")
    public WebElement setupForm;
    
    @FindBy(id = "imapHost")
    public WebElement imapHostField;
    @FindBy(id = "username")
    public WebElement usernameField;
    @FindBy(id = "password")
    public WebElement passwordField;   
    
    @FindBy(id = "folderName")
    public WebElement folderNameField;    

    public SetupPage(WebDriver aDriver) {
        this.pageURL = TestUtils.getTestURL()
                .concat("/index.xhtml");
                
        driver = aDriver;
        PageFactory.initElements(driver, this);
        driver.get(pageURL);       
    }



	@Override
    protected void load() {
        this.driver.get(pageURL);
    }



	@Override
	protected void isLoaded() throws Error {
		// do something
		
	}

}
