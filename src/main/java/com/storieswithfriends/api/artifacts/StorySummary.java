package com.storieswithfriends.api.artifacts;

/**
 * @author David Horton
 */
public class StorySummary {

    private String id;
    private String title;
    private User whoseTurn;
    private long numberOfPlayers;

    public User getWhoseTurn() {
        return whoseTurn;
    }

    public void setWhoseTurn(User whoseTurn) {
        this.whoseTurn = whoseTurn;
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

    public long getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(long numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }
}
