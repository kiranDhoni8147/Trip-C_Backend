package com.tripc.userservice.repository;

import com.tripc.userservice.model.PasswordResetToken;
import com.tripc.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {

    PasswordResetToken findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);
}
