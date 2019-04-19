package org.apache.zeppelin.utils;

import bdp.citadel.signature.VerifierFactory;
import bdp.citadel.springsecurity.CitadelFilter;
import bdp.citadel.springsecurity.CitadelUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.annotation.Nullable;

/**
 * A Spring configuration that uses Citadel and Spring Security to protect this application.
 */
@Configuration
@EnableWebSecurity
public class CitadelConfig extends WebSecurityConfigurerAdapter {

    @Autowired(required = false)
    @Nullable
    private VerifierFactory verifierFactory;

    @Bean
    public CitadelUserDetailsService citadelUserDetailsService() {
        return new CitadelUserDetailsService();
    }

    @Bean
    public CitadelFilter citadelFilter() {
        final CitadelFilter citadelFilter = new CitadelFilter();
        if (this.verifierFactory != null) {
            citadelFilter.setVerifierFactory(this.verifierFactory);
        }
        return citadelFilter;
    }

    @Bean
    public PreAuthenticatedAuthenticationProvider preauthAuthProvider() {
        final PreAuthenticatedAuthenticationProvider preauthAuthProvider =
                new PreAuthenticatedAuthenticationProvider();
        preauthAuthProvider.setPreAuthenticatedUserDetailsService(citadelUserDetailsService());
        return preauthAuthProvider;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.authenticationProvider(preauthAuthProvider());
    }

    @Override
    protected void configure(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .addFilterBefore(citadelFilter(), RequestHeaderAuthenticationFilter.class)
                .anonymous().disable()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/admin").hasAuthority("ROLE:BDP_ADMIN")
                .antMatchers("/user").hasAnyAuthority("GROUP:BDPUSERS", "ROLE:BDP_ADMIN")
                .antMatchers("/fouo").hasAuthority("AUTH:FOUO");
    }
}