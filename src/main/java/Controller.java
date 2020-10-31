import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Controller
{
	public static void main(String[] args)
	{
		Scraper.scrape();
		
//		ApiContextInitializer.init();
//
//		TelegramBotsApi botsApi = new TelegramBotsApi();
//
//		try
//		{
//			botsApi.registerBot(new Bot());
//		}
//		catch (TelegramApiException e)
//		{
//			e.printStackTrace();
//		}
//
//		DatabaseManager.test();
	}
}
