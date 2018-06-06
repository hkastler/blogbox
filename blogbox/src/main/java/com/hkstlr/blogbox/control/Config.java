package com.hkstlr.blogbox.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import com.hkstlr.blogbox.control.EmailReader.EmailReaderPropertyKeys;

/**
 *
 * @author henry.kastler
 */
@Startup
@ApplicationScoped
public class Config {

    private Properties props = new Properties();
    private Logger log = Logger.getLogger(this.getClass().getName());

    public Config() {
        super();
    }

    public Config(Properties props) {
        this.props = props;
    }

    @PostConstruct
    void init() {

        try {
        	
        	InputStream is = null;
        	is = new FileInputStream(new File("/etc/config/blogbox_app_properties"));
        	props.load(is);
        }catch (Exception e) {
        	log.log(Level.SEVERE, null, e);
		}

    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    
    public boolean isSetup() {
        try {
            return this.getProps().containsKey(EmailReaderPropertyKeys.USERNAME) 
            		&& this.getProps().containsKey(EmailReaderPropertyKeys.PASSWORD)
                    && this.getProps().containsKey(EmailReaderPropertyKeys.FOLDER_NAME)
                    && this.getProps().containsKey(EmailReaderPropertyKeys.MAIL_IMAP_HOST);
        } catch (Exception e) {
            return false;
        }
    }

}
