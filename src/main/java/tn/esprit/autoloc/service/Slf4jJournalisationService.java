package tn.esprit.autoloc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implémentation de {@link JournalisationService} basée sur SLF4J.
 * <p>
 * DIP : VehiculeService dépend de l'interface {@link JournalisationService},
 * pas de cette classe concrète. Spring injecte automatiquement cette
 * implémentation grâce à {@code @Service}.
 */
@Service
public class Slf4jJournalisationService implements JournalisationService {

    private static final Logger log = LoggerFactory.getLogger(Slf4jJournalisationService.class);

    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void warn(String message) {
        log.warn(message);
    }

    @Override
    public void erreur(String message) {
        log.error(message);
    }
}
