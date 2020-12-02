package cz.agentes.aressearch.service.impl;

import cz.agentes.aressearch.domain.Company;
import cz.agentes.aressearch.event.OnStartApplicationEvent;
import cz.agentes.aressearch.manager.CompanyManager;
import cz.agentes.aressearch.mappers.CompanyMappers.GetCompanyListOutMapper;
import cz.agentes.aressearch.repository.CompanyRepository;
import cz.agentes.aressearch.service.CompanyService;
import cz.agentes.aressearch.threads.ParseCompany;
import cz.agentes.aressearch.utils.StaxXMLReader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static cz.agentes.aressearch.utils.MessageKeys.*;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final @NonNull CompanyRepository companyRepository;
    private final @NonNull CompanyManager companyManager;
    private static final Logger logger = LoggerFactory.getLogger(OnStartApplicationEvent.class);


    @Value("${ares-search.url.base}")
    private String aresUrlBase;

    @Value("${ares-search.url.all}")
    private String aresUrlAll;

    @Value("${ares-search.filedb.root-paths.download-files}")
    private String downloadDir;

    @Value("${ares-search.filedb.root-paths.xml-files}")
    private String xmlDir;

    @Override
    public void aresSearchInitialization() throws Exception {
        // Check if solr is empty
        if (companyRepository.count() == 0) {
            logger.info("Ares search initialization started at: " + LocalDateTime.now());
            // Download all companies
            //companyManager.downloadAllCompanies();
            // Index all companies
            companyManager.indexAllCompaniesV1();
            logger.info("Ares search initialization finished at: " + LocalDateTime.now());
            //companyManager.logLastChanges(LocalDateTime.now());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetCompanyItemOut> getSearchSuggestions(String searchExpression, Integer numberOfSearchSuggestion) {
        Assert.isTrue(searchExpression != null && !searchExpression.isBlank(), SERVICE_COMPANY_NULL_OR_BLANK_SEARCH_EXPRESION);
        Assert.notNull(numberOfSearchSuggestion, SERVICE_COMPANY_NULL_OBJECT_NUMBER_OF_SEARCH_SUGGESTION);

        List<Company> companies = companyRepository.findSearchSuggestions(searchExpression, PageRequest.of(0, numberOfSearchSuggestion));

        return GetCompanyListOutMapper.fromCompanyList(companies);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCompanyDetailsOut getCompanyDetails(String ico) {
        Assert.isTrue(ico != null && !ico.isBlank(), SERVICE_COMPANY_NULL_OR_BLANK_ICO);

        // Get xml file
        File file = new File(xmlDir + "/VYSTUP/DATA/" + ico + ".xml");
        if (!file.exists()) {
            throw new IllegalArgumentException(SERVICE_COMPANY_XML_DOCUMENT_NOT_FOUND);
        }

        GetCompanyDetailsOut out = new GetCompanyDetailsOut();
        try {
            out = StaxXMLReader.parseFullXML(file);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return out;
    }

    @Override
    public void updateCompanies() {
        // Check last changes in?

//        try {
//            companyManager.downloadChanges();
//            List<String> lastChanges = companyManager.getLastChanges();
////            for (String ico : lastChanges) {
////
////            }
//            if (lastChanges != null && lastChanges.size() != 0) {
//                for (int i = 0; i <= 50; i++) {
//                    logger.info(String.format("Updating company (ico: %s) %s from %s", lastChanges.get(i), i, lastChanges.size()));
//                    Company company = companyRepository.findCompanyByIco(lastChanges.get(i));
//                    companyManager.updateCompanyFrom(company);
//                }
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }

        ParseCompanyIn parseCompanyIn = StaxXMLReader.parseXml(new File(xmlDir + "/VYSTUP/DATA/00000124.xml"));

        System.out.println(parseCompanyIn.name);
    }
}