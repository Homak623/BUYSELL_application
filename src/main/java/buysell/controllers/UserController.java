package buysell.controllers;

import buysell.dao.create.CreateUserDto;
import buysell.dao.get.GetUserDto;
import buysell.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Создать пользователя", description = "Создает нового пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    public GetUserDto createUser(@RequestBody CreateUserDto createUserDto) {
        return userService.createUser(createUserDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID",
        description = "Возвращает пользователя по его идентификатору")
    @ApiResponse(responseCode = "200", description = "Пользователь успешно найден")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public GetUserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    @Operation(summary = "Получить всех пользователей",
        description = "Возвращает список всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен")
    public List<GetUserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя",
        description = "Обновляет пользователя по его идентификатору")
    @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public GetUserDto updateUser(@PathVariable Long id, @RequestBody
        CreateUserDto createUserDto) {
        return userService.updateUser(id, createUserDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя",
        description = "Удаляет пользователя по его идентификатору")
    @ApiResponse(responseCode = "200", description = "Пользователь успешно удален")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}



