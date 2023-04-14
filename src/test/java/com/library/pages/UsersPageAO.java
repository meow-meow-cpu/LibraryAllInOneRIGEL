package com.library.pages;

import com.library.utility.BrowserUtil;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class UsersPageAO {

    @FindBy(xpath = "//input[@type='search']")
    public WebElement searchUser;

    @FindBy(xpath = "//tbody/tr/td[3]")
    public List<WebElement> allNames;

    public void verifyUserExists(String fullName){
        boolean userExists = false;
        // BrowserUtil.waitForVisibility(searchUser, 10);
        searchUser.sendKeys(fullName);
        for (WebElement eachName : allNames) {
            System.out.println("eachName = " + eachName);
            if(eachName.getText().equals(fullName))
                userExists= true;
        }
        Assert.assertTrue(userExists);


    }
}
