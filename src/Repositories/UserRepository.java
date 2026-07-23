package Repositories;

import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserRepository {

    private static final UserRepository instance = new UserRepository();

    private static final GenericFileManager<User> userFileManager = new GenericFileManager<>("users" , new ReentrantReadWriteLock());

    private UserRepository() {}

    public static UserRepository getInstance() {
        return instance;
    }

    public void addUser(User user) {
         userFileManager.addToList(user);
         userFileManager.save();
    }

    public void removeUser(User user) {
        user.validateRemoveUser();
        userFileManager.removeFromList(user);
        userFileManager.save();

    }

    public void removeUser(String id) {
        User remove = findUserById(id);
        removeUser(remove);
    }


    public User findUserById(String id) {
        Optional<User> user = userFileManager.findItemById(id);

        if (user.isEmpty()) {
            throw new ItemNotFoundException("user", id);
        }

        return user.get();
    }

    public User create(String userName , String password) {
        User user = new User(userName , password);
        addUser(user);
        return user;
    }

    public boolean isUserIdValid(String userId){
        return userFileManager.exists(u->u.getId().equals(userId));
    }
    public List<User> getAllUsers() {
        return userFileManager.getAll();
    }
}