package com.example.otpjwt.model;

import com.arangodb.springframework.annotation.Document;
import org.springframework.data.annotation.Id;
import lombok.Data;

@Data
@Document("users")
public class User {
    @Id
    private String id; // _key/_id in ArangoDB
    private String fullName;
    private String phoneNumber;
}
