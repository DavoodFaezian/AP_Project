import 'dart:convert';
import '../models/user.dart';
import '../services/session_manager.dart';
import '../services/socket_service.dart';
import '../models/app_theme.dart';
import 'package:local_auth/local_auth.dart';

class UserRepository {

  final LocalAuthentication _localAuth = LocalAuthentication();
  /// ۱. ثبت نام کاربر (signUp) -> خروجی: sessionId
  Future<String> signUp({
    required String userName,
    required String password,
    required String repeatedPassword,
  }) async {
    final responseMap = await _sendSocketRequest(
      actionName: "User/signUp",
      payload: {
        "userName": userName,
        "password": password,
        "repeatedPassword": repeatedPassword,
      },
    );

    final sessionId = responseMap['sessionId'] ?? responseMap['id'] ?? '';
    if (sessionId.isNotEmpty) {
      SessionManager.instance.saveSessionId(sessionId);
    }
    return sessionId;
  }

  Future<bool> isBiometricAvailable() async {
    try {
      final bool canAuthenticateWithBiometrics = await _localAuth.canCheckBiometrics;
      final bool isDeviceSupported = await _localAuth.isDeviceSupported();
      return canAuthenticateWithBiometrics && isDeviceSupported;
    } catch (e) {
      return false;
    }
  }

  /// احراز هویت با اثر انگشت
  Future<bool> authenticateWithBiometrics() async {
    try {
      return await _localAuth.authenticate(
        localizedReason: 'Please put your finger to open the application',
        biometricOnly: true,
        persistAcrossBackgrounding: true,
      );
    } catch (e) {
      return false;
    }
  }

  /// ۲. ورود به حساب کاربری (logIn) -> خروجی: sessionId
  Future<String> logIn({
    required String userName,
    required String password,
  }) async {
    final responseMap = await _sendSocketRequest(
      actionName: "User/logIn",
      payload: {
        "userName": userName,
        "password": password,
      },
    );

    final sessionId = responseMap['sessionId'] ?? responseMap['id'] ?? '';
    if (sessionId.isNotEmpty) {
      SessionManager.instance.saveSessionId(sessionId);
    }
    return sessionId;
  }

  /// ۳. خروج از حساب کاربری (logOut)
  Future<void> logOut() async {
    final sessionId = SessionManager.instance.sessionId;
    await _sendSocketRequest(
      actionName: "User/logOut",
      payload: {
        "sessionId": sessionId,
      },
    );
    SessionManager.instance.clearSession();
  }

  /// ۴. تغییر رمز عبور (changePassword)
  Future<void> changePassword({
    required String oldPassword,
    required String newPassword,
    required String confirmNewPassword,
  }) async {
    final sessionId = SessionManager.instance.sessionId;
    await _sendSocketRequest(
      actionName: "User/changePassword",
      payload: {
        "sessionId": sessionId,
        "oldPassword": oldPassword,
        "newPassword": newPassword,
        "confirmNewPassword": confirmNewPassword,
      },
    );
  }

  /// ۵. افزودن عکس پروفایل (addProfilePhoto)
  Future<void> addProfilePhoto(String profilePhotoId) async {
    final sessionId = SessionManager.instance.sessionId;
    await _sendSocketRequest(
      actionName: "User/addProfilePhoto",
      payload: {
        "sessionId": sessionId,
        "profilePhotoId": profilePhotoId,
      },
    );
  }

  /// ۶. حذف عکس پروفایل (removeProfilePhoto)
  Future<void> removeProfilePhoto() async {
    final sessionId = SessionManager.instance.sessionId;
    await _sendSocketRequest(
      actionName: "User/removeProfilePhoto",
      payload: {
        "sessionId": sessionId,
      },
    );
  }

  /// ۷. دنبال کردن کاربر دیگر (follow)
  Future<void> followUser(String followingUserId) async {
    final sessionId = SessionManager.instance.sessionId;
    await _sendSocketRequest(
      actionName: "User/follow",
      payload: {
        "followerSessionId": sessionId,
        "followingUserId": followingUserId,
      },
    );
  }

  /// ۸. لغو دنبال کردن کاربر دیگر (unfollow)
  Future<void> unfollowUser(String followingUserId) async {
    final sessionId = SessionManager.instance.sessionId;
    await _sendSocketRequest(
      actionName: "User/unfollow",
      payload: {
        "followerSessionId": sessionId,
        "followingUserId": followingUserId,
      },
    );
  }

  /// ۹. جستجوی کاربران (searchUsers)
  Future<List<User>> searchUsers(String query) async {
    final sessionId = SessionManager.instance.sessionId;
    final responseMap = await _sendSocketRequest(
      actionName: "User/searchUsers",
      payload: {
        "sessionId": sessionId,
        "query": query,
      },
    );

    List<dynamic> usersJson = responseMap['users'] ?? responseMap['data'] ?? [];
    return usersJson.map((json) => User.fromJson(json)).toList();
  }

  Future<void> changeAppTheme (AppTheme theme) async {
    final sessionId = SessionManager.instance.sessionId;
    await _sendSocketRequest(
      actionName: "User/changeAppTheme",
      payload: {
        "sessionId": sessionId,
        "appTheme": theme.name
    });
  }
  

  /// متد کمکی کپسوله‌سازی درخواست سوکت
  Future<Map<String, dynamic>> _sendSocketRequest({
    required String actionName,
    required Map<String, dynamic> payload,
  }) async {
    final requestMap = {
      "actionName": actionName,
      "payload": payload,
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['status'] == 'SUCCESS' || responseMap['statusCode'] == '200') {
      return responseMap;
    } else {
      throw Exception(responseMap['message'] ?? 'Action $actionName failed');
    }
  }
}