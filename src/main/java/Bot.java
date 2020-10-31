import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.CompletableFuture;

public class Bot extends TelegramLongPollingBot
{
	@Override
	public void onUpdateReceived(Update update)
	{
		// Check if the update has a message and the message has text
		if (update.hasMessage() && update.getMessage().hasText())
		{
			String userMessage = update.getMessage().getText();
			User user = update.getMessage().getFrom();
			Long chatID = update.getMessage().getChatId();
			
			String reply = "_";
			
			if(userMessage.equals("/start"))
			{
				reply = "Welcome, " + user.getFirstName();
				
				// Performing async DB request (sync causes the reply to be sent late)
				CompletableFuture.supplyAsync(() -> DatabaseManager.addUser(chatID, user.getFirstName()));
			}
			
			SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
					.setChatId(update.getMessage().getChatId())
					.setText(reply);
			try
			{
				execute(message);
			}
			catch (TelegramApiException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String getBotUsername()
	{
		return System.getenv("BOT_USERNAME");
	}
	
	@Override
	public String getBotToken()
	{
		return System.getenv("BOT_TOKEN");
	}
}