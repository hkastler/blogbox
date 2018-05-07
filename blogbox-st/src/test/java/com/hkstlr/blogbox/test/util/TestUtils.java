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
package com.hkstlr.blogbox.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TestUtils {

    private static final Logger LOG = Logger.getLogger(TestUtils.class.getName());

    public static String getNewUserEmailAddress(String oldEmail) {

        String randomString = TestUtils.getRandomString();

        String newEmail = String.format(oldEmail, randomString);

        return newEmail;
    }

    public static String getTestURL() {
        Properties prop = new Properties();
        String testURL = "http://localhost:8080";

        try {
            //load a properties file
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream stream = loader.getResourceAsStream("test.properties");
            prop.load(stream);
            //get the property value and print it out
            //System.out.println("hostName:" + prop.getProperty("hostName"));

            StringBuilder newURL = new StringBuilder(prop.getProperty("protocol", "http"));
            newURL.append("://");
            newURL.append(prop.getProperty("hostName"));
            String port = prop.getProperty("port", "80");
            if (!"80".equals(port)) {
                newURL.append(":").append(port);
            }
            testURL = newURL.toString();
        } catch (IOException ex) {
            LOG.log(Level.FINE, "", ex);
        }

        return testURL;
    }

    public static String getRandomString() {
        return getRandomString(11);
    }
    
    public static String getRandomString(int len) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

}
