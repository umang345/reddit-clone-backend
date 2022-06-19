package com.umang345.redditclonebackend.service;

import com.umang345.redditclonebackend.dto.AuthenticationResponse;
import com.umang345.redditclonebackend.dto.LoginRequest;
import com.umang345.redditclonebackend.dto.RegisterRequest;
import com.umang345.redditclonebackend.exceptions.SpringRedditException;
import com.umang345.redditclonebackend.model.NotificationEmail;
import com.umang345.redditclonebackend.model.User;
import com.umang345.redditclonebackend.model.VerificationToken;
import com.umang345.redditclonebackend.repository.UserRepository;
import com.umang345.redditclonebackend.repository.VerificationTokenRepository;
import com.umang345.redditclonebackend.security.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/***
 *  Service methods for Authentication Module
 */

@Service
@AllArgsConstructor
@NoArgsConstructor
public class AuthService
{

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    @Value("${server.port}")
    private String port;

    /***
     * Service method for incoming register user request
     * Contains logic to create and register new user
     * and send account activation link
     *
     * @param registerRequest : contains the user details
     */
    @Transactional
    public void signup(RegisterRequest registerRequest)
    {
         User user = new User();
         user.setEmail(registerRequest.getEmail());
         user.setUsername(registerRequest.getUsername());
         user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
         user.setCreated(Instant.now());
         user.setEnabled(false);
         userRepository.save(user);
         
         String token = generateVerificationToken(user);

         mailService.sendMail(new NotificationEmail(
                 "Please Activate Your Account",
                 user.getEmail(),
                 "Thank you for signing up to Reddit Clone. " +
                         "Please click on the link to activate your account : " +
                         "http://localhost:"+port+"/api/auth/accountVerification/"+token
         ));
    }

    /***
     * Generates the account verification token
     * for newly created users.
     * It creates and stores the verification token in database
     *
     * @param user : User object containing user details
     * @return : Verification token as a String
     */
    private String generateVerificationToken(User user)
    {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    /***
     * Method to verify if the verification token exists and is valid
     *
     * @param token : the verification token to be verified
     */
    @Transactional
    public void verifyAccount(String token)
    {
        Optional<VerificationToken> verificationToken
                =  verificationTokenRepository.findByToken(token);

        verificationToken.orElseThrow(()-> new SpringRedditException("Invalid Token!!"));

        System.out.println(verificationToken.get().getUser().getUsername());
        System.out.println(verificationToken.get().getToken());
        fetchUserAndEnable(verificationToken.get());
    }

    /***
     * Method to enable user after verification token is checked to be valid
     *
     * @param verificationToken : Contains verification token and user details
     */
    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken)
    {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username)
                      .orElseThrow(()-> new SpringRedditException("User not found with name : "+username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    /***
     * Service method to authenticate login request from user
     *
     * @param loginRequest : Contains username and password for login
     * @return
     */
    public AuthenticationResponse login(LoginRequest loginRequest)
    {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(token,loginRequest.getUsername());
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal
                = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }
}
