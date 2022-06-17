package com.umang345.redditclonebackend.service;

import com.umang345.redditclonebackend.dto.RegisterRequest;
import com.umang345.redditclonebackend.exceptions.SpringRedditException;
import com.umang345.redditclonebackend.model.NotificationEmail;
import com.umang345.redditclonebackend.model.User;
import com.umang345.redditclonebackend.model.VerificationToken;
import com.umang345.redditclonebackend.repository.UserRepository;
import com.umang345.redditclonebackend.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken)
    {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username)
                      .orElseThrow(()-> new SpringRedditException("User not found with name : "+username));
        user.setEnabled(true);
        userRepository.save(user);
    }
}
