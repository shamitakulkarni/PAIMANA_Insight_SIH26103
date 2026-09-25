package com.paimana.insight.config;

import com.paimana.insight.model.Project;
import com.paimana.insight.model.ProjectStatus;
import com.paimana.insight.model.Role;
import com.paimana.insight.model.User;
import com.paimana.insight.repository.ProjectRepository;
import com.paimana.insight.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository users,
            ProjectRepository projects,
            PasswordEncoder encoder
    ) {

        return args -> {

            // =========================
            // DEMO USERS
            // =========================

            if (users.findByEmail("admin@example.com").isEmpty()) {

                User admin = new User(
                        "Admin User",
                        "admin@example.com",
                        encoder.encode("admin123"),
                        Role.ADMIN
                );

                users.save(admin);
            }

            if (users.findByEmail("officer@example.com").isEmpty()) {

                User officer = new User(
                        "Officer User",
                        "officer@example.com",
                        encoder.encode("officer123"),
                        Role.OFFICER
                );

                users.save(officer);
            }

            if (users.findByEmail("monitor@example.com").isEmpty()) {

                User monitor = new User(
                        "Monitoring User",
                        "monitor@example.com",
                        encoder.encode("monitor123"),
                        Role.MONITORING
                );

                users.save(monitor);
            }


            // =========================
            // DEMO PROJECTS
            // =========================

            if (projects.findByProjectCode("PAI-001").isEmpty()) {

                Project p1 = new Project();

                p1.setProjectCode("PAI-001");
                p1.setProjectName("National Logistics Corridor");
                p1.setMinistry("Transport");
                p1.setSector("Logistics");
                p1.setState("Karnataka");
                p1.setDataSource("PROTOTYPE");

                p1.setOriginalCost(
                        new BigDecimal("100.00")
                );

                p1.setRevisedCost(
                        new BigDecimal("125.00")
                );

                p1.setExpenditure(
                        new BigDecimal("78.00")
                );

                p1.setOriginalEndDate(
                        LocalDate.of(2026, 12, 31)
                );

                p1.setRevisedEndDate(
                        LocalDate.of(2028, 6, 30)
                );

                p1.setPhysicalProgress(55.0);

                p1.setStatus(ProjectStatus.ONGOING);

                projects.save(p1);
            }


            if (projects.findByProjectCode("PAI-002").isEmpty()) {

                Project p2 = new Project();

                p2.setProjectCode("PAI-002");
                p2.setProjectName("Regional Freight Hub");
                p2.setMinistry("Transport");
                p2.setSector("Logistics");
                p2.setState("Maharashtra");
                p2.setDataSource("PROTOTYPE");

                p2.setOriginalCost(
                        new BigDecimal("90.00")
                );

                p2.setRevisedCost(
                        new BigDecimal("96.00")
                );

                p2.setExpenditure(
                        new BigDecimal("60.00")
                );

                p2.setOriginalEndDate(
                        LocalDate.of(2026, 10, 31)
                );

                p2.setRevisedEndDate(
                        LocalDate.of(2027, 3, 31)
                );

                p2.setPhysicalProgress(75.0);

                p2.setStatus(ProjectStatus.ONGOING);

                projects.save(p2);
            }


            if (projects.findByProjectCode("PAI-003").isEmpty()) {

                Project p3 = new Project();

                p3.setProjectCode("PAI-003");
                p3.setProjectName("Digital Public Service Centre");
                p3.setMinistry("Electronics");
                p3.setSector("Digital");
                p3.setState("Karnataka");
                p3.setDataSource("PROTOTYPE");

                p3.setOriginalCost(
                        new BigDecimal("70.00")
                );

                p3.setRevisedCost(
                        new BigDecimal("73.00")
                );

                p3.setExpenditure(
                        new BigDecimal("45.00")
                );

                p3.setOriginalEndDate(
                        LocalDate.of(2027, 2, 28)
                );

                p3.setRevisedEndDate(
                        LocalDate.of(2027, 4, 30)
                );

                p3.setPhysicalProgress(82.0);

                p3.setStatus(ProjectStatus.ONGOING);

                projects.save(p3);
            }

            System.out.println("=================================");
            System.out.println("PAIMANA INSIGHT DEMO DATA LOADED");
            System.out.println("=================================");
        };
    }
}