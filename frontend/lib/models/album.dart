class Album {
  const Album({
    required this.id,
    required this.albumName,
    required this.ownerId,
    this.photoIds = const {},
  });

  final String id;
  final String albumName;
  final String ownerId;
  final Set<String> photoIds;

  Album copyWith({
    String? id,
    String? albumName,
    String? ownerId,
    Set<String>? photoIds,
  }) {
    return Album(
      id: id ?? this.id,
      albumName: albumName ?? this.albumName,
      ownerId: ownerId ?? this.ownerId,
      photoIds: photoIds ?? this.photoIds,
    );
  }

  factory Album.fromJson(Map<String, dynamic> json) {
    return Album(
      id: (json['id'] ?? '').toString(),
      albumName: (json['albumName'] ?? json['name'] ?? '').toString(),
      ownerId: (json['ownerId'] ?? '').toString(),
      photoIds: Set<String>.from(json['photoIds'] ?? const []),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'albumName': albumName,
      'ownerId': ownerId,
      'photoIds': photoIds.toList(),
    };
  }
}
