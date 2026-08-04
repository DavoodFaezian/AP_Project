package DTO.User;

import MainClasses.Theme;

public class ChangeThemeDto {

    private String sessionId;

    private Theme theme;

    public ChangeThemeDto(String sessionId, Theme theme) {
        this.sessionId = sessionId;
        this.theme = theme;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
