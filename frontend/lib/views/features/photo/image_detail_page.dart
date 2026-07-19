import 'package:flutter/material.dart';

import '../../../models/photo.dart';
import '../../../repositories/photo_repository.dart';
import '../../layout/screens/home/comment_screen.dart';

class ImageDetailPage extends StatefulWidget {
  const ImageDetailPage({
    super.key,
    required this.photoId,
    required this.photoRepository,
  });

  final String photoId;
  final PhotoRepository photoRepository;

  @override
  State<ImageDetailPage> createState() => _ImageDetailPageState();
}

class _ImageDetailPageState extends State<ImageDetailPage> {
  late Future<Photo> _photoFuture;

  @override
  void initState() {
    super.initState();
    _photoFuture = widget.photoRepository.getPhotoById(widget.photoId);
  }

  Future<void> _navigateToComments(
      BuildContext context,
      Set<String> commentIds,
      ) async {
    await Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => CommentsScreen(commentIds: commentIds),
      ),
    );
  }

  Widget _buildTag(String tag) => Chip(label: Text(tag));

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<Photo>(
      future: _photoFuture,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Scaffold(
            body: Center(child: CircularProgressIndicator()),
          );
        }

        if (snapshot.hasError) {
          return Scaffold(
            appBar: AppBar(),
            body: Center(
              child: Text('Failed to load photo: ${snapshot.error}'),
            ),
          );
        }

        final photo = snapshot.data;
        if (photo == null) {
          return Scaffold(
            appBar: AppBar(),
            body: const Center(
              child: Text('Photo not found'),
            ),
          );
        }

        return Scaffold(
          appBar: AppBar(
            title: Text(photo.photoName),
            actions: [
              Icon(
                photo.isFavorable ? Icons.favorite : Icons.favorite_border,
                color: photo.isFavorable ? Colors.red : null,
              ),
              const SizedBox(width: 16),
            ],
          ),
          body: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  photo.caption,
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const SizedBox(height: 8),
                Text(
                  'Created: ${photo.createdAt.toString().split(' ')[0]}',
                ),
                const SizedBox(height: 16),
                Wrap(
                  spacing: 8,
                  runSpacing: 8,
                  children: photo.tags.map(_buildTag).toList(),
                ),
                const SizedBox(height: 16),
                Text('Shared with ${photo.sharedUserIds.length} user(s)'),
                const Spacer(),
                if (photo.permissionForLeavingComment)
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      onPressed: () => _navigateToComments(
                        context,
                        photo.commentIds,
                      ),
                      child: const Text('View Comments'),
                    ),
                  ),
              ],
            ),
          ),
        );
      },
    );
  }
}
