class Comment {
  final String id;
  final String ownerId;
  final String script;
  final String postId;

  const Comment({
    required this.id,
    required this.ownerId,
    required this.script,
    required this.postId,
  });

  Comment copyWith({
    String? id,
    String? ownerId,
    String? script,
    String? postId,
  }) {
    return Comment(
      id: id ?? this.id,
      ownerId: ownerId ?? this.ownerId,
      script: script ?? this.script,
      postId: postId ?? this.postId,
    );
  }

  factory Comment.fromJson(Map<String, dynamic> json) {
    return Comment(
      id: (json['id'] ?? '').toString(),
      ownerId: (json['ownerId'] ?? '').toString(),
      script: (json['script'] ?? '').toString(),
      postId: (json['postId'] ?? '').toString(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'ownerId': ownerId,
      'script': script,
      'postId': postId,
    };
  }
}
