package com.ramesh.news.util;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.ramesh.news.NewsScraperApplication;
import com.ramesh.news.bean.Article;
import com.ramesh.news.repository.SolrArticleRepository;

public class NewsScraperUtil {
	
	Logger logger= Logger.getLogger(this.getClass().getName());

	private final static String URL = "https://www.thehindu.com/archive/";


	public enum Status{
		IDLE,
		RUNNING,
		DONE,
		FAILED
	};
	
	public static volatile int indexedArticleCount=0;
	
	public static Status currentScrapingStatus = Status.IDLE;
	
	private SolrArticleRepository solrArticleRepository;
	


	private Elements getMonthWiseURLElements() throws IOException {
		var doc = Jsoup.connect(URL).get();
		var elements = doc.select("#archiveWebContainer");
		if(elements.size()==0)
			throw new IOException("Web archive not found");
		return elements.first().select("a[href]");
	}

	private Elements getDateElementsForMonth(Element monthEle) {
		Elements dateURLElements=null;
		//logger.log(Level.INFO,  "reading "+monthEle.attr("href"));
		try {
			var dateDocument = Jsoup.connect(monthEle.attr("href")).get();
			dateURLElements = dateDocument.select("a.ui-state-default");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "getDateElementsForMonth "+e.getMessage()+" "+monthEle.attr("href"));
		}
		return dateURLElements;
	}

	private Elements getArticleURLElementsForDate(Element dateURLEle) {
		Elements articleCategoryElements=null;
		var articleURLElements = new Elements();
		//logger.log(Level.INFO,  "reading "+dateURLEle.attr("href"));
		try {
			var articlesDocument = Jsoup.connect(dateURLEle.attr("href")).get();
			articleCategoryElements = articlesDocument.select("ul.archive-list");
			articleCategoryElements.forEach(articleCategoryElement->{
				articleCategoryElement.select("a[href]").forEach(articleURLElement->{
					articleURLElements.add(articleURLElement);
				});
				
			}); 
		} catch (IOException e) {
			logger.log(Level.SEVERE, " getArticleURLElementsForDate "+e.getMessage()+" "+dateURLEle.attr("href"));
		}
		return articleURLElements;
	}

	
	private Article getArticle(Element articleURLEle) {
		
		Article article=new Article();
		article.setUrl(articleURLEle.attr("href"));
		//logger.log(Level.INFO,  "reading "+article.getUrl());
		article.setTitle(articleURLEle.text());
		
		try {
			var articleDoc = Jsoup.connect(article.getUrl()).get();
			var authorElements = articleDoc.select(".auth-nm");
			if(authorElements!=null && authorElements.size()>0) {
				article.setAuthor(authorElements.first().text());
			}
			
			var articleBody = articleDoc.select("div[id^=\"content-body\"]");
			
			var description = new StringBuilder();
			if(articleBody!=null && articleBody.size()>0) {
				articleBody.first().getAllElements().forEach(a->{
					description.append(a.text());
				});
				article.setDescription(description.toString());
			}
			
		} catch (IOException e) {
			logger.log(Level.SEVERE,  e.getMessage()+" "+article.getUrl());
		}
		return article;
		
	}
	
	public void startScraping() throws IOException {
		
		if(currentScrapingStatus == Status.RUNNING)
			return;
		currentScrapingStatus = Status.RUNNING;

		var urls = getMonthWiseURLElements();

		urls.stream().forEach(monthEle->{
			var dateURLElements = getDateElementsForMonth(monthEle);
			
			if(dateURLElements !=null && dateURLElements.size()>0) {
				
				dateURLElements.forEach(dateURLEle->{
					var articleURLElements = getArticleURLElementsForDate(dateURLEle);
					if(articleURLElements!=null && articleURLElements.size()>0) {
						
						articleURLElements.forEach(articleURLEle->{
							var article = getArticle(articleURLEle);
							try {
								solrArticleRepository.save(article);
								indexedArticleCount++;
								//logger.log(Level.INFO,  "indexed "+indexedArticleCount);
							}catch (Exception e) {
								
								logger.log(Level.SEVERE,  e.getMessage()+" issue with indexing article "+article.getUrl()+" \n "+article.getAuthor()+" \n ");
							}
						});
					}
				});
			}
		});
		currentScrapingStatus = Status.DONE;

	}

	@Autowired
	public void setProductRepository(SolrArticleRepository solrArticleRepository) {
		this.solrArticleRepository = solrArticleRepository;
	}



}






