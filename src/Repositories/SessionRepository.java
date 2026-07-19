package Repositories;

import FileManager.GenericFileManager;
import MainClasses.Session;

public class SessionRepository {

    private final GenericFileManager<Session> sessionFileManager;

    private static final SessionRepository instance = new SessionRepository();

    private SessionRepository(){
        this.sessionFileManager = new GenericFileManager<>("session.txt");
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

    public boolean isSessionIdValid(String sessionId) {
        return sessionFileManager.exists(s -> s.getId().equals(sessionId));
    }
}
