package buysell.services;


import buysell.dao.create.CreateUserDto;
import buysell.dao.entityes.User;
import buysell.dao.get.GetUserDto;
import buysell.dao.mappers.UserMapper;
import buysell.dao.repository.UserRepository;
import buysell.errors.ErrorMessages;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public GetUserDto getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                String.format(ErrorMessages.USER_NOT_FOUND, id)
            ));
        return userMapper.toDto(user);
    }

    @Transactional
    public GetUserDto createUser(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new IllegalArgumentException(
                String.format(ErrorMessages.EMAIL_EXISTS, createUserDto.getEmail())
            );
        }

        User user = userMapper.toEntity(createUserDto);
        user = userRepository.save(user); // Сохранение в БД

        return userMapper.toDto(user);
    }

    @Transactional
    public GetUserDto updateUser(Long id, CreateUserDto createUserDto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                String.format(ErrorMessages.USER_NOT_FOUND, id)
            ));

        if (userRepository.existsByEmail(createUserDto.getEmail())
            && !user.getEmail().equals(createUserDto.getEmail())) {
            throw new IllegalArgumentException(
                String.format(ErrorMessages.EMAIL_EXISTS, createUserDto.getEmail())
            );
        }

        userMapper.updateUserFromDto(createUserDto, user);
        user = userRepository.save(user); // Обновление в БД

        return userMapper.toDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                String.format(ErrorMessages.USER_NOT_FOUND, id)
            ));
        userRepository.delete(user);
    }

    public List<GetUserDto> getAllUsers() {
        return userMapper.toDtos(userRepository.findAll());
    }
}




