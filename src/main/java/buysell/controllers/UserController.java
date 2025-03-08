package buysell.controllers;

import buysell.dao.create.CreateUserDto;
import buysell.dao.get.GetUserDto;
import buysell.services.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public GetUserDto createUser(@RequestBody CreateUserDto createUserDto)
        throws BadRequestException {
        return userService.createUser(createUserDto);
    }

    @GetMapping("/{id}")
    public GetUserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<GetUserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    public GetUserDto updateUser(@PathVariable Long id, @RequestBody
        CreateUserDto createUserDto) throws BadRequestException {
        return userService.updateUser(id, createUserDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}



