package com.hkstlr.blogbox.control;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
import javax.servlet.ServletContext;

import org.ocpsoft.logging.Logger.Level;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Log;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.config.rule.TrailingSlash;

@RewriteConfiguration
public class ApplicationConfigurationProvider extends HttpConfigurationProvider {

	private static final String INDEX_PATH = "/index.xhtml";
    private static final String ENTRY_PATH = "/entry.xhtml";
    
    public Configuration getConfiguration(ServletContext context) {
        return ConfigurationBuilder.begin()
                .addRule().perform(Log.message(Level.DEBUG, "rewrite in the app"))
                .addRule(Join.path("/index").to(INDEX_PATH))
                .addRule(Join.path("/").to(INDEX_PATH))
                .addRule(Join.path("/page/{page}").to(INDEX_PATH))
                .addRule(Join.path("/page/{page}/pageSize/{pageSize}").to(INDEX_PATH))

                .addRule(Join.path("/entry/{href}").to(ENTRY_PATH ))
                .addRule(TrailingSlash.append())
                .when(Path.matches("/{x}"))
                .where("x").matches("^(?!.*\\.xhtml.*).*$");
    }

    @Override
    public int priority() {
        return 0;
    }

}
