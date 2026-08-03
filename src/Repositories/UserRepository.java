package Repositories;

import Exceptions.ActionFailedException;
import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

public class UserRepository {

    private static final UserRepository instance = new UserRepository();

    private static final GenericFileManager<User> userFileManager = new GenericFileManager<>("users.txt" , new ReentrantReadWriteLock());

    private UserRepository() {}

    public static UserRepository getInstance() {
        return instance;
    }

    public void addUser(User user) {
         userFileManager.addToList(user);
         userFileManager.save();
    }

    public void removeUser(User user) {
        userFileManager.removeFromList(user);
        userFileManager.save();

    }

    public void removeUser(String id) {
        User remove = findUserById(id);
        removeUser(remove);
        userFileManager.save();
    }

    public void update() {
        userFileManager.save();
    }


    public User findUserById(String id) {
        Optional<User> user = userFileManager.findItemById(id);

        if (user.isEmpty()) {
            throw new ItemNotFoundException("user", id);
        }

        return user.get();
    }

    public void checkUserNameAndPassword(String userName) {
        Predicate<User> condition = s -> s.getUserName().equals(userName);
        List<User> users = userFileManager.filterItems(condition);
        if(!users.isEmpty()) {
            throw new ActionFailedException("UserName already exists.");
        }
    }

    public User findUserByUserNameAndPassword(String userName , String password) {
        Predicate<User> condition = s -> s.getUserName().equals(userName) && s.getPassword().equals(password);
        List<User> users = userFileManager.filterItems(condition);
        if(users.isEmpty()) {
            throw new ActionFailedException("User wasn't found.");
        }

        return users.getFirst();
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

    public void editUser(User user) {
        userFileManager.edit(user);
    }
}