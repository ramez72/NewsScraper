package com.ramesh.news.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.websocket.server.PathParam;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ramesh.news.bean.Article;
import com.ramesh.news.repository.SolrArticleRepository;
import com.ramesh.news.util.NewsScraperUtil;
import com.ramesh.news.util.NewsScraperUtil.Status;

@RestController
@RequestMapping("NewsScraper")
public class NewsScraperService {
	
	Logger logger= Logger.getLogger(this.getClass().getName());
	
	@Autowired
	NewsScraperUtil newsScraperUtil;
	
	@Autowired
    SolrArticleRepository solrArticleRepository;

	
	@GetMapping("/doScraping")
	public Status doScraping() throws IOException {
		
		CompletableFuture.supplyAsync(() -> {
			  try {
				newsScraperUtil.startScraping();
			} catch (IOException e) {
				
			}
			  return NewsScraperUtil.currentScrapingStatus;
		  });
		
		 return NewsScraperUtil.currentScrapingStatus;

		
	}
	
	
	@GetMapping("/authors")
	public List<String> searchAuthors(@RequestParam(name="name", defaultValue="") String name) {
		logger.info("searchAuthors, indexing status= "+NewsScraperUtil.currentScrapingStatus+" indexed Article Count= "+NewsScraperUtil.indexedArticleCount);
		FacetPage<Article> facetPages =solrArticleRepository.searchAuthors(name, PageRequest.of(1, 100));
		 return facetPages.getContent().stream().map(facetPage->facetPage.getAuthor()).distinct().collect(Collectors.toList());

	}
	
	@GetMapping("/articles/{name}")
	public List<String> findByAuthor(@PathParam(value="name") String author){
		logger.info("findByAuthor, indexing status= "+NewsScraperUtil.currentScrapingStatus+" indexed Article Count= "+NewsScraperUtil.indexedArticleCount);
		FacetPage<Article> facetPages =solrArticleRepository.findByAuthorIgnoreCase(author, PageRequest.of(1, 100));
		 return facetPages.getContent().stream().map(facetPage->facetPage.getUrl()).collect(Collectors.toList());
	}
	
	@GetMapping("/articles/{title}/{desc}")
	public List<String> findByTitleAndDescription(@PathParam(value="title") String author, @PathParam(value="desc") String desc){
		logger.info("findByTitleAndDescription, indexing status= "+NewsScraperUtil.currentScrapingStatus+" indexed Article Count= "+NewsScraperUtil.indexedArticleCount);
		FacetPage<Article> facetPages =solrArticleRepository.findByTitleAndDescriptionAllIgnoreCase(author, desc,  PageRequest.of(1, 100));
		 return facetPages.getContent().stream().map(facetPage->facetPage.getUrl()).collect(Collectors.toList());
	}
	
	

}
