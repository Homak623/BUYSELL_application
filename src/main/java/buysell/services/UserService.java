package buysell.services;


import buysell.cache.CustomCache;
import buysell.dao.create.CreateUserDto;
import buysell.dao.entityes.User;
import buysell.dao.get.GetUserDto;
import buysell.dao.mappers.UserMapper;
import buysell.dao.repository.UserRepository;
import buysell.errors.BadRequestException;
import buysell.errors.ErrorMessages;
import buysell.errors.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CustomCache<Long, GetUserDto> userCache;

    public GetUserDto getUserById(Long id) {
        GetUserDto cachedUser = userCache.get(id);
        if (cachedUser != null) {
            log.info("Cache hit for User ID: {}", id);
            return cachedUser;
        }

        log.info("Cache miss for User ID: {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.USER_NOT_FOUND, id)
            ));

        GetUserDto userDto = userMapper.toDto(user);
        userCache.put(id, userDto);
        log.info("User ID {} added to cache", id);

        return userDto;
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.USER_NOT_FOUND, id)
            ));

        userRepository.delete(user);
        userCache.remove(id);
        log.info("User ID {} removed from cache and deleted", id);
    }

    @Transactional
    public GetUserDto createUser(CreateUserDto createUserDto) throws BadRequestException {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new BadRequestException(
                String.format(ErrorMessages.EMAIL_EXISTS, createUserDto.getEmail())
            );
        }

        User user = userMapper.toEntity(createUserDto);
        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Transactional
    public GetUserDto updateUser(Long id, CreateUserDto createUserDto) throws BadRequestException {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.USER_NOT_FOUND, id)
            ));

        if (userRepository.existsByEmail(createUserDto.getEmail())
            && !user.getEmail().equals(createUserDto.getEmail())) {
            throw new BadRequestException(
                String.format(ErrorMessages.EMAIL_EXISTS, createUserDto.getEmail())
            );
        }

        userMapper.updateUserFromDto(createUserDto, user);
        user = userRepository.save(user);
        GetUserDto userDto = userMapper.toDto(user);

        userCache.put(id, userDto);

        return userDto;
    }

    public List<GetUserDto> getAllUsers() {
        return userMapper.toDtos(userRepository.findAll());
    }
}



