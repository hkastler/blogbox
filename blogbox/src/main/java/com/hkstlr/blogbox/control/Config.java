package com.hkstlr.blogbox.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.hkstlr.blogbox.control.EmailReader.EmailReaderPropertyKeys;

/**
 *
 * @author henry.kastler
 */
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
            is = new FileInputStream(new File("/etc/opt/blogbox/blogbox.properties"));
            props.load(is);
            is.close();
        } catch (FileNotFoundException ne) {
            log.info("/etc/opt/blogbox/blogbox.properties not found");
            try {
                props.load(this.getClass().getClassLoader().getResourceAsStream("app.properties"));
            } catch (IOException e) {
                log.log(Level.SEVERE, null, e);
            }
        } catch (Exception e) {
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

        Boolean hasLocalClientConfig = this.getProps().containsKey(EmailReaderPropertyKeys.USERNAME)
                && this.getProps().containsKey(EmailReaderPropertyKeys.PASSWORD)
                && this.getProps().containsKey(EmailReaderPropertyKeys.FOLDER_NAME)
                && this.getProps().containsKey(EmailReaderPropertyKeys.MAIL_IMAPS_HOST);
        
        Boolean hasContainerConfig = this.props.containsKey(EmailReaderPropertyKeys.JNDI_NAME)
                && this.getProps().containsKey(EmailReaderPropertyKeys.FOLDER_NAME);
        
        return hasContainerConfig || hasLocalClientConfig;
    }

}
