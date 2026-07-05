package ma.projet.produits.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Point 5 puis Point 7 : configuration de Spring Security.
 *
 * Pour REPRODUIRE le point 5 de l'énoncé ("désactiver la protection par défaut" afin
 * de tester librement les vues MVC avant de sécuriser), il suffit de remplacer
 * temporairement le contenu de filterChain() par :
 *
 *   http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
 *       .csrf().disable();
 *
 * La configuration ci-dessous correspond à l'état FINAL demandé au point 7
 * (application réellement sécurisée par rôles).
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Ressources publiques : CSS/JS/webjars + page de login + console H2 (dev uniquement)
                .antMatchers("/webjars/**", "/css/**", "/js/**", "/login", "/h2-console/**").permitAll()
                // Consultation et recherche accessibles à USER et ADMIN
                .antMatchers("/", "/index", "/user/**").hasAnyRole("USER", "ADMIN")
                // Ajout / édition / suppression réservés à ADMIN
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/user/products", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex.accessDeniedPage("/403"))
            .authenticationProvider(authenticationProvider())
            // La console H2 (dev) fonctionne en iframe et fait des POST sans jeton CSRF
            .csrf(csrf -> csrf.ignoringAntMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
