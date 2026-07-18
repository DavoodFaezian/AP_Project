package Repositories;

import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.User;

import java.util.List;
import java.util.Optional;

public class UserRepository {

    private final GenericFileManager<User> userFileManager;
    private static final UserRepository instance = new UserRepository();

    private UserRepository() {
        this.userFileManager = new GenericFileManager<>("user.txt");
    }

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