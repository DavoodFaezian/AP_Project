import 'package:flutter/material.dart';

import 'comment_screen.dart';

// Using a Dart Record to concisely represent your Java class data
typedef ImageData = ({
String photoName,
Set<String> tags,
String caption,
bool isFavorable,
bool permissionForLeavingComment,
Set<String> commentIds,
Set<String> sharedUserIds,
DateTime createdAt,
});

class ImageDetailScreen extends StatelessWidget {
  final ImageData image;

  // Utilizing named parameters in the constructor
  const ImageDetailScreen({
    super.key,
    required this.image,
  });

  // Async method to handle routing
  Future<void> _navigateToComments(BuildContext context) async {
    await Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => CommentsScreen(commentIds: image.commentIds),
      ),
    );
  }

  // Helper method specifically designed to be passed as a tear-off
  Widget _buildTag(String tag) => Chip(label: Text(tag));

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(image.photoName),
        actions: [
          Icon(
            image.isFavorable ? Icons.favorite : Icons.favorite_border,
            color: image.isFavorable ? Colors.red : null,
          ),
          const SizedBox(width: 16),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              image.caption,
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 8),
            Text('Created: ${image.createdAt.toString().split(' ')[0]}'),
            const SizedBox(height: 16),

            // Using a method tear-off (_buildTag) to map over the Set
            Wrap(
              spacing: 8.0,
              children: image.tags.map(_buildTag).toList(),
            ),
            const SizedBox(height: 16),
            Text('Shared with ${image.sharedUserIds.length} user(s)'),

            const Spacer(),

            // Only render the button if the user has permission
            if (image.permissionForLeavingComment)
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () => _navigateToComments(context),
                  child: const Text('View Comments'),
                ),
              ),
          ],
        ),
      ),
    );
  }
}

