import 'app_theme.dart';

class User {
  final String id;
  final String username;
  final String? profilePhotoId;
  final Set<String> followersId;
  final Set<String> followingsId;
  final AppTheme appTheme; // تم انتخابی کاربر

  User({
    required this.id,
    required this.username,
    this.profilePhotoId,
    Set<String>? followersId,
    Set<String>? followingsId,
    this.appTheme = AppTheme.system, // پیش‌فرض: بر اساس تنظیمات سیستم
  })  : followersId = followersId ?? <String>{},
        followingsId = followingsId ?? <String>{};

  /// ساخت مدل از JSON دریافتی از سرور
  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'] ?? json['userId'] ?? '',
      username: json['userName'] ?? json['username'] ?? '',
      profilePhotoId: json['profilePhotoId'],
      followersId: json['followersId'] != null
          ? Set<String>.from(json['followersId'])
          : json['followers'] != null
              ? Set<String>.from(json['followers'])
              : <String>{},
      followingsId: json['followingsId'] != null
          ? Set<String>.from(json['followingsId'])
          : json['followings'] != null
              ? Set<String>.from(json['followings'])
              : <String>{},
      appTheme: _parseTheme(json['appTheme']),
    );
  }

  /// تبدیل رشته دریافتی به Enum تم
  static AppTheme _parseTheme(String? themeStr) {
    switch (themeStr?.toLowerCase()) {
      case 'dark':
        return AppTheme.dark;
      case 'light':
        return AppTheme.light;
      case 'system':
      default:
        return AppTheme.light;
    }
  }

  /// تبدیل به Map جهت ذخیره‌سازی یا ارسال به سرور
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userName': username,
      if (profilePhotoId != null) 'profilePhotoId': profilePhotoId,
      'followersId': followersId.toList(),
      'followingsId': followingsId.toList(),
      'appTheme': appTheme.name, // ذخیره به صورت رشته "light", "dark" یا "system"
    };
  }

  /// متد copyWith جهت به‌روزرسانی آسان تم یا بقیه فیلدها
  User copyWith({
    String? id,
    String? username,
    String? profilePhotoId,
    Set<String>? followersId,
    Set<String>? followingsId,
    AppTheme? appTheme,
  }) {
    return User(
      id: id ?? this.id,
      username: username ?? this.username,
      profilePhotoId: profilePhotoId ?? this.profilePhotoId,
      followersId: followersId ?? this.followersId,
      followingsId: followingsId ?? this.followingsId,
      appTheme: appTheme ?? this.appTheme,
    );
  }
}