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

package com.hkstlr.blogbox.test.cucumber;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.hkstlr.blogbox.test.util.TestUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class BaseStepDefs {

    protected WebDriver driver;
    private String envURL;

    @PostConstruct
    public void setUp() {
        driver = new HtmlUnitDriver();
        envURL = TestUtils.getTestURL();
        //System.out.println("myURL:" + myURL);
    }

    @PreDestroy
    public void tearDown() {
        //System.out.println("tearDown in BaseStepDefs");
        driver.close();
    }
}
