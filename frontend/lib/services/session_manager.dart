import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/user.dart';
import '../repositories/user_repository.dart';

class SessionManager extends ChangeNotifier {
  SessionManager._internal();
  static final SessionManager instance = SessionManager._internal();

  static const String _keySessionId = 'user_session_id';
  
  String? _sessionId;
  User? _currentUser;

  /// دریافت sessionId موجود در حافظه رم
  String? get sessionId => _sessionId;

  /// اطلاعات کاربر جاری
  User? get currentUser => _currentUser;

  /// میان‌برها برای دسترسی سریع به مشخصات کاربر
  String? get userId => _currentUser?.id;
  String? get userName => _currentUser?.username;
  String? get profilePhotoName => _currentUser?.profilePhotoId;

  /// بررسی اینکه آیا کاربر لاگین است یا خیر
  bool get isLoggedIn => _sessionId != null && _sessionId!.isNotEmpty;

  /// ۱. فراخوانی این متد هنگام اجرای اولیه برنامه (مثلاً در main.dart)
  Future<void> init() async {
    final prefs = await SharedPreferences.getInstance();
    _sessionId = prefs.getString(_keySessionId);
    if (isLoggedIn) {
      try {
        await refreshProfile();
      } catch (e) {
        debugPrint("Error auto-fetching profile: $e");
        // اگر سشن نامعتبر بود، پاکش می‌کنیم
        if (e.toString().contains("session") || e.toString().contains("login")) {
          await clearSession();
        }
      }
    }
  }

  /// به‌روزرسانی اطلاعات پروفایل از سرور
  Future<void> refreshProfile() async {
    if (!isLoggedIn) return;
    _currentUser = await UserRepository().getUserProfile();
    notifyListeners();
  }

  /// ۲. ذخیره sessionId هنگام لاگین یا ثبت‌نام موفق
  Future<void> saveSessionId(String id) async {
    _sessionId = id;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_keySessionId, id);
    await refreshProfile();
  }

  /// ۳. پاک کردن sessionId هنگام خروج از حساب کاربری (Logout)
  Future<void> clearSession() async {
    _sessionId = null;
    _currentUser = null;
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_keySessionId);
    notifyListeners();
  }
}
