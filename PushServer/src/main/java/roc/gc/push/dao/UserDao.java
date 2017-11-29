package roc.gc.push.dao;

import java.util.List;

import roc.gc.push.pojo.User;

public interface UserDao {

    public User findUserById(Long userId);

    public User findUser(User user);

    public List<User> findUserList(User user);

    public int findUserCount(User user);

    public void addUser(User user);

    public void editUser(User user);
}
