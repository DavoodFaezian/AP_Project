class Photo {
  const Photo({
    required this.id,
    required this.ownerId,
    required this.photoName,
    this.title = '',
    this.tags = const {},
    this.caption = '',
    this.isFavorable = false,
    this.lastModified,
    this.commentIds = const {},
    this.albumIds = const {},
    this.postIds = const {},
    required this.createdAt,
  });

  final String id;
  final String ownerId;
  final String photoName;
  final String title;
  final Set<String> tags;
  final String caption;
  final bool isFavorable;
  final DateTime? lastModified;
  final Set<String> commentIds;
  final Set<String> albumIds;
  final Set<String> postIds;
  final DateTime createdAt;

  Photo copyWith({
    String? id,
    String? ownerId,
    String? photoName,
    String? title,
    Set<String>? tags,
    String? caption,
    bool? isFavorable,
    DateTime? lastModified,
    Set<String>? commentIds,
    Set<String>? albumIds,
    Set<String>? postIds,
    DateTime? createdAt,
  }) {
    return Photo(
      id: id ?? this.id,
      ownerId: ownerId ?? this.ownerId,
      photoName: photoName ?? this.photoName,
      title: title ?? this.title,
      tags: tags ?? this.tags,
      caption: caption ?? this.caption,
      isFavorable: isFavorable ?? this.isFavorable,
      lastModified: lastModified ?? this.lastModified,
      commentIds: commentIds ?? this.commentIds,
      albumIds: albumIds ?? this.albumIds,
      postIds: postIds ?? this.postIds,
      createdAt: createdAt ?? this.createdAt,
    );
  }

  factory Photo.fromJson(Map<String, dynamic> json) {
    return Photo(
      id: json['id'] as String,
      ownerId: json['ownerId'] as String,
      photoName: json['photoName'] as String,
      title: json['title'] as String? ?? '',
      tags: Set<String>.from(json['tags'] ?? const []),
      caption: json['caption'] as String? ?? '',
      isFavorable: (json['isFavorable'] ?? json['favorable']) as bool? ?? false,
      lastModified: DateTime.now(),
      commentIds: Set<String>.from(json['commentIds'] ?? const []),
      albumIds: Set<String>.from(json['albumIds'] ?? const []),
      postIds: Set<String>.from(json['postIds'] ?? const []),
      createdAt: DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'ownerId': ownerId,
      'photoName': photoName,
      'title': title,
      'tags': tags.toList(),
      'caption': caption,
      'isFavorable': isFavorable,
      'lastModified': lastModified?.toIso8601String(),
      'commentIds': commentIds.toList(),
      'albumIds': albumIds.toList(),
      'postIds': postIds.toList(),
      'createdAt': createdAt.toIso8601String(),
    };
  }
}
