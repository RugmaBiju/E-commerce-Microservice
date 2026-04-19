package com.microstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MicroStore - E-Commerce Fashion Platform
 * Main Spring Boot Application Entry Point
 * 
 * Demonstrates: Class creation, Spring Boot configuration
 */
@SpringBootApplication
public class MicrostoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicrostoreApplication.class, args);
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║         🛍️  MicroStore Application Started!       ║");
        System.out.println("║                                                   ║");
        System.out.println("║    App:       http://localhost:8091             ║");
        System.out.println("║    H2 Console: http://localhost:8091/h2-console ║");
        System.out.println("║                                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        System.out.println("\n");
    }
}
