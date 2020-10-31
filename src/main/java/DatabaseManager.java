import java.sql.*;

public class DatabaseManager
{
	private static final String CONNECTION_URL = System.getenv("DB_URL");
	private static final String USERNAME = System.getenv("DB_USERNAME");
	private static final String PASSWORD = System.getenv("DB_PASSWORD");
	
	private static final String USERS_TABLE_NAME = "users";
	
	public static void test()
	{
		String query = "SELECT * FROM users";
		
		try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
		     PreparedStatement ps = conn.prepareStatement(query);
		     ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				String name = rs.getString("FIRST_NAME");
				System.out.println(name);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean addUser(Long chatID, String firstName)
	{
		String query = "INSERT INTO " + USERS_TABLE_NAME +" VALUES (" + chatID + ", '" + firstName + "');";
		
		try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
		     PreparedStatement ps = conn.prepareStatement(query))
		{
			ps.executeUpdate();
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean deleteUser(Long chatID) //TODO call this when error 403 is detected
	{
		String query = "DELETE FROM " + USERS_TABLE_NAME +" WHERE chat_id = " + chatID;
		
		try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
		     PreparedStatement ps = conn.prepareStatement(query))
		{
			ps.executeUpdate();
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
