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
package com.hkstlr.blogbox.test.pageobject;

import com.hkstlr.blogbox.test.util.TestUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.LoadableComponent;

/**
 *
 * @author Henry
 */
public class HomePage extends LoadableComponent<HomePage> {
    private final WebDriver driver;
    private final String pageURL;

    
    public HomePage(WebDriver aDriver) {
        this.pageURL = TestUtils.getTestURL();
        this.driver = aDriver;
        PageFactory.initElements(driver, this);
        this.driver.get(pageURL);
    }

    @Override
    protected void load() {
        this.driver.get(pageURL);
    }

    @Override
    protected void isLoaded() throws Error {
        //do something here
    }
}
