package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NotValidException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Validator validator;
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public UserDto create(NewUserRequest request) {
        if (request.getName().isBlank()) {
            request.setName(request.getLogin());
        }
        User user = UserMapper.mapToUser(request);
        user = userStorage.create(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto update(UpdateUserRequest request) {
        User currentUser = userStorage.findById(request.getId()).orElseThrow(
                () -> new NotFoundException(String.format("User not found: id=%d", request.getId())));

        User commonUser = UserMapper.updateUserFields(currentUser, request);
        if (commonUser.getName().isBlank()) {
            commonUser.setName(commonUser.getLogin());
        }
        currentUser = userStorage.update(commonUser);
        return UserMapper.mapToUserDto(currentUser);
    }

    public Collection<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto findById(Long id) {
        return userStorage.findById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(
                        () -> new NotFoundException(String.format("User not found: id=%s", id)));
    }

    public void friendAdd(Long id, Long friendId) {
        userStorage.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("User not found: id = %s", id)));
        userStorage.findById(friendId).orElseThrow(
                () -> new NotFoundException(String.format("User not found: id = %s", friendId)));
        if (id.equals(friendId)) {
            throw new NotValidException(String.format("Same id: id=%s, friendId=%s", id, friendId));
        }
        friendshipStorage.friendAdd(id, friendId);
    }

    public void friendRemove(Long id, Long friendId) {
        userStorage.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("User not found: id = %s", id)));
        userStorage.findById(friendId).orElseThrow(
                () -> new NotFoundException(String.format("User not found: id = %s", friendId)));
        if (id.equals(friendId)) {
            throw new NotValidException(String.format("Same id: id=%s, friendId=%s", id, friendId));
        }
        friendshipStorage.friendRemove(id, friendId);
    }

    public Collection<UserDto> friendFindAll(Long id) {
        userStorage.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("User not found: id = %s", id)));
        return friendshipStorage.friendFindAll(id).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public Collection<UserDto> friendCommon(Long id, Long otherId) {
        userStorage.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("User not found: id = %s", id)));
        userStorage.findById(otherId).orElseThrow(
                () -> new NotFoundException(String.format("User not found: id = %s", otherId)));
        if (id.equals(otherId)) {
            throw new NotValidException(String.format("Same id: id=%s, otherId=%s", id, otherId));
        }
        return friendshipStorage.friendCommon(id, otherId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }
}