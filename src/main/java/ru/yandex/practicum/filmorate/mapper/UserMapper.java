package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setLogin(user.getLogin());
        userDto.setName(user.getName());
        userDto.setBirthday(user.getBirthday());
        return userDto;
    }

    public static User mapToUser(NewUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setLogin(request.getLogin());
        user.setName(request.getName());
        user.setBirthday(request.getBirthday());
        return user;
    }

    public static User updateUserFields(User user, UpdateUserRequest request) {
        User newUser = new User(user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        if (request.hasEmail()) {
            newUser.setEmail(request.getEmail());
        }
        if (request.hasLogin()) {
            newUser.setLogin(request.getLogin());
        }
        if (request.hasName()) {
            newUser.setName(request.getName());
        }
        if (request.hasBirthday()) {
            newUser.setBirthday(request.getBirthday());
        }
        return newUser;
    }
}