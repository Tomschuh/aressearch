package cz.agentes.aressearch.threads;

import cz.agentes.aressearch.domain.Company;
import cz.agentes.aressearch.event.OnStartApplicationEvent;
import cz.agentes.aressearch.repository.CompanyRepository;
import cz.agentes.aressearch.service.CompanyService.ParseCompanyIn;
import cz.agentes.aressearch.utils.AresSearchHandler;
import cz.agentes.aressearch.utils.StaxXMLReader;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.parsers.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ParseCompanyCallable implements Callable<List<Company>> {

    private final File[] files;
    private final String name;
    private static final Logger logger = LoggerFactory.getLogger(OnStartApplicationEvent.class);


    public ParseCompanyCallable(File[] files, String name) {
        this.files = files;
        this.name = name;
    }

    @Override
    public List<Company> call() throws Exception {

        List<Company> companies = new ArrayList<>();
        int progress = 0;

        for (File file : files) {
            try {

                //companies.add(createCompany(StaxXMLReader.parseXml(file)));
                Company company = createCompany(StaxXMLReader.parseXml(file));
                if (company.getIco() == null) {
                    company.setIco(FilenameUtils.removeExtension(file.getName()));
                }
                companies.add(company);
                if (progress % 10000 == 0) {
                    logger.info(String.format("Parsing company callable name:%s (%s of %s)", name, progress, files.length));
                }
                progress++;
            } catch (Exception e) {
                logger.error(String.valueOf(e.getStackTrace()));
            }
        }

        return companies;
    }

    // saxParser.parse(new InputSource(new StringReader(xml)), handler);

    //~ Private methods

    private Company createCompany(@Valid ParseCompanyIn in) {
        Company company = new Company();
        company.setIco(in.ico);
        company.setName(in.name);
        company.setStreet(in.street);
        company.setNumber(in.number);
        company.setCity(in.city);

        return company;
    }
}
