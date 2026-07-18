class Comment {
  final String? id;
  final String ownerId;
  final String script;
  final String photoId;

  const Comment({
    this.id,
    required this.ownerId,
    required this.script,
    required this.photoId,
  });

  Comment copyWith({
    String? id,
    String? ownerId,
    String? script,
    String? photoId,
  }) {
    return Comment(
      id: id ?? this.id,
      ownerId: ownerId ?? this.ownerId,
      script: script ?? this.script,
      photoId: photoId ?? this.photoId,
    );
  }

  factory Comment.fromJson(Map<String, dynamic> json) {
    return Comment(
      id: json['id'] as String?,
      ownerId: json['ownerId'] as String,
      script: json['script'] as String,
      photoId: json['photoId'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'ownerId': ownerId,
      'script': script,
      'photoId': photoId,
    };
  }
}
