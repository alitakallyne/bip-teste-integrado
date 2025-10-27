package config;

public class EjbClientConfig {
    
}
package com.example.backend.config;

import com.example.beneficioejb.service.BeneficioEjbService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

@Configuration
public class EjbClientConfig {

    // Nome JNDI do EJB (ajuste conforme seu servidor/app)
    private static final String JNDI_NAME = "ejb:/ejb-module/BeneficioEjbService!com.example.beneficioejb.service.BeneficioEjbService";

    @Bean
    public BeneficioEjbService beneficioEjbService() throws NamingException {
        // Configuração básica JNDI
        Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080"); // URL do servidor EJB

        Context context = new InitialContext(jndiProperties);

        // Lookup do EJB remoto
        return (BeneficioEjbService) context.lookup(JNDI_NAME);
    }
}
