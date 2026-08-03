import 'app_theme.dart';

class User {
  const User({
    required this.id,
    required this.userName,
    required this.password,
    this.followingIds = const {},
    this.followerIds = const {},
    this.theme = AppTheme.light,
    this.profilePictureUrl,
  });

  final String id;
  final String userName;
  final String password;
  final Set<String> followingIds;
  final Set<String> followerIds;
  final AppTheme theme;
  final String? profilePictureUrl;

  String get initial {
    if (userName.isEmpty) return '?';
    return userName[0].toUpperCase();
  }

  User copyWith({
    String? id,
    String? userName,
    String? password,
    Set<String>? followingIds,
    Set<String>? followerIds,
    AppTheme? theme,
    String? profilePictureUrl,
  }) {
    return User(
      id: id ?? this.id,
      userName: userName ?? this.userName,
      password: password ?? this.password,
      followingIds: followingIds ?? this.followingIds,
      followerIds: followerIds ?? this.followerIds,
      theme: theme ?? this.theme,
      profilePictureUrl: profilePictureUrl ?? this.profilePictureUrl,
    );
  }

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'] as String,
      userName: json['userName'] as String,
      password: json['password'] as String,
      followerIds: Set<String>.from(json['followers'] ?? const []),
      followingIds: Set<String>.from(json['followingIds'] ?? const []),
      theme: _themeFromString(json['theme'] as String?),
      profilePictureUrl: json['profilePictureUrl'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userName': userName,
      'password': password,
      'followingIds': followingIds.toList(),
      'followerIds': followerIds.toList(),
      'theme': theme.name,
      'profilePictureUrl': profilePictureUrl,
    };
  }

  static AppTheme _themeFromString(String? value) {
    switch (value) {
      case 'dark':
        return AppTheme.dark;
      case 'light':
      default:
        return AppTheme.light;
    }
  }
}
