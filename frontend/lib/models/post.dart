class Post {
  final String id;
  final String ownerId;
  final Set<String> photoIds;
  final Set<String> albumIds;
  final Set<String> commentIds;
  final bool commentsAllowed;
  final DateTime? createdAt;
  final DateTime? lastModified;

  Post({
    required this.id,
    required this.ownerId,
    required this.photoIds,
    required this.albumIds,
    required this.commentIds,
    required this.commentsAllowed,
    this.createdAt,
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
      commentIds: json['commentIds'] != null
          ? Set<String>.from(json['commentIds'])
          : <String>{},
      commentsAllowed: json['commentsAllowed'] ?? json['areCommentsAllowed'] ?? true,
      createdAt: json['createdAt'] != null
          ? DateTime.tryParse(json['createdAt'].toString())
          : null,
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
      'commentIds': commentIds.toList(),
      'commentsAllowed': commentsAllowed,
      if (createdAt != null) 'createdAt': createdAt!.toIso8601String(),
      if (lastModified != null) 'lastModified': lastModified!.toIso8601String(),
    };
  }

  /// متد کمکی برای کپی گرفتن و تغییر مقادیر (تغییرناپذیری/Immutability)
  Post copyWith({
    String? id,
    String? ownerId,
    Set<String>? photoIds,
    Set<String>? albumIds,
    Set<String>? commentIds,
    bool? commentsAllowed,
    DateTime? createdAt,
    DateTime? lastModified,
  }) {
    return Post(
      id: id ?? this.id,
      ownerId: ownerId ?? this.ownerId,
      photoIds: photoIds ?? this.photoIds,
      albumIds: albumIds ?? this.albumIds,
      commentIds: commentIds ?? this.commentIds,
      commentsAllowed: commentsAllowed ?? this.commentsAllowed,
      createdAt: createdAt ?? this.createdAt,
      lastModified: lastModified ?? this.lastModified,
    );
  }
}