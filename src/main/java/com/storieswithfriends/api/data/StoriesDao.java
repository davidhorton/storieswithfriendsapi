package com.storieswithfriends.api.data;

import com.storieswithfriends.api.artifacts.Story;
import com.storieswithfriends.api.artifacts.StorySummary;
import com.storieswithfriends.api.artifacts.User;
import com.storieswithfriends.api.artifacts.Word;

import java.util.List;

/**
 * @author David Horton
 */
public interface StoriesDao {

    public void addUser(User user, String password);

    public int addNewStory(Story story);

    public void addStoryParticipant(User user, int storyId, int orderPosition);

    public void addWordToStory(Word word, int storyId);

    public Story getSpecificStory(int storyId);

    public List<StorySummary> getYourTurnStories(String username);

    public List<StorySummary> getPastStories(String username);

    public List<StorySummary> getUnfinishedStories(String username);

    public void finishStory(int storyId);

    public User login(String username, String enteredPassword);

    public boolean usernameExists(String username);
}
