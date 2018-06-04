Feature: Setup Blogbox
	As a user
	I want to setup blogbox to read an email folder

Scenario: setup from setup home page
	Given the setup form is displayed
	And user enters "your site name" in setup form siteName field
	And user enters "your imaps host" in setup form imapHost field
    And user enters "your email username" in setup form username field
    And user enters "your email account password" in setup form password field
    And user enters "the name of the folder holding blog messages" in setup form folderName field
	
       
    And user submits the setup form
    Then a loading message should be displayed