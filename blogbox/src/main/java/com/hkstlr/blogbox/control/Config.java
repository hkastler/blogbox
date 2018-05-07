package com.hkstlr.blogbox.control;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import com.hkstlr.blogbox.control.EmailReader.EmailReaderPropertyKey;

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
            props.load(this.getClass().getClassLoader().getResourceAsStream("app.properties"));

        } catch (IOException e) {

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
            return this.getProps().containsKey(EmailReaderPropertyKey.USERNAME) 
            		&& this.getProps().containsKey(EmailReaderPropertyKey.PASSWORD)
                    && this.getProps().containsKey(EmailReaderPropertyKey.FOLDER_NAME)
                    && this.getProps().containsKey(EmailReaderPropertyKey.MAIL_IMAP_HOST);
        } catch (Exception e) {
            return false;
        }
    }

}
