package DTO.User;

import MainClasses.Theme;

public class ChangeThemeDto {
    private Theme theme;
    private String sessionId;

    public ChangeThemeDto(Theme theme, String sessionId) {
        this.theme = theme;
        this.sessionId = sessionId;
    }

    public Theme getTheme() {
        return theme;
    }

    public String getSessionId() {
        return sessionId;
    }
}
