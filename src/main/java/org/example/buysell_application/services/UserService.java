package org.example.buysell_application.services;


import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.example.buysell_application.dao.dto.UserDto;
import org.example.buysell_application.dao.entityes.User;
import org.example.buysell_application.dao.mappers.UserMapper;
import org.example.buysell_application.dao.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
    }

    public User createUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail())) {
            throw new NoSuchElementException(
                "User with email " + userDto.getEmail() + " already exists");
        }
        User user = userMapper.toEntity(userDto);
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
        user = userMapper.merge(user, userDto);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}

