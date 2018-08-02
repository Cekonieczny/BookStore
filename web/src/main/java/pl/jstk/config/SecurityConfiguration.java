package pl.jstk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	/*
	 * @Autowired public void configureGlobal(AuthenticationManagerBuilder auth)
	 * throws Exception {
	 * auth.inMemoryAuthentication().withUser("admin").password("admin").roles(
	 * "ADMIN").and().withUser("user") .password("user").roles("USER"); ; }
	 */

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
				.withUser(User.withUsername("admin").password("{noop}admin").roles("ADMIN").build())
				.withUser(User.withUsername("user").password("{noop}user").roles("USER").build());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		/*
		 * http.authorizeRequests().antMatchers("/", "/css/**", "/img/**",
		 * "/*css/*", "/webjars/**", "/static/img/logo.png")
		 * .permitAll().anyRequest().authenticated().and().formLogin().loginPage
		 * ("/login").permitAll().and() .logout().logoutRequestMatcher(new
		 * AntPathRequestMatcher("/logout")).permitAll();
		 */

		http.authorizeRequests().antMatchers("/", "/books","/books/find", "/books/find/*", "/css/**", "/webjars/**", "/static/img/**").permitAll()
				.anyRequest().authenticated().and().formLogin().loginPage("/login").permitAll()
				.defaultSuccessUrl("/").failureUrl("/login?error").and().logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll();
		
		//http.csrf().disable();
	}

}
