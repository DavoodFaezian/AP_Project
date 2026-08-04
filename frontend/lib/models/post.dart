class Post {
  final String id;
  final String ownerId;
  final Set<String> photoIds;
  final Set<String> albumIds;
  final bool commentsAllowed;
  final DateTime? lastModified;

  Post({
    required this.id,
    required this.ownerId,
    required this.photoIds,
    required this.albumIds,
    required this.commentsAllowed,
    this.lastModified,
  });

  /// ساخت مدل از روی JSON دریافتی از سوکت (متناظر با PostDto یا Post جاوا)
  factory Post.fromJson(Map<String, dynamic> json) {
    return Post(
      id: json['id'] ?? '',
      ownerId: json['ownerId'] ?? '',
      photoIds: json['photoIds'] != null
          ? Set<String>.from(json['photoIds'])
          : <String>{},
      albumIds: json['albumIds'] != null
          ? Set<String>.from(json['albumIds'])
          : <String>{},
      commentsAllowed: json['commentsAllowed'] ?? json['areCommentsAllowed'] ?? true,
      lastModified: json['lastModified'] != null
          ? DateTime.tryParse(json['lastModified'].toString())
          : null,
    );
  }

  /// تبدیل مدل به Map جهت ارسال به سمت سرور (در صورت نیاز)
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'ownerId': ownerId,
      'photoIds': photoIds.toList(),
      'albumIds': albumIds.toList(),
      'commentsAllowed': commentsAllowed,
      if (lastModified != null) 'lastModified': lastModified!.toIso8601String(),
    };
  }

  /// متد کمکی برای کپی گرفتن و تغییر مقادیر (تغییرناپذیری/Immutability)
  Post copyWith({
    String? id,
    String? ownerId,
    Set<String>? photoIds,
    Set<String>? albumIds,
    bool? commentsAllowed,
    DateTime? lastModified,
  }) {
    return Post(
      id: id ?? this.id,
      ownerId: ownerId ?? this.ownerId,
      photoIds: photoIds ?? this.photoIds,
      albumIds: albumIds ?? this.albumIds,
      commentsAllowed: commentsAllowed ?? this.commentsAllowed,
      lastModified: lastModified ?? this.lastModified,
    );
  }
}