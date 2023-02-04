package com.jbdl.KafkaWallet;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    @NotBlank
    private String name;

    @Email
    private String email;
    private String contact;

    public User to() {
        return User.builder()
                .email(this.email)
                .contact(this.contact)
                .name(this.name)
                .build();
    }
}
