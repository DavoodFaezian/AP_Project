package Repositories;

import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.User;

import java.util.List;
import java.util.Optional;

public class UserRepository extends BaseRepository<User> {

    private static final UserRepository instance = new UserRepository();

    private UserRepository() {
        super("users");
    }

    public static UserRepository getInstance() {
        return instance;
    }

    public void addUser(User user) {
        var userFileManager = getFileManager("users");
         userFileManager.addToList(user);
         userFileManager.save();
    }

    public void removeUser(User user) {
        var userFileManager = getFileManager("users");
        user.validateRemoveUser();
        userFileManager.removeFromList(user);
        userFileManager.save();

    }

    public void removeUser(String id) {
        User remove = findUserById(id);
        removeUser(remove);
    }


    public User findUserById(String id) {
        var userFileManager = getFileManager("users");
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
        var userFileManager = getFileManager("users");
        return userFileManager.exists(u->u.getId().equals(userId));
    }
    public List<User> getAllUsers() {
        var userFileManager = getFileManager("users");
        return userFileManager.getAll();
    }
}