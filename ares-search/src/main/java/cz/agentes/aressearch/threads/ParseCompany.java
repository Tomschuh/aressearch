package cz.agentes.aressearch.threads;

import cz.agentes.aressearch.domain.Company;
import cz.agentes.aressearch.repository.CompanyRepository;
import cz.agentes.aressearch.service.CompanyService;
import cz.agentes.aressearch.utils.StaxXMLReader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
public class ParseCompany {

    private final @NonNull CompanyRepository companyRepository;
    private static final Logger logger = LoggerFactory.getLogger(ParseCompany.class);

    public ParseCompanyCallableTest newRunnable(File[] files, String name) {
        return new ParseCompanyCallableTest(files, name);
    }

    public class ParseCompanyCallableTest implements Callable<List<Void>> {
        private final File[] files;
        private final String name;

        public ParseCompanyCallableTest(File[] files, String name) {
            this.files = files;
            this.name = name;
        }


        @Override
        public List<Void> call() throws Exception {
            int progress = 0;
            List<Company> companies = new ArrayList<>();

            for (File file : files) {
                try {
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
                    logger.error(file.getName() + String.valueOf(e.getCause()));
                }
            }
            try {
                companyRepository.saveAll(companies);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }

            return null;
        }
    }

    //~ Private methods

    private Company createCompany(@Valid CompanyService.ParseCompanyIn in) {
        Company company = new Company();
        company.setIco(in.ico);
        company.setName(in.name);
        company.setStreet(in.street);
        company.setNumber(in.number);
        company.setCity(in.city);

        return company;
    }
}
