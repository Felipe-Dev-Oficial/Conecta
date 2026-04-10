package com.etec.zl.conecta.Infraestructure.Security.Configuration;

import com.etec.zl.conecta.Infraestructure.Security.Filter.SecurityFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    SecurityFilter filter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost", "http://localhost:80"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(c -> c.disable())
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("conecta/management/**").hasRole("SECRETARIA")
                        .requestMatchers("conecta/anuncios/management/**").hasRole("SECRETARIA")
                        .requestMatchers("conecta/aluno/**").hasRole("ALUNO")
                        .requestMatchers("conecta/professores/**").hasRole("PROFESSOR")
                        .requestMatchers("conecta/alterador/**").permitAll()
                        .requestMatchers("conecta/secretaria").authenticated()
                        .requestMatchers("conecta/anuncios/default").permitAll()
                        .requestMatchers("conecta/anuncio/**").authenticated()
                        .requestMatchers("conecta/arquivos/**").authenticated()
                        .requestMatchers("conecta/auth/login").permitAll()
                        .requestMatchers("conecta/faqs/**").permitAll()
                        .requestMatchers("conecta/mensagens/**").authenticated()
                        .requestMatchers("conecta/auth").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
