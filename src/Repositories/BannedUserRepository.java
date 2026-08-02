package Repositories;

import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.BannedUser;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BannedUserRepository {

    private static final BannedUserRepository instance = new BannedUserRepository();

    private static final GenericFileManager<BannedUser> bannedUserFileManager =
            new GenericFileManager<>("banned_users.txt", new ReentrantReadWriteLock());

    private BannedUserRepository() {}

    public static BannedUserRepository getInstance() {
        return instance;
    }

    public void addBannedUser(BannedUser bannedUser) {
        bannedUserFileManager.addToList(bannedUser);
        bannedUserFileManager.save();
    }

    public void removeBannedUser(BannedUser bannedUser) {
        bannedUserFileManager.removeFromList(bannedUser);
        bannedUserFileManager.save();
    }

    public void removeBannedUser(String userId) {
        BannedUser remove = findBannedUserById(userId);
        removeBannedUser(remove);
        bannedUserFileManager.save();
    }

    public void update() {
        bannedUserFileManager.save();
    }

    public BannedUser findBannedUserById(String userId) {
        Optional<BannedUser> bannedUser = bannedUserFileManager.findItemById(userId);

        if (bannedUser.isEmpty()) {
            throw new ItemNotFoundException("BannedUser", userId);
        }

        return bannedUser.get();
    }

    public BannedUser create(String userId, boolean loginAllowed, boolean commentAllowed, boolean postAllowed) {
        BannedUser bannedUser = new BannedUser(userId, loginAllowed, commentAllowed, postAllowed);
        addBannedUser(bannedUser);
        return bannedUser;
    }

    public boolean isUserBanned(String userId) {
        return bannedUserFileManager.exists(u -> u.getUserId().equals(userId));
    }

    public List<BannedUser> getAllBannedUsers() {
        return bannedUserFileManager.getAll();
    }

    public void editBannedUser(BannedUser bannedUser) {
        bannedUserFileManager.edit(bannedUser);
    }
    public boolean isUserAllowedToLogin(String userId) {
        Optional<BannedUser> bannedUser = bannedUserFileManager.findItemById(userId);

        return bannedUser.map(BannedUser::isUserAllowedToLogin).orElse(true);
    }
    public boolean isUserAllowedToComment(String userId) {
        Optional<BannedUser> bannedUser = bannedUserFileManager.findItemById(userId);

        return bannedUser.map(BannedUser::isUserAllowedToComment).orElse(true);
    }

    public boolean isUserAllowedToPost(String userId) {
        Optional<BannedUser> bannedUser = bannedUserFileManager.findItemById(userId);

        return bannedUser.map(BannedUser::isUserAllowedToPost).orElse(true);
    }
}