package com.storieswithfriends.api.artifacts;

import java.util.Date;

/**
 * @author David Horton
 */
public class Word {

    private Date dateAdded;
    private User userWhoAddedIt;
    private String wordValue;

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public User getUserWhoAddedIt() {
        return userWhoAddedIt;
    }

    public void setUserWhoAddedIt(User userWhoAddedIt) {
        this.userWhoAddedIt = userWhoAddedIt;
    }

    public String getWordValue() {
        return wordValue;
    }

    public void setWordValue(String wordValue) {
        this.wordValue = wordValue;
    }
}
