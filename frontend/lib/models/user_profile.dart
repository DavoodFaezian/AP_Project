class UserProfile {
  final String userId;
  final String userName;
  final String? profilePhotoName;

  const UserProfile({
    required this.userId,
    required this.userName,
    this.profilePhotoName,
  });

  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      userId: (json['userId'] ?? json['id'] ?? '').toString(),
      userName: (json['userName'] ?? json['username'] ?? '').toString(),
      profilePhotoName: json['profilePhotoName']?.toString(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'userName': userName,
      if (profilePhotoName != null) 'profilePhotoName': profilePhotoName,
    };
  }
}
