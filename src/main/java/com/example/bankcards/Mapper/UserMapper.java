package com.example.bankcards.Mapper;

import com.example.bankcards.dto.RegisterRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import lombok.Builder;

@Builder
public class UserMapper {
    public static User toUser(RegisterRequest request){
        return User.builder()
                .username(request.username())
                .password(request.password())
                .role(Role.USER)
                .build();
    }
}
