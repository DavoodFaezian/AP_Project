import 'package:flutter/foundation.dart';
import 'package:test_app/models/user_profile.dart';
import '../models/app_theme.dart';
import '../models/user.dart';
import '../repositories/user_repository.dart';
import '../services/session_manager.dart';

class UserViewModel extends ChangeNotifier {
  UserViewModel({
    required UserRepository userRepository,
  }) : _userRepository = userRepository;

  final UserRepository _userRepository;

  User? _currentUser;
  List<UserProfile> _searchResults = [];
  bool _isLoading = false;
  String? _errorMessage;

  // Getters برای دسترسی آسان UI به وضعیت‌ها
  User? get currentUser => _currentUser;
  List<User> get searchResults => List.unmodifiable(_searchResults);
  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;
  bool get isLoggedIn => _currentUser != null;

  /// ۱. ثبت‌نام کاربر
  Future<bool> signUp({
    required String userName,
    required String password,
    required String repeatedPassword,
  }) async {
    _setLoading(true);
    try {
      final sessionId = await _userRepository.signUp(
        userName: userName,
        password: password,
        repeatedPassword: repeatedPassword,
      );

      if (sessionId.isNotEmpty) {
        _currentUser = User(id: '', username: userName);
        _setLoading(false);
        return true;
      }
      return false;
    } catch (e) {
      _setError('Failed to sign up: $e');
      return false;
    }
  }

  /// ۲. ورود به حساب کاربری (LogIn)
  Future<bool> logIn({
    required String userName,
    required String password,
  }) async {
    _setLoading(true);
    try {
      final sessionId = await _userRepository.logIn(
        userName: userName,
        password: password,
      );

      if (sessionId.isNotEmpty) {
        _currentUser = User(id: '', username: userName);
        _setLoading(false);
        return true;
      }
      return false;
    } catch (e) {
      _setError('Failed to log in: $e');
      return false;
    }
  }

  /// ۳. خروج از حساب کاربری (LogOut)
  Future<void> logOut() async {
    _setLoading(true);
    try {
      await _userRepository.logOut();
      _currentUser = null;
      _searchResults.clear();
    } catch (e) {
      _setError('Failed to log out: $e');
    } finally {
      _setLoading(false);
    }
  }

  /// ۴. تغییر رمز عبور
  Future<bool> changePassword({
    required String oldPassword,
    required String newPassword,
    required String confirmNewPassword,
  }) async {
    _setLoading(true);
    try {
      await _userRepository.changePassword(
        oldPassword: oldPassword,
        newPassword: newPassword,
        confirmNewPassword: confirmNewPassword,
      );
      _setLoading(false);
      return true;
    } catch (e) {
      _setError('Failed to change password: $e');
      return false;
    }
  }

  /// ۵. افزودن/تغییر عکس پروفایل
  Future<void> addProfilePhoto(String profilePhotoId) async {
    try {
      await _userRepository.addProfilePhoto(profilePhotoId);
      if (_currentUser != null) {
        _currentUser = _currentUser!.copyWith(profilePhotoId: profilePhotoId);
        notifyListeners();
      }
    } catch (e) {
      _setError('Failed to add profile photo: $e');
    }
  }

  /// ۶. حذف عکس پروفایل
  Future<void> removeProfilePhoto() async {
    try {
      await _userRepository.removeProfilePhoto();
      if (_currentUser != null) {
        _currentUser = _currentUser!.copyWith(profilePhotoId: null);
        notifyListeners();
      }
    } catch (e) {
      _setError('Failed to remove profile photo: $e');
    }
  }

  /// ۷. دنبال کردن کاربر دیگر (Follow)
  Future<void> followUser(String targetUserId) async {
    try {
      await _userRepository.followUser(targetUserId);

      if (_currentUser != null) {
        final updatedFollowings = Set<String>.from(_currentUser!.followingsId)
          ..add(targetUserId);
        _currentUser = _currentUser!.copyWith(followingsId: updatedFollowings);
        notifyListeners();
      }
    } catch (e) {
      _setError('Failed to follow user: $e');
    }
  }

  /// ۸. لغو دنبال کردن (Unfollow)
  Future<void> unfollowUser(String targetUserId) async {
    try {
      await _userRepository.unfollowUser(targetUserId);

      if (_currentUser != null) {
        final updatedFollowings = Set<String>.from(_currentUser!.followingsId)
          ..remove(targetUserId);
        _currentUser = _currentUser!.copyWith(followingsId: updatedFollowings);
        notifyListeners();
      }
    } catch (e) {
      _setError('Failed to unfollow user: $e');
    }
  }

  /// ۹. تغییر تم اپلیکیشن
  Future<void> changeAppTheme(AppTheme newTheme) async {
    try {
      await _userRepository.changeAppTheme(newTheme);

      if (_currentUser != null) {
        _currentUser = _currentUser!.copyWith(appTheme: newTheme);
        notifyListeners();
      }
    } catch (e) {
      _setError('Failed to change theme: $e');
    }
  }

  /// ۱۰. جستجوی کاربران
  Future<void> searchUsers(String query) async {
    if (query.trim().isEmpty) {
      _searchResults = [];
      notifyListeners();
      return;
    }

    _setLoading(true);
    try {
      _searchResults = await _userRepository.searchUsers(query.trim());
    } catch (e) {
      _setError('Search failed: $e');
    } finally {
      _setLoading(false);
    }
  }

  /// ورود به برنامه با استفاده از اثر انگشت
Future<bool> logInWithBiometrics() async {
  _setLoading(true);
  try {
    // ۱. بررسی پشتیبانی دستگاه
    final isAvailable = await _userRepository.isBiometricAvailable();
    if (!isAvailable) {
      _setError('بیومتریک یا اثر انگشت روی این دستگاه فعال نیست.');
      return false;
    }

    // ۲. درخواست اسکن اثر انگشت از کاربر
    final isAuthenticated = await _userRepository.authenticateWithBiometrics();

    if (isAuthenticated) {
      // ۳. خواندن sessionId ذخیره‌شده و تنظیم کاربر جاری
      final savedSessionId = SessionManager.instance.sessionId;
      if (savedSessionId != null && savedSessionId.isNotEmpty) {
        // کاربر با موفقیت تایید شد
        _setLoading(false);
        return true;
      } else {
        _setError('نشست قبلی یافت نشد، لطفاً ابتدا با رمز عبور وارد شوید.');
        return false;
      }
    } else {
      _setError('احراز هویت با اثر انگشت ناموفق بود.');
      return false;
    }
  } catch (e) {
    _setError('خطا در ورود با اثر انگشت: $e');
    return false;
  }
}

  // --- متدهای کمکی جهت مدیریت State ---
  void _setLoading(bool loading) {
    _isLoading = loading;
    _errorMessage = null;
    notifyListeners();
  }

  void _setError(String message) {
    _errorMessage = message;
    _isLoading = false;
    notifyListeners();
  }
}