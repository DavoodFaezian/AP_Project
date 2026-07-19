class Photo {
  const Photo({
    required this.id,
    required this.ownerId,
    required this.photoName,
    this.tags = const {},
    this.caption = '',
    this.isFavorable = false,
    this.permissionForLeavingComment = true,
    this.dateOfShare,
    this.lastModified,
    this.commentIds = const {},
    this.albumIds = const {},
    this.sharedUserIds = const {},
    required this.createdAt,
    this.photoUrl,
  });

  final String id;
  final String ownerId;
  final String photoName;
  final Set<String> tags;
  final String caption;
  final bool isFavorable;
  final bool permissionForLeavingComment;
  final DateTime? dateOfShare;
  final DateTime? lastModified;
  final Set<String> commentIds;
  final Set<String> albumIds;
  final Set<String> sharedUserIds;
  final DateTime createdAt;
  final String? photoUrl;

  Photo copyWith({
    String? id,
    String? ownerId,
    String? photoName,
    Set<String>? tags,
    String? caption,
    bool? isFavorable,
    bool? permissionForLeavingComment,
    DateTime? dateOfShare,
    DateTime? lastModified,
    Set<String>? commentIds,
    Set<String>? albumIds,
    Set<String>? sharedUserIds,
    DateTime? createdAt,
    String? photoUrl,
  }) {
    return Photo(
      id: id ?? this.id,
      ownerId: ownerId ?? this.ownerId,
      photoName: photoName ?? this.photoName,
      tags: tags ?? this.tags,
      caption: caption ?? this.caption,
      isFavorable: isFavorable ?? this.isFavorable,
      permissionForLeavingComment:
      permissionForLeavingComment ?? this.permissionForLeavingComment,
      dateOfShare: dateOfShare ?? this.dateOfShare,
      lastModified: lastModified ?? this.lastModified,
      commentIds: commentIds ?? this.commentIds,
      albumIds: albumIds ?? this.albumIds,
      sharedUserIds: sharedUserIds ?? this.sharedUserIds,
      createdAt: createdAt ?? this.createdAt,
      photoUrl: photoUrl ?? this.photoUrl,
    );
  }

  factory Photo.fromJson(Map<String, dynamic> json) {
    return Photo(
      id: json['id'] as String,
      ownerId: json['ownerId'] as String,
      photoName: json['photoName'] as String,
      tags: Set<String>.from(json['tags'] ?? const []),
      caption: json['caption'] as String? ?? '',
      isFavorable: json['isFavorable'] as bool? ?? false,
      permissionForLeavingComment:
      json['permissionForLeavingComment'] as bool? ?? true,
      dateOfShare: DateTime.parse(json['dateOfShare'] as String),
      lastModified: DateTime.parse(json['lastModified'] as String),
      commentIds: Set<String>.from(json['commentIds'] ?? const []),
      albumIds: Set<String>.from(json['albumIds'] ?? const []),
      sharedUserIds: Set<String>.from(json['sharedUserIds'] ?? const []),
      createdAt: DateTime.parse(json['createdAt'] as String),
      photoUrl: json['photoUrl'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'ownerId': ownerId,
      'photoName': photoName,
      'tags': tags.toList(),
      'caption': caption,
      'isFavorable': isFavorable,
      'permissionForLeavingComment': permissionForLeavingComment,
      'dateOfShare': dateOfShare?.toIso8601String(),
      'lastModified': lastModified?.toIso8601String(),
      'commentIds': commentIds.toList(),
      'albumIds': albumIds.toList(),
      'sharedUserIds': sharedUserIds.toList(),
      'createdAt': createdAt.toIso8601String(),
      'photoUrl': photoUrl,
    };
  }
}
