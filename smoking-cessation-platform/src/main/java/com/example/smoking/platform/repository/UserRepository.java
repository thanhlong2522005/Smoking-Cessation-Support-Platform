package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Đánh dấu đây là một Spring Data JPA Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA sẽ tự động triển khai các phương thức sau dựa trên tên:
    Optional<User> findByUsername(String username);
<<<<<<< HEAD
    User findByEmail(String email);
=======
    Optional<User> findByEmail(String email);
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}