package com.stg.vo.loginVo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("username")
    String username;

    @JsonProperty("password")
    String password;


}
