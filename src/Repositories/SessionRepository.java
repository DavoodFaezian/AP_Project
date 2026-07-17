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
}
