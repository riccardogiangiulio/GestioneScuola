package com.riccardo.giangiulio.gestionescuola.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.riccardo.giangiulio.gestionescuola.model.ERole;
import com.riccardo.giangiulio.gestionescuola.model.Role;
import com.riccardo.giangiulio.gestionescuola.model.SchoolClass;
import com.riccardo.giangiulio.gestionescuola.model.User;
import com.riccardo.giangiulio.gestionescuola.repository.RoleRepository;
import com.riccardo.giangiulio.gestionescuola.repository.UserRepository;

@Service
public class UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        log.info("UserService initialized");
    }

    public List<User> findAll() {
        log.debug("Retrieving all users");
        return userRepository.findAll();
    }

    public User findById(Long id) {
        log.debug("Finding user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new RuntimeException("User not found with ID: " + id);
                });
    }

    public User findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new RuntimeException("User not found with email: " + email);
                });
    }

    public Boolean existsByEmail(String email) {
        log.debug("Checking if user exists with email: {}", email);
        return userRepository.existsByEmail(email);
    }

    public User save(User user) {
        log.info("Saving new user with email: {}", user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        User savedUser = userRepository.save(user);
        log.info("User saved successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    public void deleteById(Long id) {
        log.warn("Attempting to delete user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            log.error("Failed to delete user: User not found with ID: {}", id);
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    public User update(Long id, User user) {
        log.info("Updating user with ID: {}", id);
        User existingUser = findById(id);
        
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setBirthDate(user.getBirthDate());
        
        if (user.getEmail() != null && !existingUser.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                log.error("Failed to update user: Email {} already in use", user.getEmail());
                throw new RuntimeException("Email already in use");
            }
            existingUser.setEmail(user.getEmail());
        }
        
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        
        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with ID: {}", id);
        return updatedUser;
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("Attempting to change password for user ID: {}", userId);
        User existingUser = findById(userId);
        
        if (!passwordEncoder.matches(currentPassword, existingUser.getPassword())) {
            log.error("Failed to change password: Current password is not valid for user ID: {}", userId);
            throw new RuntimeException("Current password is not valid");
        }
        
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(existingUser);
        log.info("Password changed successfully for user ID: {}", userId);
    }
    
    public User assignRole(Long userId, ERole roleType) {
        log.info("Assigning role {} to user ID: {}", roleType, userId);
        User existingUser = findById(userId);
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> {
                    log.error("Failed to assign role: Role {} not found", roleType);
                    return new RuntimeException("Role not found");
                });
        
        existingUser.setRole(role);
        User updatedUser = userRepository.save(existingUser);
        log.info("Role {} assigned successfully to user ID: {}", roleType, userId);
        return updatedUser;
    }

    public List<User> findByNameOrSurname(String keyword) {
        log.debug("Searching users by name or surname containing: {}", keyword);
        return userRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(keyword, keyword);
    }
    
    public List<User> findByRole(ERole role) {
        log.debug("Finding users with role: {}", role);
        return userRepository.findByRole(role);
    }
    
    public List<User> findStudentsBySchoolClass(SchoolClass schoolClass) {
        log.debug("Finding students in school class: {}", schoolClass.getId());
        return userRepository.findStudentsBySchoolClass(schoolClass);
    }
    
    public List<User> findTeachersBySchoolClass(SchoolClass schoolClass) {
        log.debug("Finding teachers in school class: {}", schoolClass.getId());
        return userRepository.findTeachersBySchoolClass(schoolClass);
    }
    
    public List<User> findStudentsWithoutActiveRegistrations() {
        log.debug("Finding students without active registrations");
        return userRepository.findStudentsWithoutActiveRegistrations();
    }

    public boolean isTeacher(User user) {
        log.debug("Checking if user {} is a teacher", user.getId());
        return user.getRole().getName() == ERole.ROLE_TEACHER;
    }
    
    public boolean isStudent(User user) {
        log.debug("Checking if user {} is a student", user.getId());
        return user.getRole().getName() == ERole.ROLE_STUDENT;
    }
    
    public boolean isAdmin(User user) {
        log.debug("Checking if user {} is an admin", user.getId());
        return user.getRole().getName() == ERole.ROLE_ADMIN;
    }
}
