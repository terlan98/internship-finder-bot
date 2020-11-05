public class Post
{
	private final String title;
	private final String url;
	private final String content;
	
	public Post(String title, String url, String content)
	{
		this.title = title;
		this.url = url;
		this.content = content;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public String getContent()
	{
		return content;
	}
	
	@Override
	public String toString()
	{
		return "Post{" +
				"title='" + title + '\'' +
				", url='" + url + '\'' +
				", content='" + content + '\'' +
				'}';
	}
	
	/**
	 * Returns a string that is formatted in a way that can be understood by the users.
	 */
	public String toMessageString()
	{
		String result = "";
		
		result += "<b>Başlıq: </b>" + getTitle();
		result += "\n\n<b>Məlumat: </b>" + getContent();
		result += "\n<b>Link: </b>" + getUrl();
		
		return result;
	}
}
