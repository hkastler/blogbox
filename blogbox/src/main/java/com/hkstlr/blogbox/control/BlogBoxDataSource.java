
package com.hkstlr.blogbox.control;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Startup;

/**
 *
 * @author henry.kastler
 */
@DataSourceDefinition(name = "java:app/blogbox/BlogBoxDS",
    className = "org.h2.jdbcx.JdbcDataSource",
    url = "jdbc:h2:mem:blogbox;DB_CLOSE_DELAY=-1",
    user = "sa",
    password = "sa",
    initialPoolSize = 4,
    minPoolSize = 4,
    maxPoolSize = 16
)
@Startup
public class BlogBoxDataSource {
    //see annotation
    public BlogBoxDataSource(){
        super();
    }
}
