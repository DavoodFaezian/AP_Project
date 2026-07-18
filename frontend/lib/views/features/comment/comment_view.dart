import 'package:flutter/material.dart';

import '../../../models/comment.dart';
import '../../../models/user.dart';
import '../../../repositories/user_repository.dart';
import '../user/user_avatar.dart';

class CommentView extends StatelessWidget {
  const CommentView({
    super.key,
    required this.comment,
    required this.userRepository,
  });

  final Comment comment;
  final UserRepository userRepository;

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<User?>(
      future: userRepository.getUserById(comment.ownerId),
      builder: (context, snapshot) {
        final user = snapshot.data;
        final isLoading = snapshot.connectionState == ConnectionState.waiting;
        final displayName = isLoading
            ? 'Loading...'
            : (user?.userName ?? 'Unknown user');

        return Padding(
          padding: const EdgeInsets.symmetric(vertical: 8, horizontal: 16),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              UserAvatar(
                userId: comment.ownerId,
                userRepository: userRepository,
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      displayName,
                      style: const TextStyle(
                        fontSize: 14,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      comment.script,
                      style: const TextStyle(
                        fontSize: 14,
                        color: Colors.black87,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        );
      },
    );
  }
}
