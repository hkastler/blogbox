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
package com.hkstlr.blogbox.control;

import java.text.MessageFormat;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.config.rule.TrailingSlash;

@RewriteConfiguration
public class ApplicationConfigurationProvider extends HttpConfigurationProvider {

    private static final String INDEX = "index";
    private static final String ENTRY = "entry";
    private static final String PAGE = "page";
    private static final String PAGE_SIZE = "pageSize";

    private static final String FS = "/";
    private static final String URL_EXTENSION = ".xhtml";

    private static final String INDEX_URL = FS + INDEX + URL_EXTENSION;
    private static final String ENTRY_URL = FS + ENTRY + URL_EXTENSION;
    
    public Configuration getConfiguration(ServletContext context) {

        String indexPath = FS + INDEX;
        String entryPath = FS + ENTRY + FS + "{href}";
        String pagePath = FS + PAGE + FS + "{page}";
        String pageSizePath = FS + PAGE_SIZE + FS + "{pageSize}";
        String extMatcher = MessageFormat.format("^(?!.*\\.{0}.*).*$", URL_EXTENSION);

        return ConfigurationBuilder.begin()

                .addRule(Join.path(FS).to(INDEX_URL))
                
                .addRule(Join.path(indexPath).to(INDEX_URL))
                
                .addRule(Join.path(pagePath).to(INDEX_URL))
                .addRule(Join.path(pagePath + pageSizePath).to(INDEX_URL))

                .addRule(Join.path(entryPath).to(ENTRY_URL))

                .addRule(TrailingSlash.append())
                .when(Path.matches("/{x}"))
                .where("x").matches(extMatcher);
    }

    @Override
    public int priority() {
        return 0;
    }

}
