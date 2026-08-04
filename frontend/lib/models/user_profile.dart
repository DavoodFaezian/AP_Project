class UserProfile {
  final String userId;
  final String userName;
  final String? profilePhotoName;
  final Set<String> followerIds;
  final Set<String> followingIds;

  const UserProfile({
    required this.userId,
    required this.userName,
    this.profilePhotoName,
    required this.followerIds,
    required this.followingIds,
  });

  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      userId: (json['userId'] ?? json['id'] ?? '').toString(),
      userName: (json['userName'] ?? json['username'] ?? '').toString(),
      profilePhotoName: json['profilePhotoName']?.toString(),
      followerIds: ((json['followerIds'] ?? []) as List)
          .map((e) => e.toString())
          .toSet(),
      followingIds: ((json['followingIds'] ?? []) as List)
          .map((e) => e.toString())
          .toSet(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'userName': userName,
      if (profilePhotoName != null) 'profilePhotoName': profilePhotoName,
      'followerIds': followerIds.toList(),
      'followingIds': followingIds.toList(),
    };
  }
}