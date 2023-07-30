package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundExceptionEntity;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id;

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        validateUser(user);
        user.setFriends(new HashSet<>());
        user.setId(getId());
        users.put(user.getId(), user);
        log.info("Создание нового пользователя: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
       if (users.get(user.getId()) != null) {
            validateUser(user);
            user.setFriends(new HashSet<>());
            users.put(user.getId(), user);
            log.info("Обновление пользователя с id {}: {}", id, user);
        } else {
            log.error("Пользователь не найден.");
            throw new NotFoundExceptionEntity("User not found.");
        }

        return user;
    }

    @Override
    public User getUserById(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundExceptionEntity("User not found.");
        }
    }

    @Override
    public List<User> getFriendsByUserId(Integer id) {
        return findAllUsers().stream()
            .filter(user -> user.getFriends().contains(id))
            .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer friendId) {
        List<User> mutualFriends = new ArrayList<>();
        for (Integer id : getUserById(userId).getFriends()) {
            if (getUserById(friendId).getFriends().contains(id)) {
                mutualFriends.add(getUserById(id));
            }
        }
        return mutualFriends;
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        getUserById(userId).getFriends().add(friendId);
        getUserById(friendId).getFriends().add(userId);
        return getUserById(userId);
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        getUserById(userId).getFriends().remove(friendId);
        getUserById(friendId).getFriends().remove(userId);
        return getUserById(userId);
    }

    public int getId() {
        return ++id;
    }

    private void validateUser(User user) throws ValidationException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}