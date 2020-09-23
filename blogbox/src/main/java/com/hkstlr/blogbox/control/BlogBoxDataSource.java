
package com.hkstlr.blogbox.control;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Startup;

/**
 *
 * @author henry.kastler
 * C:\\etc\\opt\\blogbox\\data
 * /etc/opt/blogbox/data
 */
@DataSourceDefinition(name = "java:app/blogbox/BlogBoxDS",
    className = "org.h2.jdbcx.JdbcDataSource",
    url = "jdbc:h2:/etc/opt/blogbox/data;DB_CLOSE_DELAY=-1;LOB_TIMEOUT=1000;",
    user = "sa",
    password = "sa"
    )
@Startup
public class BlogBoxDataSource {
    //see annotation
    public BlogBoxDataSource(){
        super();
    }
}
