import 'package:flutter/material.dart';

import '../../../models/user.dart';
import '../../../repositories/user_repository.dart';


class UserAvatar extends StatelessWidget {
  const UserAvatar({
    super.key,
    required this.userId,
    required this.userRepository,
    this.radius = 20,
  });

  final String userId;
  final UserRepository userRepository;
  final double radius;

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<User?>(
      future: userRepository.getUserById(userId),
      builder: (context, snapshot) {
        final user = snapshot.data;
        final isLoading = snapshot.connectionState == ConnectionState.waiting;
        final profilePictureUrl = user?.profilePictureUrl;
        final initial = user?.initial ?? '?';

        if (isLoading) {
          return CircleAvatar(
            radius: radius,
            backgroundColor: Colors.grey,
            child: const SizedBox(
              width: 16,
              height: 16,
              child: CircularProgressIndicator(
                strokeWidth: 2,
                valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
              ),
            ),
          );
        }

        if (profilePictureUrl != null && profilePictureUrl.isNotEmpty) {
          return CircleAvatar(
            radius: radius,
            backgroundImage: NetworkImage(profilePictureUrl),
            backgroundColor: Colors.grey.shade200,
          );
        }

        return CircleAvatar(
          radius: radius,
          backgroundColor: Theme.of(context).primaryColor,
          child: Text(
            initial,
            style: TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: radius * 0.9,
            ),
          ),
        );
      },
    );
  }
}
