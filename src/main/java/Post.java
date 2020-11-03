public class Post
{
	private String title;
	private String url;
	private String content;
	
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
}
