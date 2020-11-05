import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Controller
{
	static final Long SCRAPE_PERIOD = 3600 * 1000L; // after this many ms the program will check for new internships
	static final Long CLEAN_PERIOD = 10800 * 1000L; // after this many ms the program will check trigger a query that will reduce the number of rows in posts table.
	static final int POST_TABLE_THRESHOLD = 10; // the maximum number of posts that can be stored in DB
	static final int POSTS_TO_DELETE = 3; // the number of posts to delete if the number of posts in DB exceeds the limit.
	
	public static void main(String[] args)
	{
		ApiContextInitializer.init();
		TelegramBotsApi botsApi = new TelegramBotsApi();
		Bot myBot = new Bot();
		
		try
		{
			botsApi.registerBot(myBot);
		}
		catch (TelegramApiException e)
		{
			e.printStackTrace();
		}
		
		configureRepeatableTasks(myBot);
	}
	
	/**
	 * Configures tasks that are executed periodically.
	 * @param bot the bot for which the tasks should be configured
	 */
	private static void configureRepeatableTasks(Bot bot)
	{
		Timer timer = new Timer("Timer");
		
		TimerTask scraperTask = new TimerTask() {
			public void run() {
				System.out.println("Scraping performed on: " + new Date());
				
				ArrayList<Post> posts = Scraper.scrape();
				DatabaseManager.addPosts(posts);
				
				posts = DatabaseManager.getNewPosts();
				
				for (Post post: posts)
				{
					bot.sendToAll(post.toMessageString());
				}
			}
		};
		
		TimerTask cleanerTask = new TimerTask() {
			public void run() {
				System.out.println("Cleaning performed on: " + new Date());
				DatabaseManager.deleteOldPosts(POST_TABLE_THRESHOLD, POSTS_TO_DELETE);
			}
		};
		
		timer.scheduleAtFixedRate(scraperTask, 0, SCRAPE_PERIOD);
		timer.scheduleAtFixedRate(cleanerTask, CLEAN_PERIOD, CLEAN_PERIOD);
	}
}
