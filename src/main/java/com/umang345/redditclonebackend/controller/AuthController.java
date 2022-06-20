package com.umang345.redditclonebackend.controller;

import com.umang345.redditclonebackend.dto.AuthenticationResponse;
import com.umang345.redditclonebackend.dto.LoginRequest;
import com.umang345.redditclonebackend.dto.RefreshTokenRequest;
import com.umang345.redditclonebackend.dto.RegisterRequest;
import com.umang345.redditclonebackend.service.AuthService;
import com.umang345.redditclonebackend.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

/***
 *  Rest Controller for authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController
{
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest)
    {
        authService.signup(registerRequest);
        return new ResponseEntity<>("User Registration Successful", OK);
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token)
    {
         authService.verifyAccount(token);
         return new ResponseEntity<>("Account activated successfully.", OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest)
    {
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh/token")
    public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(OK).body("Refresh Token Deleted Successfully!!");
    }
}
