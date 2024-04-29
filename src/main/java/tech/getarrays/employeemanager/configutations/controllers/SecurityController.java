package tech.getarrays.employeemanager.configutations.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import tech.getarrays.employeemanager.configutations.SpringSecurityConfig;
//import tech.getarrays.employeemanager.configutations.controllers.securite.JwtUtils;
import tech.getarrays.employeemanager.model.ERole;
import tech.getarrays.employeemanager.model.Role;
import tech.getarrays.employeemanager.model.UserE;
import tech.getarrays.employeemanager.payload.LoginRequest;
import tech.getarrays.employeemanager.payload.SignupRequest;
import tech.getarrays.employeemanager.response.MessageResponse;
import tech.getarrays.employeemanager.response.UserInfoResponse;
import tech.getarrays.employeemanager.service.implementation.UserDetailImpl;
import tech.getarrays.employeemanager.service.implementation.UserDetailsServiceImpl;
import tech.getarrays.employeemanager.service.repo.RoleRepository;
import tech.getarrays.employeemanager.service.repo.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
//import java.util.HashSet;
import java.util.*;
//import java.util.Set;
import java.util.stream.Collectors;

//import static org.apache.logging.log4j.util.Base64Util.encoder;

@RestController
@RequestMapping("/auth")
public class SecurityController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    SpringSecurityConfig springSecurityConfig;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

//    @Autowired
//    private JwtEncoder jwtEncoder;

    @Autowired
    BCryptPasswordEncoder encoder;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

//    @Autowired
//    JwtUtils jwtUtils;
    byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();



    //Configuration du End Point
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//    public Map<String, String> login(@RequestBody LoginRequest loginRequest) {
        System.out.println(loginRequest.getUsername() + "yooooooooooooooooooooooooooooooooo");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getPassword(), loginRequest.getUsername())
        );

//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
//
            UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
       Map<String, Object> claims = new HashMap<>();
        String jwts = Jwts.builder()
                .setClaims(claims)
                .setSubject(loginRequest.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, keyBytes)
                .compact();
        System.out.printf("\n\n\n return " + jwts);
        return ResponseEntity.ok(jwts);
    }

@GetMapping("/profile")
public Authentication authentication(Authentication auth) {
    return auth;
}


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

//         Create new user's account
        UserE user = new UserE(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

//        user.setRoles(roles);
        System.out.println("voici les valeurs");
        userRepository.save(user);
        System.out.println("le code a ete sauvegarder");
//        System.out.println(userRepository.save(user));
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

//    @PostMapping("/signout")
//    public ResponseEntity<?> logoutUser() {
//        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
//        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
//                .body(new MessageResponse("You've been signed out!"));
//    }

}