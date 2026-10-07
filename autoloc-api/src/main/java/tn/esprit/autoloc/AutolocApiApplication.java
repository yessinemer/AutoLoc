package tn.esprit.autoloc;

import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.VehiculeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SpringBootApplication
public class AutolocApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutolocApiApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(VehiculeRepository vehiculeRepository) {
        return args -> {
            if (vehiculeRepository.count() == 0) {
                Vehicule v1 = new Vehicule(
                        null,
                        "200-TN-1234",
                        "Renault",
                        "Clio 5",
                        CategorieVehicule.CITADINE,
                        new BigDecimal("90.00"),
                        StatutVehicule.DISPONIBLE,
                        null,
                        new HashSet<>(),
                        new ArrayList<>()
                );
                Vehicule v2 = new Vehicule(
                        null,
                        "201-TN-5678",
                        "Peugeot",
                        "3008",
                        CategorieVehicule.SUV,
                        new BigDecimal("180.00"),
                        StatutVehicule.DISPONIBLE,
                        null,
                        new HashSet<>(),
                        new ArrayList<>()
                );
                Vehicule v3 = new Vehicule(
                        null,
                        "202-TN-9999",
                        "Volkswagen",
                        "Passat",
                        CategorieVehicule.BERLINE,
                        new BigDecimal("220.00"),
                        StatutVehicule.LOUE,
                        null,
                        new HashSet<>(),
                        new ArrayList<>()
                );

                vehiculeRepository.saveAll(List.of(v1, v2, v3));
                System.out.println(">>> Initialisation : 3 vehicules inseres avec succes !");
            }
        };
    }
}
