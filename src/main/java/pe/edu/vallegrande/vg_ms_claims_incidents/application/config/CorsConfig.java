package pe.edu.vallegrande.vg_ms_claims_incidents.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración global de CORS para WebFlux - ACCESO UNIVERSAL
 * Esta configuración permite el acceso desde cualquier origen web
 * Configuración actualizada para máxima compatibilidad y acceso abierto
 */
@Configuration
public class CorsConfig {

   @Bean
        public CorsWebFilter corsWebFilter() {
                CorsConfiguration corsConfiguration = new CorsConfiguration();

                corsConfiguration.setAllowedOriginPatterns(Arrays.asList("*"));

                corsConfiguration.setAllowedMethods(Arrays.asList("*"));

                corsConfiguration.setAllowedHeaders(Arrays.asList("*"));

                corsConfiguration.setExposedHeaders(Arrays.asList(
                                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials",
                                "Authorization", "Content-Disposition", "X-Total-Count",
                                "X-Page-Number", "X-Page-Size"));

                corsConfiguration.setAllowCredentials(false);

                corsConfiguration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsConfiguration);

                return new CorsWebFilter(source);
        }

}