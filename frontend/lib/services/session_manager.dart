import 'package:shared_preferences/shared_preferences.dart';

class SessionManager {
  SessionManager._internal();
  static final SessionManager instance = SessionManager._internal();

  static const String _keySessionId = 'user_session_id';
  
  String? _sessionId;

  /// دریافت sessionId موجود در حافظه رم
  String? get sessionId => _sessionId;

  /// بررسی اینکه آیا کاربر لاگین است یا خیر
  bool get isLoggedIn => _sessionId != null && _sessionId!.isNotEmpty;

  /// ۱. فراخوانی این متد هنگام اجرای اولیه برنامه (مثلاً در main.dart)
  Future<void> init() async {
    final prefs = await SharedPreferences.getInstance();
    _sessionId = prefs.getString(_keySessionId);
  }

  /// ۲. ذخیره sessionId هنگام لاگین یا ثبت‌نام موفق
  Future<void> saveSessionId(String id) async {
    _sessionId = id;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_keySessionId, id);
  }

  /// ۳. پاک کردن sessionId هنگام خروج از حساب کاربری (Logout)
  Future<void> clearSession() async {
    _sessionId = null;
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_keySessionId);
  }
}