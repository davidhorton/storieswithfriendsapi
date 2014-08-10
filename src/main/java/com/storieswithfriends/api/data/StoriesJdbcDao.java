package com.storieswithfriends.api.data;


import com.storieswithfriends.api.artifacts.Story;
import com.storieswithfriends.api.artifacts.StorySummary;
import com.storieswithfriends.api.artifacts.User;
import com.storieswithfriends.api.artifacts.Word;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author David Horton
 */
public class StoriesJdbcDao extends JdbcDaoSupport implements StoriesDao {

    /**
     *
     * @param user User to add
     * @param password Password of new user
     */
    public void addUser(User user, String password) {
        String sql = "INSERT into \"AppUser\" (\"Username\", \"Password\", \"DisplayName\") VALUES (?, ?, ?)";

        getJdbcTemplate().update(sql, user.getUsername(), password, user.getDisplayName());
    }

    /**
     *
     * @param story Story to add
     * @return The Id of the new story
     */
    public int addNewStory(Story story) {
        String sql = "INSERT into \"Story\" (\"DateStarted\", \"Title\", \"NumberOfPlayers\") VALUES (current_timestamp, ?, ?) RETURNING \"StoryId\"";

        return getJdbcTemplate().queryForObject(sql, new Object[]{story.getTitle(), story.getParticipants().size() + 1}, new RowMapper<Integer>(){
            @Override
            public Integer mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                return resultSet.getInt("StoryId");
            }

        });
    }

    /**
     *
     * @param user The user to add to the story
     * @param storyId The Id of the story
     */
    public void addStoryParticipant(User user, int storyId, int orderPosition) {
        String sql = "INSERT into \"StoryUser\" (\"StoryId\", \"UserId\", \"OrderPosition\", \"IsStoryOwner\", \"IsMyTurn\") VALUES (?, ?, ?, ?, ?);";

        getJdbcTemplate().update(sql, storyId, getUserIdFromUsername(user.getUsername()), orderPosition, user.isOwner(), user.isMyTurn());
    }

    /**
     *
     * @param word The word to add
     * @param storyId The story Id of where the word is being added
     */
    public void addWordToStory(Word word, int storyId) {

        String sqlToGetNextOrderPosition = "SELECT \"OrderInStory\" from \"Word\" where \"StoryId\"=? order by \"OrderInStory\"::int DESC LIMIT 1";
        int mostRecentWordOrder = 0;
        try {
            mostRecentWordOrder = Integer.parseInt(getJdbcTemplate().queryForObject(sqlToGetNextOrderPosition, new Object[]{storyId}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getString("OrderInStory");
                }
            }));
        }
        catch(EmptyResultDataAccessException e) {
            e.printStackTrace();
            System.out.println("There are no words yet for this story, so this must be the first one.");
        }
        String orderOfNewWord = Integer.toString(mostRecentWordOrder + 1);

        String sql = "INSERT into \"Word\" (\"StoryId\", \"UserId\", \"Word\", \"OrderInStory\", \"DateAdded\") VALUES (?, ?, ?, ?, current_timestamp)";
        getJdbcTemplate().update(sql, storyId, getUserIdFromUsername(word.getUserWhoAddedIt().getUsername()), word.getWordValue(), orderOfNewWord);


        //Now it is time to update whose turn it is next
        //First, I do a check to see if there is anyone whose position is above the current guy's turn. If so, I just make it that guys turn. If not, I just make it the first guys turn again.
        String sqlNullnessCheck = "SELECT \"StoryUserId\" from \"StoryUser\" where \"StoryId\"=? and \"IsMyTurn\"=false " +
                "and \"OrderPosition\"= 1 + (SELECT \"OrderPosition\" from \"StoryUser\" where \"IsMyTurn\"=true and \"StoryId\"=?) order by \"OrderPosition\" LIMIT 1";
        List<Map<String,Object>> results = getJdbcTemplate().queryForList(sqlNullnessCheck, storyId, storyId);
        System.out.println("The result size for the nullness check was " + results.size());
        if(results.size() == 0) {
            String sqlToLoopBack1 = "UPDATE \"StoryUser\" SET \"IsMyTurn\"=false where \"StoryId\"=? and \"IsMyTurn\"=true";
            String sqlToLoopBack2 = "UPDATE \"StoryUser\" SET \"IsMyTurn\"=true where \"StoryId\"=? and \"StoryUserId\"=(SELECT \"StoryUserId\" from \"StoryUser\" where \"StoryId\"=? and \"IsMyTurn\"=false order by \"OrderPosition\" LIMIT 1)";

            getJdbcTemplate().update(sqlToLoopBack1, storyId);
            getJdbcTemplate().update(sqlToLoopBack2, storyId, storyId);
        }
        else {
            String sqlToGoToNext1 = "UPDATE \"StoryUser\" SET \"IsMyTurn\"=true where \"StoryId\"=? and \"StoryUserId\"=" +
                    "(SELECT \"StoryUserId\" from \"StoryUser\" where \"StoryId\"=? and \"IsMyTurn\"=false " +
                    "and \"OrderPosition\"= 1 + (SELECT \"OrderPosition\" from \"StoryUser\" where \"IsMyTurn\"=true and \"StoryId\"=?) order by \"OrderPosition\" LIMIT 1)";
            String sqlToGoToNext2 = "UPDATE \"StoryUser\" SET \"IsMyTurn\"=false where \"StoryId\"=? and \"StoryUserId\"=" +
                    "(SELECT \"StoryUserId\" from \"StoryUser\" where \"IsMyTurn\"=true and \"StoryId\"=? order by \"OrderPosition\" LIMIT 1)";

            getJdbcTemplate().update(sqlToGoToNext1, storyId, storyId, storyId);
            getJdbcTemplate().update(sqlToGoToNext2, storyId, storyId);
        }
    }

    /**
     *
     * @param storyId The Id of the story to get returned
     * @return The requested story
     */
    public Story getSpecificStory(final int storyId) {

        final Story storyToReturn = new Story();
        storyToReturn.setIsAllFinished(false);


        String sqlToGetStoryData = "SELECT * from \"Story\" where \"StoryId\"=?";
        getJdbcTemplate().queryForObject(sqlToGetStoryData, new Object[]{storyId}, new RowMapper<Object>() {
            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                storyToReturn.setTitle(resultSet.getString("Title"));
                storyToReturn.setDateFinished(resultSet.getDate("DateFinished"));
                storyToReturn.setDateStarted((resultSet.getDate("DateStarted")));
                storyToReturn.setId(Integer.toString(resultSet.getInt("StoryId")));
                return null;
            }
        });

        final List<Word> words = new ArrayList<>();
        String sqlToGetWords = "SELECT * from \"Word\" where \"StoryId\"=? order by \"OrderInStory\"::int";
        final String sqlToGetMoreWordData = "SELECT * from \"StoryUser\" where \"StoryId\"=? and \"UserId\"=?";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sqlToGetWords, storyId);

        for(Map row : rows) {

            final Word word = new Word();
            word.setDateAdded((Date)row.get("DateAdded"));
            word.setWordValue((String)row.get("Word"));
            long userId = (Long)row.get("UserId");
            word.setUserWhoAddedIt(getUserFromId(userId));

            getJdbcTemplate().queryForObject(sqlToGetMoreWordData, new Object[]{storyId, userId}, new RowMapper<Object>() {
                @Override
                public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                    word.getUserWhoAddedIt().setOrderPosition(resultSet.getInt("OrderPosition"));
                    word.getUserWhoAddedIt().setIsMyTurn(resultSet.getBoolean("IsMyTurn"));
                    if(word.getUserWhoAddedIt().isMyTurn()) {
                        storyToReturn.setWhoseTurn(word.getUserWhoAddedIt());
                    }
                    word.getUserWhoAddedIt().setIsOwner(resultSet.getBoolean("IsStoryOwner"));
                    if(word.getUserWhoAddedIt().isOwner()) {
                        storyToReturn.setOwner(word.getUserWhoAddedIt());
                    }

                    return null;
                }
            });

            //If whoever's turn it is has never added a word, they won't be found in the up above query through the words
            if(storyToReturn.getWhoseTurn() == null) {
                String sqlToGetWhoseTurn = "SELECT * from \"StoryUser\" where \"StoryId\"=? and \"IsMyTurn\"=true";
                getJdbcTemplate().queryForObject(sqlToGetWhoseTurn, new Object[]{storyId}, new RowMapper<Object>() {
                    @Override
                    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                        storyToReturn.setWhoseTurn(getUserFromId(resultSet.getInt("UserId")));
                        return null;
                    }
                });
            }

            words.add(word);
        }
        storyToReturn.setWords(words);

        return storyToReturn;
    }

    /**
     *
     * @param username The username of the current user
     * @return A list of 'it's your turn' stories
     */
    public List<StorySummary> getYourTurnStories(String username) {

        String sql = "SELECT * from \"Story\" where \"StoryId\" IN (SELECT \"StoryId\" from \"StoryUser\" where \"UserId\"=? and \"IsMyTurn\"=true)";

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, getUserIdFromUsername(username));

        List<StorySummary> stories = new ArrayList<>();
        for(Map row : rows) {
            StorySummary story = new StorySummary();
            story.setId(Long.toString((Long)row.get("StoryId")));
            story.setTitle((String)row.get("Title"));
            story.setNumberOfPlayers((Integer)row.get("NumberOfPlayers"));
            stories.add(story);
        }

        return stories;
    }

    /**
     *
     * @param username The username of the current user
     * @return A list of finished stories that this user was an owner of
     */
    public List<StorySummary> getPastStories(String username) {
        String sql = "SELECT * from \"Story\" where \"DateFinished\" IS NOT NULL and \"StoryId\" IN (SELECT \"StoryId\" from \"StoryUser\" where \"UserId\"=? and \"IsStoryOwner\"=true)";

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, getUserIdFromUsername(username));

        List<StorySummary> stories = new ArrayList<>();
        for(Map row : rows) {
            StorySummary story = new StorySummary();
            story.setId(Long.toString((Long)row.get("StoryId")));
            story.setTitle((String)row.get("Title"));
            story.setNumberOfPlayers((Integer)row.get("NumberOfPlayers"));
            stories.add(story);
        }

        return stories;
    }

    /**
     *
     * @param username The username of the current user
     * @return A list of stories that are unfinished and that this user is an owner of
     */
    public List<StorySummary> getUnfinishedStories(String username) {
        String sql = "SELECT * from \"Story\" where \"DateFinished\" IS NULL and \"StoryId\" IN (SELECT \"StoryId\" from \"StoryUser\" where \"UserId\"=? and \"IsStoryOwner\"=true)";
        String sqlToGetWhoseTurn = "SELECT \"UserId\" from \"StoryUser\" where \"StoryId\"=? and \"IsMyTurn\"=true";

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, getUserIdFromUsername(username));

        List<StorySummary> stories = new ArrayList<>();
        for(Map row : rows) {
            StorySummary story = new StorySummary();
            long storyId = (Long)row.get("StoryId");
            story.setId(Long.toString(storyId));
            story.setTitle((String) row.get("Title"));
            story.setNumberOfPlayers((Integer) row.get("NumberOfPlayers"));
            story.setWhoseTurn(getJdbcTemplate().queryForObject(sqlToGetWhoseTurn, new Object[]{storyId}, new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet resultSet, int i) throws SQLException {
                    return getUserFromId(resultSet.getInt("UserId"));
                }
            }));
            stories.add(story);
        }

        return stories;
    }

    /**
     *
     * @param storyId The Id of the story that is to be finished
     */
    public void finishStory(int storyId) {
        String sql = "UPDATE \"Story\" SET \"DateFinished\"=current_timestamp WHERE \"StoryId\"=?";

        getJdbcTemplate().update(sql, storyId);
    }

    /**
     *
     * @param username The entered username
     * @param enteredPassword The entered password
     * @return Whether the username and password are correct or not
     */
    public User login(String username, String enteredPassword) {
        String sql = "SELECT * from \"AppUser\" where \"Username\"=?";

        boolean valid = false;
        final User user = new User();

        try {
            String password = getJdbcTemplate().queryForObject(sql, new Object[]{username}, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet resultSet, int i) throws SQLException {
                    user.setUsername(resultSet.getString("Username"));
                    user.setDisplayName(resultSet.getString("DisplayName"));

                    return resultSet.getString("Password");
                }
            });

            if(password.equals(enteredPassword)) {
                valid = true;
            }
            else {
                System.out.println("The password doesn't match.");
            }
        }
        catch(EmptyResultDataAccessException e) {
            e.printStackTrace();
            System.out.println("The username does not exist in the database");
        }

        if(valid) {
            return user;
        }
        else {
            return null;
        }
    }

    public boolean usernameExists(String username) {

        boolean exists = true;

        String sql = "SELECT \"UserId\" from \"AppUser\" where \"Username\" = ?";

        try {
            int userId = getJdbcTemplate().queryForObject(sql, new Object[]{username}, new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getInt("UserId");
                }
            });

            System.out.println("Foudn user with id of " + userId);
        }
        catch(EmptyResultDataAccessException e) {
            exists = false;
            e.printStackTrace();
            System.out.println("The user " + username + " doesn't exist.");
        }

        return exists;
    }

    /**
     *
     * @param username The username of the user whose Id is wanted
     * @return The Id of the user
     */
    private int getUserIdFromUsername(String username) {
        String sql = "SELECT \"UserId\" from \"AppUser\" where \"Username\" = ?";

        return getJdbcTemplate().queryForObject(sql, new Object[]{username}, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt("UserId");
            }
        });
    }

    /**
     *
     * @param userId The user Id of the user whose user information
     * @return The user's information
     */
    private User getUserFromId(long userId) {
        String sql = "SELECT * from \"AppUser\" where \"UserId\"=?";

        return getJdbcTemplate().queryForObject(sql, new Object[]{userId}, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setUsername(resultSet.getString("Username"));
                user.setDisplayName(resultSet.getString("DisplayName"));
                return user;
            }
        });
    }




    /*private class CustomerRowMapper implements RowMapper
    {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setCustId(rs.getInt("CUST_ID"));
            customer.setName(rs.getString("NAME"));
            customer.setAge(rs.getInt("AGE"));
            return customer;
        }

    }*/

}
