package org.eclipse.scout.boot.tasks.standalone.security;

import org.eclipse.scout.boot.tasks.commons.ui.admin.db.ReadDatabaseAdministrationConsolePermission;
import org.eclipse.scout.boot.tasks.standalone.controller.ReadApiPermission;
import org.eclipse.scout.boot.ui.security.AbstractScoutBootWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security Configuration
 * <p>
 * For formatting please refer to <a href=
 * "https://spring.io/blog/2013/07/11/spring-security-java-config-preview-readability/">Spring
 * Security Java Config Preview: Readability</a>
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Configuration
	@Order(2)
	public static class DatabaseAdministrationConsoleWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
		/** @formatter:off **/
	      http
	          .antMatcher(StandaloneServletConfiguration.H2_CONTEXT_PATH + "/**")
	          .authorizeRequests()
	            .anyRequest().hasAuthority(ReadDatabaseAdministrationConsolePermission.class.getName())
	            .and()
	          .httpBasic()
	            .and()
	          .sessionManagement()
	            .sessionCreationPolicy(SessionCreationPolicy.NEVER) // only reuse UI HTTP-session (don't create new HTTP-session with HTTP basic authentication)
	            .and()
	          .headers()
	            .frameOptions()
	            .sameOrigin() // allow "h2-console" to be embedded in Scout UI
	            .and() 
	          .csrf()
	            .disable(); // "h2-console" doesn't implement CSRF
      	/** @formatter:on **/
		}
	}

	@Configuration
	@Order(1)
	public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
		/** @formatter:off **/
	      http
	          .antMatcher(StandaloneServletConfiguration.API_CONTEXT_PATH + "/**")
	          .authorizeRequests()
	            .anyRequest().hasAuthority(ReadApiPermission.class.getName())
	            .and()
	          .httpBasic()
	            .and()
	          .sessionManagement()
	            .sessionCreationPolicy(SessionCreationPolicy.NEVER); // only reuse UI HTTP-session (don't create new HTTP-session with HTTP basic authentication)
      	/** @formatter:on **/
		}
	}
	
	@Configuration
	public static class SecurityScoutBootWebSecurityConfigurerAdapter extends AbstractScoutBootWebSecurityConfigurerAdapter {
		
	}
}
