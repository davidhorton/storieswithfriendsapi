package com.storieswithfriends.api.artifacts;


import java.util.Date;
import java.util.List;

/**
 * @author David Horton
 */
public class Story {

    private String id;
    private User owner;
    private List<User> participants;
    private List<Word> words;
    private String title;
    private boolean allFinished;
    private User whoseTurn;
    private Date dateStarted;
    private Date dateFinished;

    public User getWhoseTurn() {
        return whoseTurn;
    }

    public void setWhoseTurn(User whoseTurn) {
        this.whoseTurn = whoseTurn;
    }

    public boolean isAllFinished() {
        return allFinished;
    }

    public void setIsAllFinished(boolean allFinished) {
        this.allFinished = allFinished;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Date dateFinished) {
        this.dateFinished = dateFinished;
    }
}
