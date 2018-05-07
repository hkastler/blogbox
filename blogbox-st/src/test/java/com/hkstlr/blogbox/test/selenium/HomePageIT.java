/*
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.hkstlr.blogbox.test.selenium;

import com.hkstlr.blogbox.test.util.TestUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 *
 * @author Henry
 */
public class HomePageIT {

    private static final Logger LOG = Logger.getLogger(HomePageIT.class.getName());
    
    HtmlUnitDriver wd;
    String baseUrl = TestUtils.getTestURL();
    
    
    @Before
    public void setUp() throws Exception {
        wd = new HtmlUnitDriver();
        Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
    }
    
    @Test
    public void clickthru() {
        String url = baseUrl.concat("/");
        LOG.info(url);
        wd.get(url);
        
        Assert.assertEquals("hkstlr.com", getHeadingText());
        
        
    }
    
    private String getHeadingText(){
        String heading = wd.findElement(By.tagName("title")).getText();
        return heading;
    }
    
    @After
    public void tearDown() {
        wd.quit();
    }
    
    public static boolean isAlertPresent(FirefoxDriver wd) {
        try {
            wd.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }
}
