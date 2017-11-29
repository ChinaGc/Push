package roc.gc.push.service;

import java.util.List;

import roc.gc.push.pojo.User;

public interface UserService {

    public User findUser(Long userId);

    public User findUser(String token);

    public User findUser(User user);

    public List<User> findUserList(User user);

    public void upDateUser(User user);

    public void addUser(User user);
}
