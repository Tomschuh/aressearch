package cz.agentes.aressearch;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@Configuration
@EnableSolrRepositories(
        basePackages = "cz.agentes.aressearch.repository")
//        namedQueriesLocation = "classpath:solr-named-queries.properties")
@ComponentScan
public class Config {

    @Value("${spring.data.solr.host}")
    private String solrURL;

    @Bean
    public SolrClient solrClient() {
        return new HttpSolrClient.Builder(solrURL).build();
    }

    @Bean
    public SolrTemplate solrTemplate(SolrClient client) {
        return new SolrTemplate(client);
    }
}