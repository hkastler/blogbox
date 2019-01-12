package com.hkstlr.blogbox.boundary;

import java.util.logging.Level;
import java.util.logging.Logger;

@javax.ejb.Startup
@javax.ejb.Singleton
public class StartupBean{

    @javax.inject.Inject
    private com.hkstlr.blogbox.control.Index index;

    public StartupBean(){
        super();
    }
 
    @javax.annotation.PostConstruct
    public void eagerInit() {
        Logger.getLogger(StartupBean.class.getCanonicalName())
        .log(Level.INFO,"StartupBean.eagerInit.isSetup:{0}",index.getConfig().isSetup());
       ;
    }
}