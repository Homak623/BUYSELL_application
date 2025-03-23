package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;

import buysell.cache.CustomCache;
import buysell.dao.create.CreateUserDto;
import buysell.dao.entityes.User;
import buysell.dao.get.GetUserDto;
import buysell.dao.mappers.UserMapper;
import buysell.dao.repository.UserRepository;
import buysell.errors.BadRequestException;
import buysell.errors.ResourceNotFoundException;
import buysell.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CustomCache<Long, GetUserDto> userCache;

    @InjectMocks
    private UserService userService;

    private User user;
    private CreateUserDto createUserDto;
    private GetUserDto getUserDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        createUserDto = new CreateUserDto();
        createUserDto.setEmail("test@example.com");

        getUserDto = new GetUserDto();
        getUserDto.setId(1L);
        getUserDto.setEmail("test@example.com");
    }

    @Test
    void getUserById_UserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_CacheHit() {
        Long userId = 1L;
        when(userCache.get(userId)).thenReturn(getUserDto);

        GetUserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userCache, times(1)).get(userId);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void getUserById_CacheMiss() {
        // Arrange
        Long userId = 1L;
        when(userCache.get(userId)).thenReturn(null); // Кэш пуст
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(getUserDto);

        GetUserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userCache, times(1)).get(userId); // Проверяем, что кэш был проверен
        verify(userRepository, times(1)).findById(userId); // Проверяем, что данные загружены из репозитория
        verify(userCache, times(1)).put(userId, getUserDto); // Проверяем, что данные добавлены в кэш
    }

    @Test
    void deleteUser_SuccessWithCache() {

        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
        verify(userCache, times(1)).remove(userId);
    }

    @Test
    void deleteUser_Success() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void getAllUsers_EmptyList() {

        when(userRepository.findAll()).thenReturn(List.of());
        when(userMapper.toDtos(List.of())).thenReturn(List.of());

        List<GetUserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDtos(List.of());
    }

    @Test
    void updateUser_SameEmail() throws BadRequestException {
        CreateUserDto sameEmailDto = new CreateUserDto();
        sameEmailDto.setEmail("test@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(getUserDto);

        GetUserDto result = userService.updateUser(1L, sameEmailDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L); // Проверяем, что репозиторий был вызван
        verify(userRepository, times(1)).existsByEmail("test@example.com"); // Проверяем, что email был проверен
        verify(userMapper, times(1)).updateUserFromDto(sameEmailDto, user); // Проверяем, что маппер был вызван
        verify(userRepository, times(1)).save(user); // Проверяем, что пользователь был сохранен
    }

    @Test
    void deleteUser_UserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
        verify(userCache, never()).remove(anyLong());
    }

    @Test
    void createUser_Success() throws BadRequestException {

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(createUserDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(getUserDto);

        GetUserDto result = userService.createUser(createUserDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userMapper, times(1)).toEntity(createUserDto);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void createUser_EmailExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.createUser(createUserDto));
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userMapper, never()).toEntity(any(CreateUserDto.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_Success() throws BadRequestException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(getUserDto);

        GetUserDto result = userService.updateUser(1L, createUserDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userMapper, times(1)).updateUserFromDto(createUserDto, user);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void updateUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, createUserDto));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userMapper, never()).updateUserFromDto(any(), any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_EmailExists() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("old@example.com");

        CreateUserDto newUserDto = new CreateUserDto();
        newUserDto.setEmail("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.updateUser(1L, newUserDto));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("new@example.com");
        verify(userMapper, never()).updateUserFromDto(any(), any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDtos(List.of(user))).thenReturn(List.of(getUserDto));

        List<GetUserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDtos(List.of(user));
    }
}