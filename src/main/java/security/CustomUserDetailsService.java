package ma.projet.produits.security;

import lombok.AllArgsConstructor;
import ma.projet.produits.entities.AppUser;
import ma.projet.produits.repositories.AppUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Point 7 : Charge l'utilisateur et ses rôles depuis la base (via AppUserRepository)
 * pour que Spring Security puisse authentifier / autoriser.
 */
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("Utilisateur '" + username + "' introuvable");
        }

        // Préfixe "ROLE_" attendu par hasRole(...) de Spring Security
        List<SimpleGrantedAuthority> authorities = appUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                .collect(java.util.stream.Collectors.toList());

        return new User(appUser.getUsername(), appUser.getPassword(), appUser.isEnabled(),
                true, true, true, authorities);
    }
}
