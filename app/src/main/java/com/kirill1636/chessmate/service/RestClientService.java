package com.kirill1636.chessmate.service;

import com.kirill1636.chessmate.model.rest.User;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestClientService {

    //private final String SERVER_HOST = "188.225.33.112";
    private final String SERVER_HOST = "10.0.2.2";
    private final String SERVER_URL = "http://" + SERVER_HOST + ":8080/";
    //private final String SERVER_URL = "http://192.168.29.76:8000";

    private RestTemplate restTemplate;

    public RestClientService() {
        this.restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    public User loginByToken(String token) {
        try {
            return restTemplate.getForObject(SERVER_URL + "login?token={token}", User.class, token);
        } catch (Exception e) {
            throw new RuntimeException("Error while login by token", e);
        }
    }

    public User registerUser(User newUser){
        try {
            return restTemplate.postForObject(SERVER_URL + "registration", newUser, User.class);
        } catch (Exception e) {
            throw new RuntimeException("Error while login by token", e);
        }
    }
}
