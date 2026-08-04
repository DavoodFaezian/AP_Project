import 'package:flutter/material.dart';
import 'package:test_app/services/session_manager.dart';
import 'package:test_app/views/components/widgets/socket_image.dart';

import '../../../models/photo.dart';
import '../../../repositories/photo_repository.dart';
import '../../layout/screens/home/comment_screen.dart';

class ImageDetailPage extends StatefulWidget {
  const ImageDetailPage({
    super.key,
    required this.photoId,
    required this.ownerId,
    required this.photoRepository,
  });

  final String photoId;
  final String ownerId;
  final PhotoRepository photoRepository;

  @override
  State<ImageDetailPage> createState() => _ImageDetailPageState();
}

class _ImageDetailPageState extends State<ImageDetailPage> {
  late Future<Photo> _photoFuture;

  @override
  void initState() {
    super.initState();
    _photoFuture = widget.photoRepository.getPhotoById(widget.photoId, widget.ownerId);
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

        final displayTitle = photo.title.isNotEmpty ? photo.title : photo.photoName;

        return Scaffold(
          appBar: AppBar(
            title: Text(displayTitle),
            actions: [
              IconButton(
                icon: Icon(
                  photo.isFavorable ? Icons.favorite : Icons.favorite_border,
                  color: photo.isFavorable ? Colors.red : null,
                ),
                onPressed: () {
                  // TODO: Toggle favorite
                },
              ),
              const SizedBox(width: 8),
            ],
          ),
          body: SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                ClipRRect(
                  borderRadius: BorderRadius.circular(12),
                  child: AspectRatio(
                    aspectRatio: 16 / 9,
                    child: SocketImage(
                      photoName: photo.photoName,
                      sessionId: SessionManager.instance.sessionId!,
                      ownerId: photo.ownerId,
                      loadingPlaceholder: const Center(child: CircularProgressIndicator()),
                      errorPlaceholder: Container(
                        color: Colors.grey.shade200,
                        child: const Icon(Icons.broken_image, size: 48, color: Colors.grey),
                      ),
                      builder: (context, provider) => Image(
                        image: provider,
                        fit: BoxFit.cover,
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 24),
                if (photo.title.isNotEmpty) ...[
                  Text(
                    photo.title,
                    style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 8),
                ],
                if (photo.caption.isNotEmpty) ...[
                  Text(
                    photo.caption,
                    style: Theme.of(context).textTheme.bodyLarge,
                  ),
                  const SizedBox(height: 16),
                ],
                Text(
                  'Created: ${photo.createdAt.toString().split(' ')[0]}',
                  style: Theme.of(context).textTheme.bodySmall,
                ),
                const SizedBox(height: 16),
                if (photo.tags.isNotEmpty)
                  Wrap(
                    spacing: 8,
                    runSpacing: 8,
                    children: photo.tags.map(_buildTag).toList(),
                  ),
                const SizedBox(height: 16),
                Text('Shared with ${photo.postIds.length} post(s)'),
              ],
            ),
          ),
        );
      },
    );
  }
}

