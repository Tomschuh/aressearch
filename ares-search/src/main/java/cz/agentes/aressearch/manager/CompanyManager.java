package cz.agentes.aressearch.manager;

import cz.agentes.aressearch.domain.Company;
import cz.agentes.aressearch.repository.CompanyRepository;
import cz.agentes.aressearch.service.CompanyService;
import cz.agentes.aressearch.service.CompanyService.ParseCompanyIn;
import cz.agentes.aressearch.threads.ParseCompany;
import cz.agentes.aressearch.threads.ParseCompany.ParseCompanyCallableTest;
import cz.agentes.aressearch.threads.ParseCompanyCallable;
import cz.agentes.aressearch.utils.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class CompanyManager {

    // URL
    @Value("${ares-search.url.base}")
    private String aresUrlBase;
    @Value("${ares-search.url.all}")
    private String aresUrlAll;
    @Value("${ares-search.url.changes}")
    private String aresUrlChanges;
    @Value("${ares-search.url.one}")
    private String aresUrlOne;
    // Directory
    @Value("${ares-search.filedb.root-paths.download-files}")
    private String downloadDir;
    @Value("${ares-search.filedb.root-paths.xml-files}")
    private String xmlDir;
    @Value("${ares-search.filedb.root-paths.changes-files}")
    private String changesDir;

    private static final int MAX_THREADS = 2;
    private static final int NO_SEGMENTS = 100;
    private static final Logger logger = LoggerFactory.getLogger(CompanyManager.class);

    private final @NonNull CompanyRepository companyRepository;
    private final @NonNull ParseCompany parseRunnable;


    public void downloadAllCompanies() throws IOException {
        // Download files from ares
        logger.info("Files downloading starts at: " + LocalDateTime.now());
        try {
            DownloadUtils.downloadFile(aresUrlBase + aresUrlAll, downloadDir + "ares_all.tar.gz");
        } catch (IOException exception) {
            throw new IOException(exception.getMessage());
        }
        logger.info("Files downloading finished at: " + LocalDateTime.now());

        // Decompressing files .tar.gz
        logger.info("Files decompressing starts at: " + LocalDateTime.now());
        File xmlFiles = new File(xmlDir);
        TarUtils.decompress(downloadDir + "/ares_all.tar.gz", xmlFiles);
        logger.info("Files decompressing finished at: " + LocalDateTime.now());
    }

    // Version 1.

    public void indexAllCompaniesV1() throws InterruptedException, ExecutionException {
        File[] allXmlDir = new File(xmlDir + "/VYSTUP/DATA").listFiles();
//        File[] allFiles = new File(xmlDir + "/VYSTUP/DATA").listFiles();
//        File[] allXmlDir = Arrays.copyOfRange(allFiles, 0, 50);
        int sizeOfSegment = computeSizeOfArrays(allXmlDir.length, NO_SEGMENTS);
        // Set threads
        List<File[]> segments = new ArrayList<>();
        for (int noSegment = 0; noSegment < NO_SEGMENTS; noSegment++ ) {
            if (noSegment != NO_SEGMENTS - 1) {
                segments.add(Arrays.copyOfRange(
                        allXmlDir,
                        noSegment * sizeOfSegment,
                        (noSegment + 1) * sizeOfSegment));
            } else {
                segments.add(Arrays.copyOfRange(
                        allXmlDir,
                        noSegment * sizeOfSegment,
                        allXmlDir.length));
            }
        }

        segments.forEach(segment -> {
            try {
                parseSegment(segment);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    // Version 1.

    public void indexAllCompaniesV2() throws InterruptedException, ExecutionException {
        File[] allXmlDir = new File(xmlDir + "/VYSTUP/DATA").listFiles();
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        // Divide all companies for multi thread processing
        logger.info(String.format("XML parsing starts at: %s", LocalDateTime.now()));
        int sizeOfArrays = computeSizeOfArrays(allXmlDir.length, MAX_THREADS);
        // Set threads
        Set<ParseCompanyCallableTest> callables = new HashSet<ParseCompanyCallableTest>();
        for (int threadNo = 0; threadNo < MAX_THREADS; threadNo++ ) {
            if (threadNo != MAX_THREADS - 1) {
                callables.add(parseRunnable.newRunnable(Arrays.copyOfRange(
                        allXmlDir,
                        threadNo * sizeOfArrays,
                        (threadNo + 1) * sizeOfArrays),
                        "Callable test no." + threadNo));
            } else {
                callables.add(parseRunnable.newRunnable(Arrays.copyOfRange(
                        allXmlDir,
                        threadNo * sizeOfArrays,
                        allXmlDir.length),
                        "Callable test no." + threadNo));
            }
        }

//      Invoke all threads
        executor.invokeAll(callables);
        logger.info(String.format("XML parsing finished at: %s", LocalDateTime.now()));
        logger.info(String.format("Companies indexing finished at: %s", LocalDateTime.now()));
    }

    public void indexAllCompaniesV3() throws Exception {
        List<Future<Company>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        File[] allXmlDir = new File(xmlDir + "/VYSTUP/DATA").listFiles();
        long progress = 0;

        for(File file : allXmlDir) {
            progress = progress + 1;
            if ((progress % 10000) == 0) {
                logger.info(String.format("Progress %s of %s", progress, allXmlDir.length));
            }
            futures.add(executorService.submit(new Callable<Company>() {
                @Override
                public Company call() throws Exception {
                    Company company = createCompany(StaxXMLReader.parseXml(file));
                    if (company.getIco() == null) {
                        company.setIco(FilenameUtils.removeExtension(file.getName()));
                    }
                    return company;
                }
            }));
        }
        List<Company> companies = new ArrayList<>(futures.size());
        for (Future<Company> f : futures) companies.add(f.get());

        for (Company company: companies) {
            try {
                companyRepository.save(company);
            } catch (Exception e) {
                logger.error(String.format("Filename: %s, message %s", company.getIco(), e.getMessage()));
            }
        }

        executorService.shutdown();
    }

    public void downloadChanges() throws IOException {
        logger.info("Download changes file.");
        try {
            DownloadUtils.downloadFile(aresUrlBase + aresUrlChanges, downloadDir + "/changes.7z");
        } catch (IOException exception) {
            throw new IOException(exception.getMessage());
        }

        SevenZUtils.unzipSevenZFile(downloadDir + "/changes.7z", changesDir);
    }

    public List<String> getLastChanges() throws IOException {
        String[] changesFileList = new File(changesDir).list();
        List<String> lastChanges = new ArrayList<>();
        String lastChangesFilename = "";
        LocalDateTime today = LocalDateTime.now();

        if (changesFileList != null) {
            for(String filename : changesFileList) {
                if (filename.equals(String.format("ZMENY_%s%s%s.csv", today.getYear(), today.getMonth().getValue(), today.getDayOfMonth() - 1))) {
                    lastChangesFilename = filename;
                }
            }
        }
        if (!lastChangesFilename.isBlank()) {
            lastChanges = CsvUtils.readCsvRows(changesDir + "/" + lastChangesFilename, ",");
        } else {
            logger.warn("No file changes for today.");
        }

        return lastChanges;
    }

    public void logLastChanges(LocalDateTime localDateTime) {
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        row.add("Last update:");
        rows.add(row);
        row = new ArrayList<>();
        row.add(String.format("%s%s%s", localDateTime.getYear(), localDateTime.getMonth().getValue(), localDateTime.getDayOfMonth()));
        rows.add(row);
        // Write to csv date of last changes
        try {
            CsvUtils.writeCsv(changesDir + "/log/changes.csv",rows, ",");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void updateCompanyFrom(Company company) throws IOException {
        logger.info("Download one company XML.");
        try {
            DownloadUtils.downloadFile(aresUrlBase + aresUrlOne + company.getIco(), downloadDir + "/" + company.getIco() + ".xml");
            File file = new File(downloadDir + "/" + company.getIco() + ".xml");
            ParseCompanyIn in = StaxXMLReader.parseXml(file);
            company.setCity(in.city);
            company.setNumber(in.number);
            company.setStreet(in.street);
            company.setName(in.name);

            AresFileUtils.deleteFile(xmlDir, company.getIco() + ".xml");
            AresFileUtils.moveFile(downloadDir, company.getIco() + ".xml", xmlDir);
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    //~ Private methods

    private int computeSizeOfArrays(int allFiles, int noArrays) {
        return allFiles / noArrays;
    }

    private void parseSegment(File[] allXmlDir) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        // Divide all companies for multi thread processing
        logger.info(String.format("XML parsing starts at: %s", LocalDateTime.now()));
        int sizeOfArrays = computeSizeOfArrays(allXmlDir.length, MAX_THREADS);
        // Set threads
        Set<ParseCompanyCallable> callables = new HashSet<ParseCompanyCallable>();
        for (int threadNo = 0; threadNo < MAX_THREADS; threadNo++ ) {
            if (threadNo != MAX_THREADS - 1) {
                callables.add(new ParseCompanyCallable(Arrays.copyOfRange(
                        allXmlDir,
                        threadNo * sizeOfArrays,
                        (threadNo + 1) * sizeOfArrays),
                        "Callable test no." + threadNo));
            } else {
                callables.add(new ParseCompanyCallable(Arrays.copyOfRange(
                        allXmlDir,
                        threadNo * sizeOfArrays,
                        allXmlDir.length),
                        "Callable test no." + threadNo));
            }
        }

//      Invoke all threads
        List<Future<List<Company>>> futures = executor.invokeAll(callables);
        logger.info(String.format("XML parsing finished at: %s", LocalDateTime.now()));

        // Indexing companies
        logger.info(String.format("Companies indexing starts at: %s", LocalDateTime.now()));
        for (Future<List<Company>> future : futures) {
                future.get().forEach(company -> {
                    try {
                        companyRepository.save(company);
                    } catch (Exception e) {
                        logger.error(company.getIco() + " : " + String.valueOf(e.getCause()));
                    }
                });
        }
        logger.info(String.format("Companies indexing finished at: %s", LocalDateTime.now()));
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