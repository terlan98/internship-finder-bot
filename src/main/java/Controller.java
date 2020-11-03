import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Controller
{
	static final Long period = 3600 * 1000L; // after this many ms the program will check for new internships
	
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
	
	private static void configureRepeatableTasks(Bot bot)
	{
		TimerTask repeatedTask = new TimerTask() {
			public void run() {
				System.out.println("Scraping performed on " + new Date());
				
				ArrayList<Post> posts = Scraper.scrape();
				DatabaseManager.addPosts(posts);
				
				posts = DatabaseManager.getNewPosts();
				
				for (Post post: posts)
				{
					bot.sendToAll(post.getUrl());
				}
			}
		};
		Timer timer = new Timer("Timer");
		
		timer.scheduleAtFixedRate(repeatedTask, 0, period);
	}
}
