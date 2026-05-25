package com.example.Auth_Service.DTO;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

@Data
public class registerRequest {
    private String username;
    private String email;
    private String password;
}
