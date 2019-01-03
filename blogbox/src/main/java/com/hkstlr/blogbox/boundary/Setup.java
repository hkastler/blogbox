package com.hkstlr.blogbox.boundary;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import com.hkstlr.blogbox.control.Config;
import com.hkstlr.blogbox.control.EmailReader;
import com.hkstlr.blogbox.control.FetchEvent;


@Model
public class Setup {
	
    private User user = new User();
    private String imapHost;
    private String folderName;
    private String action = "create";
    private String siteName = "";

    @Inject
    Config config;
    
    @Inject
    private Event<FetchEvent> event;

    public Setup() {
        super();
    }

    public void setup()  {
        config.getProps().put(EmailReader.EmailReaderPropertyKeys.PASSWORD, 
        		this.user.getPassword());
        config.getProps().put(EmailReader.EmailReaderPropertyKeys.FOLDER_NAME, 
        		this.folderName);
        config.getProps().put(EmailReader.EmailReaderPropertyKeys.USERNAME, 
        		this.user.getUsername());
        config.getProps().put(EmailReader.EmailReaderPropertyKeys.MAIL_IMAP_HOST, 
        		this.imapHost);
        config.getProps().put("site.name", this.siteName);
        
        
        if(config.isSetup()) {
        	event.fire(new FetchEvent(this.getClass().getName().concat(".setup()")));
        }
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext. getApplication()
                .getNavigationHandler().handleNavigation(facesContext, null, 
                        "index");
    }
    

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
	 * @return the imapHost
	 */
	public String getImapHost() {
		return imapHost;
	}

	/**
	 * @param imapHost the imapHost to set
	 */
	public void setImapHost(String imapHost) {
		this.imapHost = imapHost;
	}

	public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    
    
    @Produces
    public boolean isSetup() {
    	return config.isSetup();
    }
    
    public class User {

    	private String username;
    	private String password;

    	public User() {
            super();
    	}

    	public String getUsername() {
    		return username;
    	}

    	public void setUsername(String username) {
    		this.username = username;
    	}

    	public String getPassword() {
    		return password;
    	}

    	public void setPassword(String password) {
    		this.password = password;
    	}

    }

}
