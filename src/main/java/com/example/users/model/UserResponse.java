package com.example.users.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponse {
    private String state;
    private User response;
    private String error;

    public UserResponse(String state, User response, String error) {
        this.state = state;
        this.response = response;
        this.error = error;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("response")
    public User getResponse() {
        return response;
    }

    public void setResponse(User response) {
        this.response = response;
    }

    @JsonProperty("error")
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
