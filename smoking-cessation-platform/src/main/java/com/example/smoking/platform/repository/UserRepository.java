package com.example.smoking.platform.repository;

import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository // Đánh dấu đây là một Spring Data JPA Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA sẽ tự động triển khai các phương thức sau dựa trên tên:
    Optional<User> findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(UserRole role);
}