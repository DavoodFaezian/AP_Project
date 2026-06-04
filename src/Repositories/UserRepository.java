package Repositories;

import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.User;

import java.util.Optional;

public class UserRepository {

    private final GenericFileManager<User> userFileManager;

    private UserRepository(){
        this.userFileManager = new GenericFileManager<>(User.fileName);
    }

    public UserRepository getInstance(){
        return new UserRepository();
    }

    public void saveUser(User user){
        for(int i = 0 ; i < userFileManager.getAll().size(); i++){
            if(user.getId().equals(userFileManager.getAll().get(i).getId())){
                userFileManager.getAll().set(i , user);
                break;
            }
        }
    }

    public User findUserById(String id){
        Optional<User> user = userFileManager.findItemById(id);
        if(user.isEmpty()){
            throw new ItemNotFoundException("User was not found!!!" , id);
        }
        return user.get();
    }

    public User findUserByUserName(String userName){
        for(int i = 0 ; i < userFileManager.getAll().size() ; i++){
            if(userName.equals(userFileManager.getAll().get(i).getUserName())){
                return userFileManager.getAll().get(i);
            }
        }
        throw new ItemNotFoundException("User was not found!!!" , "user");
    }

    public User findUserByPassword(String password){
        for(int i = 0 ; i < userFileManager.getAll().size() ; i++){
            if(password.equals(userFileManager.getAll().get(i).getPassword())){
                return userFileManager.getAll().get(i);
            }
        }
        throw new ItemNotFoundException("User was not found!!!" , "user");
    }
}
