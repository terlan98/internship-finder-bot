import java.sql.*;
import java.util.ArrayList;

/**
 * Handles all database-related tasks.
 */
public class DatabaseManager
{
	private static final String CONNECTION_URL = System.getenv("DB_URL");
	private static final String USERNAME = System.getenv("DB_USERNAME");
	private static final String PASSWORD = System.getenv("DB_PASSWORD");
	
	private static final String USERS_TABLE_NAME = "users";
	private static final String POSTS_TABLE_NAME = "posts";
	
	public static ArrayList<Long> getChatIDs()
	{
		String query = "SELECT chat_id FROM users";
		ArrayList<Long> chatIDs = new ArrayList<>();
		
		try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
		     PreparedStatement ps = conn.prepareStatement(query);
		     ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				Long chatID = rs.getLong("CHAT_ID");
				chatIDs.add(chatID);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return chatIDs;
	}
	
	/**
	 * Adds a new user to the DB.
	 *
	 * @param chatID
	 * @param firstName
	 * @return
	 */
	public static boolean addUser(Long chatID, String firstName)
	{
		String query = "INSERT INTO " + USERS_TABLE_NAME + " VALUES (" + chatID + ", '" + firstName + "');";
		
		return executeDML(query);
	}
	
	/**
	 * Deletes the user with the specified chat id.
	 *
	 * @param chatID
	 * @return
	 */
	public static boolean deleteUser(Long chatID)
	{
		String query = "DELETE FROM " + USERS_TABLE_NAME + " WHERE chat_id = " + chatID;
		
		return executeDML(query);
	}
	
	/**
	 * Returns the posts that have not been sent to the users.
	 * This function is not idempotent. It marks the posts as 'sent' in DB before returning the result.
	 */
	public static ArrayList<Post> getNewPosts()
	{
		ArrayList<Post> posts = new ArrayList<>();
		String query = "SELECT * FROM posts WHERE is_sent = false";
		
		try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
		     PreparedStatement ps = conn.prepareStatement(query);
		     ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				String title = rs.getString("TITLE");
				String url = rs.getString("URL");
				String content = rs.getString("CONTENT");
				
				posts.add(new Post(title, url, content));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		String updateSentFlagQuery = "UPDATE " + POSTS_TABLE_NAME + " SET is_sent = true";
		
		executeDML(updateSentFlagQuery);
		
		return posts;
	}
	
	/**
	 * Adds the posts from the given ArrayList to DB.
	 *
	 * @param posts
	 */
	public static void addPosts(ArrayList<Post> posts)
	{
		String query = "INSERT INTO " + POSTS_TABLE_NAME + "(url, title, content, is_sent) VALUES";
		String valuesString = "";
		
		for (int i = 0, size = posts.size(); i < size; i++)
		{
			Post post = posts.get(i);
			
			valuesString += "('" + post.getUrl() + "', '" + post.getTitle() + "', '" + post.getContent() + "', false)";
			
			if (i != size - 1)
			{
				valuesString += ",";
			}
		}
		
		query += valuesString + "ON DUPLICATE KEY UPDATE is_sent = is_sent;"; // ignores already existing values

		executeDML(query);
	}
	
	/**
	 * Deletes the specified number of posts from the bottom of the posts table iff the # of rows exceeds the given limit
	 * @param rowLimit the max number of rows that can be allowed in the posts table.
	 * @param numberOfPostsToDelete the number of posts to delete
	 */
	public static void deleteOldPosts(int rowLimit, int numberOfPostsToDelete)
	{
		String rowTestQuery = "SELECT IF(count(*) = " + rowLimit + ",'true','false') from " + POSTS_TABLE_NAME;
		
		try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
		     PreparedStatement ps = conn.prepareStatement(rowTestQuery);
		     ResultSet rs = ps.executeQuery())
		{
			rs.next();
			if(rs.getBoolean(1))
			{
				String deleteQuery = "DELETE FROM " + POSTS_TABLE_NAME + " ORDER BY id DESC LIMIT " + numberOfPostsToDelete;
				executeDML(deleteQuery);
			}
		}
		catch (SQLException e)
		{
			System.err.println("Couldn't delete old posts. Attempted deleting " + numberOfPostsToDelete + " with a row limit of " + rowLimit);
			e.printStackTrace();
		}
	}
	
	/**
	 * Executes the given DML query.
	 */
	private static boolean executeDML(String query)
	{
		try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
		     PreparedStatement ps = conn.prepareStatement(query))
		{
			ps.executeUpdate();
			return true;
		}
		catch (SQLException e)
		{
			System.err.println("Failed DML query: " + query);
			e.printStackTrace();
			return false;
		}
	}
}
