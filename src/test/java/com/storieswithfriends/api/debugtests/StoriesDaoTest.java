package com.storieswithfriends.api.debugtests;


import com.storieswithfriends.api.artifacts.Story;
import com.storieswithfriends.api.artifacts.StorySummary;
import com.storieswithfriends.api.artifacts.User;
import com.storieswithfriends.api.artifacts.Word;
import com.storieswithfriends.api.data.StoriesDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author David Horton
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:StoriesSpringContext.xml")
public class StoriesDaoTest {

    @Autowired
    private StoriesDao dao;

    /*@Test
    public void testAddUser() {
        dao.addUser(generateTestUser("Betty Bets1"), "abc123");
        dao.addUser(generateTestUser("Betty Bets2"), "abc123");
        dao.addUser(generateTestUser("Betty Bets3"), "abc123");
    }*/

    /*@Test
    public void testAddNewStory() {

        Story story = new Story();
        story.setTitle("Betty's third story");
        User ownerToSend = generateTestUser("Betty Bets1");
        ownerToSend.setOwner(true);
        story.setOwner(ownerToSend);
        story.setParticipants(new ArrayList<User>(Arrays.asList(generateTestUser("Betty Bets2"), generateTestUser("Betty Bets3"))));

        Word word1 = new Word();
        word1.setDateAdded(new Date());
        word1.setUserWhoAddedIt(generateTestUser("Betty Bets1"));
        word1.setWordValue("there");

        story.setWords(new ArrayList<Word>(Arrays.asList(word1)));

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

        System.out.println("New story id: " + newStoryId);

        Story storyStored = dao.getSpecificStory(newStoryId);
        System.out.println("\nID: " + storyStored.getId());
        System.out.println("Date started: " + storyStored.getDateStarted());
        System.out.println("Date finished: " + storyStored.getDateFinished());
        System.out.println("Title: " + storyStored.getTitle());
        System.out.println("Owner: " + storyStored.getOwner());
        System.out.println("Whose Turn: " + storyStored.getWhoseTurn());

        System.out.println("\nWords...");
        for(Word word : storyStored.getWords()) {
            System.out.println("Word: " + word.getWordValue() + ", Date added: " + word.getDateAdded() + ", " + word.getUserWhoAddedIt());
        }

        if(storyStored.getParticipants() != null) {
            System.out.println("\nParticipants...");
            for (User user : storyStored.getParticipants()) {
                System.out.println(user);
            }
        }
    }*/

    @Test
    public void testGetYourTurnStories() {
        //List<StorySummary> stories = dao.getYourTurnStories("betty_bets2");
        //for(StorySummary story : stories) {
            //System.out.println("Title: " + story.getTitle() + ", Id: " + story.getId() + ", Number of players: " + story.getNumberOfPlayers() + ", Whose turn: " + story.getWhoseTurn());
        //}
        //TODO make this meaningful...
    }

    /*@Test
    public void testFinishStory() {
        dao.finishStory(27);
    }*/

    /*@Test
    public void testLogin() {
        assert(!dao.login("bobbyjoe", "abc124"));
        assert(dao.login("bobbyjoe", "abc123"));
        assert(!dao.login("bobbyjoesss", "abc123"));
    }*/

    /*@Test
    public void testGetPastStories() {
        List<StorySummary> stories = dao.getPastStories("betty_bets1");

        for(StorySummary story : stories) {
            System.out.println("Title: " + story.getTitle() + ", Id: " + story.getId() + ", Number of players: " + story.getNumberOfPlayers() + ", Whose turn: " + story.getWhoseTurn());
        }
    }*/

    /*@Test
    public void testGetUnfinishedStories() {
        List<StorySummary> stories = dao.getUnfinishedStories("betty_bets1");

        for(StorySummary story : stories) {
            System.out.println("Title: " + story.getTitle() + ", Id: " + story.getId() + ", Number of players: " + story.getNumberOfPlayers() + ", Whose turn: " + story.getWhoseTurn());
        }
    }*/

    /*@Test
    public void testAddStoryParticipant() {
        dao.addStoryParticipant(generateTestUser("jimmy_joe2"), 9, 0);
    }

    @Test
    public void testAddWordToStory() {
        User user = new User();
        user.setUsername("bobbyjoe");

        Word word = new Word();
        word.setWordValue("butt munch");
        word.setUserWhoAddedIt(user);
        dao.addWordToStory(word, 21);
    }

    @Test
    public void testGetSpecificStory() {
        Story story = dao.getSpecificStory(21);
        System.out.println("ID: " + story.getId());
        System.out.println("Date started: " + story.getDateStarted());
        System.out.println("Date finished: " + story.getDateFinished());
        System.out.println("Title: " + story.getTitle());
        System.out.println("Owner: " + story.getOwner());
        System.out.println("Whose Turn: " + story.getWhoseTurn());

        System.out.println("\nWords...");
        for(Word word : story.getWords()) {
            System.out.println("Word: " + word.getWordValue() + ", Date added: " + word.getDateAdded() + ", " + word.getUserWhoAddedIt());
        }

        if(story.getParticipants() != null) {
            System.out.println("\nParticipants...");
            for (User user : story.getParticipants()) {
                System.out.println(user);
            }
        }

    }*/

    private User generateTestUser(String name) {
        User user = new User();
        user.setDisplayName(name);
        user.setUsername(name.toLowerCase().replace(' ', '_'));
        return user;
    }

    private ArrayList<Word> generateTestWords() {
        Word word1 = new Word();
        word1.setDateAdded(new Date());
        word1.setUserWhoAddedIt(generateTestUser("Bobby"));
        word1.setWordValue("there");
        Word word2 = new Word();
        word2.setDateAdded(new Date());
        word2.setUserWhoAddedIt(generateTestUser("Jimmy"));
        word2.setWordValue("was");
        Word word3 = new Word();
        word3.setDateAdded(new Date());
        word3.setUserWhoAddedIt(generateTestUser("Davey"));
        word3.setWordValue("a");
        Word word4 = new Word();
        word4.setDateAdded(new Date());
        word4.setUserWhoAddedIt(generateTestUser("Bobby"));
        word4.setWordValue("dude");
        return new ArrayList<Word>(Arrays.asList(word1, word2, word3, word4));
    }
}
