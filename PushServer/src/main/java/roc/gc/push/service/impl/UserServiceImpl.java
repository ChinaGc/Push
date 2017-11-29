package roc.gc.push.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import roc.gc.push.dao.UserDao;
import roc.gc.push.nio.SequenceCreater;
import roc.gc.push.pojo.User;
import roc.gc.push.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User findUser(Long userId) {
        return userDao.findUserById(userId);
    }

    @Override
    public User findUser(User user) {
        return userDao.findUser(user);
    }

    @Override
    public List<User> findUserList(User user) {
        return userDao.findUserList(user);
    }

    @Override
    public void upDateUser(User user) {
        userDao.editUser(user);
    }

    @Override
    public User findUser(String token) {
        User user = new User();
        user.setToken(token);
        return userDao.findUser(user);
    }

    @Override
    public void addUser(User user) {
        user.setToken(SequenceCreater.createSequence());
        userDao.addUser(user);
    }	
}
