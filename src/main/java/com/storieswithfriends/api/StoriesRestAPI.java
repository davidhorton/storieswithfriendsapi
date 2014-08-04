package com.storieswithfriends.api;

import com.storieswithfriends.api.artifacts.Story;
import com.storieswithfriends.api.artifacts.StorySummary;
import com.storieswithfriends.api.artifacts.User;
import com.storieswithfriends.api.artifacts.Word;
import com.storieswithfriends.api.context.SpringApplicationContext;
import com.storieswithfriends.api.data.StoriesDao;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * @author David Horton
 */
@Path("/")
public class StoriesRestAPI {

    private final static String LASTWORD = "the_end."; //This is what a user must enter to trigger the end of a story.

    private StoriesDao dao;

    public StoriesRestAPI() {
        dao = (StoriesDao) SpringApplicationContext.getBean("storiesDao");
    }

    @GET
    @Path("story/{id}")
    @Produces("application/json")
    public Story getStory(@PathParam("id") int id) {
        return dao.getSpecificStory(id);
    }

    @GET
    @Path("story/past/{username}")
    @Produces("application/json")
    public List<StorySummary> getPastStories(@PathParam("username") String username) {
        return dao.getPastStories(username);
    }

    @GET
    @Path("story/yourturn/{username}")
    @Produces("application/json")
    public List<StorySummary> getYourTurnStories(@PathParam("username") String username) {
        return dao.getYourTurnStories(username);
    }

    /**
     * I didn't put this in my project proposal, but this would be for a user to see the status of the stories that they are owners of.
     * @param username For the time being, the userId will just be the username
     * @return A list of unfinished story summaries
     */
    @GET
    @Path("story/unfinished/{username}")
    @Produces("application/json")
    public List<StorySummary> getUnfinishedStories(@PathParam("username") String username) {
        return dao.getUnfinishedStories(username);
    }

    @POST
    @Path("story/newstory")
    @Consumes("application/json")
    public Response createNewStory(Story story) {
        int newStoryId = dao.addNewStory(story);

        //Add the owner as a story participant first
        User owner = story.getOwner();
        owner.setMyTurn(true);  //It technically is his turn because the word hasn't been added yet. Once it's added, it'll get incremented to the next guy's turn.
        dao.addStoryParticipant(story.getOwner(), newStoryId, 0);

        //Add all of the other story participants
        for(int i = 0; i < story.getParticipants().size(); i++) {
            dao.addStoryParticipant(story.getParticipants().get(i), newStoryId, i + 1);
        }

        dao.addWordToStory(story.getWords().get(0), newStoryId);

        String result = "New story created with ID of " + newStoryId;
        return Response.status(201).entity(result).build();
    }


    @POST
    @Path("story/addword")
    @Consumes("application/x-www-form-urlencoded")
    public Response addWordToStory(@FormParam("storyId") String storyId, @FormParam("newWord") String newWord, @FormParam("username") String username) {

        dao.addWordToStory(createNewWord(newWord, username), Integer.parseInt(storyId));

        String result = "New word added to story with StoryId of" + storyId + ". The new word was: " + newWord;
        if(StoriesRestAPI.LASTWORD.equalsIgnoreCase(newWord)) {
            dao.finishStory(Integer.parseInt(storyId));
            result += ". The story is now finished.";
        }

        return Response.status(200).entity(result).build();
    }

    @POST
    @Path("story/newuser")
    @Consumes("application/x-www-form-urlencoded")
    public Response createNewUser(@FormParam("username") String username, @FormParam("displayName") String displayName, @FormParam("password") String password) {

        dao.addUser(createNewUser(username, displayName), password);

        String result = "New user created";
        return Response.status(201).entity(result).build();
    }

    @POST
    @Path("story/login")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public User login(@FormParam("username") String username, @FormParam("password") String password) {
        return dao.login(username, password);
    }

    /**
     *
     * @param newWord The String of the new word to be added
     * @param username The username of the user who is adding the word
     * @return The newly created word
     */
    private Word createNewWord(String newWord, String username) {

        Word word = new Word();
        word.setDateAdded(new Date());
        word.setWordValue(newWord);
        word.setUserWhoAddedIt(createNewUser(username, null));

        return word;
    }

    /**
     *
     * @param username The username of the user to be created
     * @param displayName The display name of the user to be created
     * @return The newly created user
     */
    private User createNewUser(String username, String displayName) {

        User user = new User();
        user.setUsername(username);
        user.setDisplayName(displayName);

        return user;
    }
}
