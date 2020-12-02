package cz.agentes.aressearch.event;

import cz.agentes.aressearch.service.CompanyService;
import cz.agentes.aressearch.utils.DownloadUtils;
import cz.agentes.aressearch.utils.TarUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class OnStartApplicationEvent {

    private static final Logger logger = LoggerFactory.getLogger(OnStartApplicationEvent.class);

    private final @NonNull CompanyService companyService;

    @EventListener(ApplicationReadyEvent.class)
    public void startApp() throws Exception {
        logger.info("Ares search start event.");
        // Initialization of ares search
        companyService.aresSearchInitialization();
    }
}
