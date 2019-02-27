package com.ramesh.news;



import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.stereotype.Component;

import com.ramesh.news.util.NewsScraperUtil;


@Configuration
@EnableSolrRepositories(basePackages = { "com.ramesh.news.repository" })
@Component
public class NewsScaraperConfig {

	@Value("${spring.data.solr.host}")
	String solrURL;
	
	@Bean
	public SolrClient solrClient() {
		return new HttpSolrClient.Builder(solrURL).build();
	}
	@Bean
	public SolrTemplate solrTemplate(SolrClient client) throws Exception {
		return new SolrTemplate(client);
	}
	
	@Bean
	public NewsScraperUtil newsScraperUtil() {
		return new NewsScraperUtil();
	}

	

	
}
