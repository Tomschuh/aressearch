package cz.agentes.aressearch.service;

import cz.agentes.aressearch.domain.Company;
import org.xml.sax.SAXException;

import javax.validation.constraints.NotNull;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CompanyService {

    /**
     * Initialization of ares search.
     * Downloads all companies from ares, then save, compress and parse.
     * After parsing index into solr using an wrapper object {@link Company}.
     *
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    void aresSearchInitialization() throws Exception;

    /**
     * Get search suggestions according to param searchExpresion.
     *
     * @param searchExpression contains part of word which user what to search.
     * @param numberOfSearchSuggestion contains information about how many search suggestion will be returned.
     * @return list of search suggestion of {@link Company} in wrapper object {@link GetCompanyItemOut}.
     */
    List<GetCompanyItemOut> getSearchSuggestions(String searchExpression, Integer numberOfSearchSuggestion);

    /**
     * Get company details by given ico.
     * Parse and return company from xml files by given ico.
     *
     * @param ico identifies which xml file should be parsed and returned.
     * @return company details in wrapper object {@link GetCompanyDetailsOut}.
     */
    GetCompanyDetailsOut getCompanyDetails(String ico);

    /**
     * Update companies by last changes.
     *
     * Download companies changes and unzip, then download all changes and index them into solr.
     * Delete old xml and save new one.
     */
    void updateCompanies();

    class ParseCompanyIn {
        @NotNull
        public String ico;
        @NotNull
        public String name;
        public String city;
        public String street;
        public String number;
    }

    class GetCompanyItemOut {
        public String name;
        public String ico;
        public String street;
        public String city;
        public String number;
    }

    class GetCompanyDetailsOut {
        public String name;
        public String ico;
        public AddressOut address;
        public List<MemberOut> memberList;

        public static class AddressOut {
            public String state;
            public String zip_code;
            public String city;
            public String street;
            public String number;
        }

        public static class MemberOut {
            public String firstName;
            public String lastName;
            public String degreeBefore;
            public String degreeAfter;
            public String role;
        }
    }
}
