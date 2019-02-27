package com.ramesh.news.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.repository.Facet;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.data.solr.repository.Stats;

import com.ramesh.news.bean.Article;



public interface SolrArticleRepository extends SolrCrudRepository<Article, String> {
	
	
	@Query("author:*?0*")
	@Facet(fields= {"author"})
	public FacetPage<Article> searchAuthors(String name, Pageable pageable);
	
	@Facet(fields= {"url"})
	public FacetPage<Article> findByAuthorIgnoreCase(String author, Pageable pageable);
	
	
	@Facet(fields= {"url"})
	public FacetPage<Article> findByTitleAndDescriptionAllIgnoreCase(String title, String description, Pageable pageable);
	
}