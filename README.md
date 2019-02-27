# NewsScraper

Requirements
1) Running solr with article core
if core not availanle create with the command \n
cd solr-version/bin\n
solr create -c article

2)Java 10 or higher
3) maven

To run

Goto the project directory and type 
mvn spring-boot:run


API's:
1) to start index the article
GET http://host:port/NewsScraper/doScraping

2) Search available authors
GET http://host:port/NewsScraper/authors?name=<search string>

3) Search articles based on author name
GET http://host:port/NewsScraper/articles/<author name>

4)Search articles based on article title and description
GET http://host:port/NewsScraper/articles/<title text>/<body string>
