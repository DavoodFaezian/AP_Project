class Album {
  const Album({
    required this.id,
    required this.albumName,
    this.photoIds = const {},
  });

  final String id;
  final String albumName;
  final Set<String> photoIds;

  Album copyWith({
    String? id,
    String? albumName,
    Set<String>? photoIds,
  }) {
    return Album(
      id: id ?? this.id,
      albumName: albumName ?? this.albumName,
      photoIds: photoIds ?? this.photoIds,
    );
  }

  factory Album.fromJson(Map<String, dynamic> json) {
    return Album(
      id: json['id'] as String,
      albumName: json['albumName'] as String,
      photoIds: Set<String>.from(json['photoIds'] ?? const []),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'albumName': albumName,
      'photoIds': photoIds.toList(),
    };
  }
}
