import 'app_theme.dart';

class User {
  const User({
    required this.id,
    required this.userName,
    required this.password,
    this.photoIds = const {},
    this.albumIds = const {},
    this.sharedPhotoIds = const {},
    this.theme = AppTheme.light,
    this.profilePictureUrl,
  });

  final String id;
  final String userName;
  final String password;
  final Set<String> photoIds;
  final Set<String> albumIds;
  final Set<String> sharedPhotoIds;
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
    Set<String>? photoIds,
    Set<String>? albumIds,
    Set<String>? sharedPhotoIds,
    AppTheme? theme,
    String? profilePictureUrl,
  }) {
    return User(
      id: id ?? this.id,
      userName: userName ?? this.userName,
      password: password ?? this.password,
      photoIds: photoIds ?? this.photoIds,
      albumIds: albumIds ?? this.albumIds,
      sharedPhotoIds: sharedPhotoIds ?? this.sharedPhotoIds,
      theme: theme ?? this.theme,
      profilePictureUrl: profilePictureUrl ?? this.profilePictureUrl,
    );
  }

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'] as String,
      userName: json['userName'] as String,
      password: json['password'] as String,
      photoIds: Set<String>.from(json['photoIds'] ?? const []),
      albumIds: Set<String>.from(json['albumIds'] ?? const []),
      sharedPhotoIds: Set<String>.from(json['sharedPhotoIds'] ?? const []),
      theme: _themeFromString(json['theme'] as String?),
      profilePictureUrl: json['profilePictureUrl'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userName': userName,
      'password': password,
      'photoIds': photoIds.toList(),
      'albumIds': albumIds.toList(),
      'sharedPhotoIds': sharedPhotoIds.toList(),
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
