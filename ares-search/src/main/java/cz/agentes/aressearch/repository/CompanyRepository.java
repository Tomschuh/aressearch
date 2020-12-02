package cz.agentes.aressearch.repository;

import cz.agentes.aressearch.domain.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

public interface CompanyRepository extends SolrCrudRepository<Company, String> {


    @Query(value = "ico:?0* OR name:?0*")
    List<Company> findSearchSuggestions(String searchTerm, Pageable pageable);

    @Query(value = "ico:?0")
    Company findCompanyByIco(String ico);
}
