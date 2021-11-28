package service;

import data.UserRequest;
import entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final static String REGEX_EMAIL = "^(.+)@(.+)$";
    private final static String REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{5,20}$";
    @Autowired
    private UserRepository userRepository;

    public User validateUser(UserRequest userRequest) {
        LocalDateTime currentDate = LocalDateTime.now();
        String token = this.getJWTToken(userRequest.getEmail(),userRequest.getPassword());
        User user = new User(userRequest.getName(),
                userRequest.getEmail(),
                userRequest.getPhones(),
                currentDate, currentDate, currentDate,
                token, true);
        return user;
    }

    public String validateUserByEmail(String email) {
        User user1 = userRepository.getByEmail(email);
        if (Objects.nonNull(user1)) {
            return "El correo ya registrado";
        }
        return null;
    }

    public boolean checkPassword(String password) {
        Pattern pattern = Pattern.compile(REGEX_PASSWORD);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean checkEmail(String email) {
        Pattern pattern = Pattern.compile(REGEX_EMAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private String getJWTToken(String email, String password) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("softtekJWT")
                .setSubject(email)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        password.getBytes()).compact();

        return "Bearer " + token;
    }
}
