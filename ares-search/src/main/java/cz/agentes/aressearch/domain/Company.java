package cz.agentes.aressearch.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.io.File;
import java.lang.annotation.ElementType;

@Getter
@Setter
@SolrDocument(collection = "aressearch")
public class Company {

    @Id
    @Indexed
    private String id;

    @Indexed(name = "ico", required = true, type = "string")
    @Field
    private String ico;
    //@Indexed(name = "name", type = "string")
    @Indexed(name = "name", type = "text_cz", required = true)
    @Field
    private String name;
    @Indexed(name = "city", type = "text_general", searchable = false)
    @Field
    private String city;
    @Indexed(name = "street", type = "text_general", searchable = false)
    @Field
    private String street;
    @Indexed(name = "number", type = "text_general", searchable = false)
    @Field
    private String number;
}
