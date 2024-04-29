package tech.getarrays.employeemanager.service.implementation;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import tech.getarrays.employeemanager.model.Role;
import tech.getarrays.employeemanager.model.UserE;
import tech.getarrays.employeemanager.service.repo.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("c'est ici le code " + username);
        UserE user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role  -> authorities.add(new SimpleGrantedAuthority(String.valueOf(role.getName()))));
        System.out.println("les roles sont a se niveau " + user);
        return user;
    }
}
