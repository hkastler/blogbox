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

import com.hkstlr.blogbox.test.pageobject.HomePage;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Henry
 */
public class HomeStepDefs extends BaseStepDefs {

    private HomePage put;

    @Before
    public void setUp() {
        super.setUp();
        put = new HomePage(driver);
    }

    @Given("^the user is on home page$")
    public void the_user_is_on_home_page() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        
    }

    @Then("^a the title should be \"([^\"]*)\"$")
    public void a_the_title_should_be(String arg1) throws Throwable {
        String expected = "hkstlr.com";
        assertEquals(expected, arg1);
    }
}
