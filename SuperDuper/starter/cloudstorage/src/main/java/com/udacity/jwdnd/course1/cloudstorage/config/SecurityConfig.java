package com.udacity.jwdnd.course1.cloudstorage.config;
import com.udacity.jwdnd.course1.cloudstorage.services.AuthenticationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    private final AuthenticationService authenticationService;
    public SecurityConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(this.authenticationService);
    //calls methods on the builder class to add and configure parts of the authentication scheme for our app
    //configure(AuthenticationManagerBuilder auth): used to tell Spring to use our AuthenticationService to check user logins
    //first check whether password would be right or not
        //configure(HttpSecurity http): used to configure the HttpSecurity object by chaining methods to express security requirements
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/signup", "/css/**", "/js/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated();//any request ecept signup,need to be authenticated
        //Allows all users to access the /signup page, as well as the css and js files.
        //Allows authenticated users to make any request that's not explicitly covered elsewhere.
        //Generates a login form at /login and allows anyone to access it.
        //Redirects successful logins to the /home page.

        http.formLogin()
                .loginPage("/login")
                .loginPage("/login?logout")
                .permitAll();
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.formLogin()
                .defaultSuccessUrl("/home", true); //default login form is home
    }


}
//Onion Pattern: Sometimes also called Tiered Architecture, Multi-tiered Architecture, or n-tiered Architecture. This is a design pattern that separates areas of the application into controller, service, and data layers (and sometimes more). User flows originate from the controller tier, which passes them to the service tier, which then reaches a data access bean.
//Encryption: Modifying data before storing it, with the intention of using another algorithm to return the data to its original form once it needs to be used.
//Hashing: Modifying data before storing with the intention of never returning it to its original form. The modified data will be compared to other modified data only.
//Salt: random data that is combined with the input string when hashing so that the resultant hashed values are unique for each row. This means that two users with the same password would not have the same hash in the database.
//In order for Spring to actually use our AuthenticationService, we need to extend our Web Security configuration. We do that with an adapter for the WebSecurityConfigurer. This example overrides two configure methods:
//
//configure(AuthenticationManagerBuilder auth): used to tell Spring to use our AuthenticationService to check user logins
//configure(HttpSecurity http): used to configure the HttpSecurity object by chaining methods to express security requirements
//When a user logs in, we have no way to retrieve their original password, but we can re-hash their user input and see if it matches the hashed value in our database. Below is an example AuthenticationService class that implements a Spring interface, the AuthenticationProvider. This allows Spring to integrate our provider with many different authentication schemes, but we can see in our supports method that we specify that we only support UsernamePasswordAuthentication.
//Remember the separation of concerns and our onion architecture! The idea is that all user flows originate externally, travel through a controller, then through one or more services, finally through a data access bean to the database, and then all the way back up the chain.
//The authenticate() method takes an Authentication object from spring and returns an authentication token if the user's credentials are correct.