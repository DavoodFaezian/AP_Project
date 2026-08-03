class SessionManager {
  static final SessionManager instance = SessionManager._internal();
  SessionManager._internal();

  String? _sessionId;

  void setSession(String sessionId) {
    _sessionId = sessionId;
  }

  String? get sessionId => _sessionId;

  bool get isLoggedIn => _sessionId != null;

  void clearSession() {
    _sessionId = null;
  }
}