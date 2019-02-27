package com.ramesh.news.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "article")
public class Article {
	
	public Article() {
		// TODO Auto-generated constructor stub
	}
	
	public Article(String url, String auther, String title, String desc) {
		// TODO Auto-generated constructor stub
	}
	
	
    @Id
    @Indexed(name = "url", type = "string")
	private String url;
    
    @Indexed(name = "author", type = "string")
	private String author;
	
    @Indexed(name = "title", type = "string")
    private String title;
    
    @Indexed(name = "desc", type = "string")
	private String description;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String uRL) {
		url = uRL;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
