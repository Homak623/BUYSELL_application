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

    public UserGetDto getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
        return userMapper.toDto(user);
    }

    public UserGetDto createUser(UserCreateDto userCreateDto) {
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new IllegalArgumentException(
                "User with email " + userCreateDto.getEmail() + " already exists");
        }
        User user = userMapper.toEntity(userCreateDto);
        return userMapper.toDto(userRepository.save(user));
    }

    public UserGetDto updateUser(Long id, UserCreateDto userCreateDto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

        userMapper.updateUserFromDto(userCreateDto, user);
        return userMapper.toDto(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    public List<UserGetDto> getAllUsers() {
        return userMapper.toDtos(userRepository.findAll());
    }
}


