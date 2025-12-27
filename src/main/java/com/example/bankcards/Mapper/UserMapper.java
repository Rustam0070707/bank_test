package com.example.bankcards.Mapper;

import com.example.bankcards.dto.RegisterRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import lombok.Builder;

@Builder
public class UserMapper {
    public static User toUser(RegisterRequest request){
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(Role.USER)
                .build();
    }
}
