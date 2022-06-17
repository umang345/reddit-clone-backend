package com.umang345.redditclonebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 *  Data Transfer Object Structure for
 *  registering new user endpoint
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest
{
    private String email;
    private String username;
    private String password;
}
