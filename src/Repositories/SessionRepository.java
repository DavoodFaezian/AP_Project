package Repositories;

import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.Session;
import MainClasses.User;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SessionRepository {

    private static final SessionRepository instance = new SessionRepository();

    private static final GenericFileManager<Session> sessionFileManager = new GenericFileManager<>("sessions" , new ReentrantReadWriteLock());

    private SessionRepository(){
    }

    public static SessionRepository getInstance() {
        return instance;
    }

    public void addSession(Session session) {
        sessionFileManager.addToList(session);
        sessionFileManager.save();
    }

    public void removeSession(Session session) {
        sessionFileManager.removeFromList(session);
        sessionFileManager.save();
    }

    public Session createSession(String userId) {
        Session session = new Session(userId);
        addSession(session);
        return session;
    }

    public User findUserBySessionId(String sessionId) {
        Optional<Session> session = sessionFileManager.findItemById(sessionId);
        if(session.isEmpty()){
            throw new ItemNotFoundException("session" , sessionId);
        }
        String userId = session.get().getUserId();
        return UserRepository.getInstance().findUserById(userId);
    }

    public boolean isSessionIdValid(String userId , String sessionId) {
        return sessionFileManager.exists(s -> s.getId().equals(sessionId));
    }
}
