package com.example.demo

import org.keycloak.adapters.AdapterDeploymentContext
import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver
import org.keycloak.adapters.springsecurity.AdapterDeploymentContextFactoryBean
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider
import org.keycloak.adapters.springsecurity.config.KeycloakSpringConfigResolverWrapper
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutFilter

@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun adapterDeploymentContext(keycloakConfigResolver: KeycloakConfigResolver): AdapterDeploymentContext? {
        val factoryBean =
            AdapterDeploymentContextFactoryBean(KeycloakSpringConfigResolverWrapper(keycloakConfigResolver))
        factoryBean.afterPropertiesSet()
        return factoryBean.getObject()
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        val keycloakAuthenticationProvider = KeycloakAuthenticationProvider()
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(
            SimpleAuthorityMapper()
        )

        return ProviderManager(keycloakAuthenticationProvider)
    }

    @Bean
    fun keycloakAuthenticationProcessingFilter(authenticationManager: AuthenticationManager) =
        KeycloakAuthenticationProcessingFilter(authenticationManager)

    @Bean
    fun apiFilterChain(
        http: HttpSecurity,
        keycloakAuthenticationProcessingFilter: KeycloakAuthenticationProcessingFilter
    ): SecurityFilterChain {
        http
            .addFilterBefore(keycloakAuthenticationProcessingFilter, LogoutFilter::class.java)
            .cors().and()
            .authorizeRequests()
            .antMatchers("/swagger-ui*").hasAnyRole()
            .antMatchers("/api/users*").hasRole("task-list-administrators")
            .antMatchers("/api/users/**").hasRole("task-list-administrators")
            .antMatchers("/api/tasks*").hasRole("task-list-users")
            .antMatchers("/").permitAll()
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .rememberMe().disable()

        return http.build()
    }

    @Bean
    fun keycloakConfigResolver() = KeycloakSpringBootConfigResolver()
}
