package com.restapi.study.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SessionRequestData {

    private String email;

    private String password;

}
