import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:test_app/models/user_profile.dart';
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

    final sessionId = responseMap['payload']?['id'] ?? responseMap['sessionId'] ?? responseMap['id'] ?? '';
    if (sessionId.isNotEmpty) {
      await SessionManager.instance.saveSessionId(sessionId);
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

    final sessionId = responseMap['payload']?['id'] ?? responseMap['sessionId'] ?? responseMap['id'] ?? '';
    if (sessionId.isNotEmpty) {
      await SessionManager.instance.saveSessionId(sessionId);
    }
    return sessionId;
  }

  Future<void> loginBySessionId(String sessionId) async {
    await _sendSocketRequest(
      actionName: "User/loginBySessionId",
      payload: {
        "sessionId": sessionId,
      },
    );
    await SessionManager.instance.refreshProfile();
  }

  /// ۳. خروج از حساب کاربری (logOut)
  Future<void> logOut() async {
    final sessionId = SessionManager.instance.sessionId;
    try {
      await _sendSocketRequest(
        actionName: "User/logOut",
        payload: {
          "sessionId": sessionId,
        },
      );
    } finally {
      await SessionManager.instance.clearSession();
    }
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
    await SessionManager.instance.refreshProfile();
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
    await SessionManager.instance.refreshProfile();
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
    await SessionManager.instance.refreshProfile();
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
    await SessionManager.instance.refreshProfile();
  }

  Future<bool> checkIsFollowing(String userId) async {
    final sessionId = SessionManager.instance.sessionId;
    final responseMap = await _sendSocketRequest(
      actionName: "User/checkIsFollowing",
      payload: {
        "sessionId": sessionId,
        "userId": userId,
      },
    );

    // Based on BooleanDto { boolean value; }
    final payload = responseMap['payload'];
    if (payload is Map) {
      return payload['value'] ?? false;
    }
    return payload ?? false;
  }

  /// جستجوی کاربران (searchUsers)
  Future<List<UserProfile>> searchUsers(String query) async {
    final sessionId = SessionManager.instance.sessionId;
    final responseMap = await _sendSocketRequest(
      actionName: "User/searchUsers",
      payload: {
        "sessionId": sessionId,
        "query": query,
      },
    );

    List<dynamic> usersJson = responseMap['payload']?['users'] ?? responseMap['users'] ?? responseMap['data'] ?? [];
    return usersJson.map((json) => UserProfile.fromJson(json)).toList();
  }

  Future<UserProfile> getUserProfile() async {
    final sessionId = SessionManager.instance.sessionId;
    final responseMap = await _sendSocketRequest(
      actionName: "User/getUserProfile",
      payload: {
        "sessionId": sessionId,
      },
    );

    return UserProfile.fromJson(responseMap['payload'] ?? responseMap['data'] ?? responseMap);
  }

  Future<UserProfile> getUserProfileById(String userId) async {
    final sessionId = SessionManager.instance.sessionId;
    final responseMap = await _sendSocketRequest(
      actionName: "User/getUserProfile",
      payload: {
        "sessionId": sessionId,
        "userId": userId,
      },
    );

    return UserProfile.fromJson(responseMap['payload'] ?? responseMap['data'] ?? responseMap);
  }

  Future<void> changeAppTheme (AppTheme theme) async {
    final sessionId = SessionManager.instance.sessionId;
    await _sendSocketRequest(
      actionName: "User/changeAppTheme",
      payload: {
        "sessionId": sessionId,
        "appTheme": theme.name
    });
    await SessionManager.instance.refreshProfile();
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

    if (responseMap['status'] == 'SUCCESS' || responseMap['status'] == '200' || responseMap['statusCode'] == 200 || responseMap['statusCode'] == '200') {
      return responseMap;
    } else {
      throw Exception(responseMap['message'] ?? 'Action $actionName failed');
    }
  }
}
