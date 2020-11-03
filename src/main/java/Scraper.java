import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Scraper
{
	public static ArrayList<Post> scrape()
	{
		ArrayList<Post> result = new ArrayList<>();
		
		try
		{
			Document doc = Jsoup.connect("https://edumap.az/category/təcrubə-proqramlari/").get();
			Elements posts = doc.getElementsByClass("post-title");
			
			for (Element post : posts) // clicking on each post here
			{
				String postLink = post.getElementsByTag("a").attr("href");
				Document postPage = Jsoup.connect(postLink).get();
				
				String postTitle = postPage.getElementsByClass("post-title entry-title").first().text();
				String postContent = postPage.getElementsByClass("entry-content entry clearfix").first().wholeText();
				
				// Removing extraneous new lines and tabs
				postContent = postContent.replaceAll("\\t|[\\n]{2,}","");
				
				System.out.println(postLink);
//				System.out.println(postTitle + ":\n" + postContent + "\n-----------------------\n\n");
				
				result.add(new Post(postTitle, postLink, postContent));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
}
