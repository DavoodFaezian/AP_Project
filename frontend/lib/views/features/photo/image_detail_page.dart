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
          backgroundColor: Colors.white,
          appBar: AppBar(
            backgroundColor: Colors.white,
            elevation: 0,
            iconTheme: Theme.of(context).iconTheme,
            title: Text(
              displayTitle,
              style: const TextStyle(color: Colors.black, fontWeight: FontWeight.bold),
            ),
            actions: [
              IconButton(
                icon: Icon(
                  photo.isFavorable ? Icons.favorite : Icons.favorite_border,
                  color: photo.isFavorable ? Colors.red : Colors.black87,
                ),
                onPressed: () {
                  // TODO: Implement toggle favorite
                },
              ),
              const SizedBox(width: 8),
            ],
          ),
          body: SingleChildScrollView(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Image Section
                Hero(
                  tag: 'photo_${photo.id}',
                  child: Container(
                    width: double.infinity,
                    constraints: BoxConstraints(
                      maxHeight: MediaQuery.of(context).size.height * 0.6,
                    ),
                    child: SocketImage(
                      photoName: photo.photoName,
                      sessionId: SessionManager.instance.sessionId!,
                      ownerId: photo.ownerId,
                      loadingPlaceholder: const Center(child: CircularProgressIndicator()),
                      errorPlaceholder: Container(
                        color: Colors.grey.shade100,
                        child: const Icon(Icons.broken_image, size: 64, color: Colors.grey),
                      ),
                      builder: (context, provider) => InteractiveViewer(
                        minScale: 1.0,
                        maxScale: 3.0,
                        child: Image(
                          image: provider,
                          fit: BoxFit.contain,
                        ),
                      ),
                    ),
                  ),
                ),

                Padding(
                  padding: const EdgeInsets.all(20),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      if (photo.title.isNotEmpty) ...[
                        Text(
                          photo.title,
                          style: const TextStyle(
                            fontSize: 24,
                            fontWeight: FontWeight.bold,
                            letterSpacing: -0.5,
                          ),
                        ),
                        const SizedBox(height: 12),
                      ],
                      if (photo.caption.isNotEmpty) ...[
                        Text(
                          photo.caption,
                          style: TextStyle(
                            fontSize: 16,
                            color: Colors.grey.shade800,
                            height: 1.5,
                          ),
                        ),
                        const SizedBox(height: 24),
                      ],

                      // Metadata Section
                      Row(
                        children: [
                          _buildInfoChip(
                            context,
                            Icons.calendar_today_outlined,
                            photo.createdAt.toString().split(' ')[0],
                          ),
                          const SizedBox(width: 12),
                          _buildInfoChip(
                            context,
                            Icons.share_outlined,
                            '${photo.postIds.length} post(s)',
                          ),
                        ],
                      ),

                      const SizedBox(height: 24),

                      // Tags Section
                      if (photo.tags.isNotEmpty) ...[
                        const Text(
                          'Tags',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 12),
                        Wrap(
                          spacing: 8,
                          runSpacing: 8,
                          children: photo.tags
                              .map((tag) => _buildTag(tag, context))
                              .toList(),
                        ),
                      ],

                      const SizedBox(height: 40),
                    ],
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildInfoChip(BuildContext context, IconData icon, String label) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(
        color: Colors.grey.shade100,
        borderRadius: BorderRadius.circular(10),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 16, color: Colors.grey.shade600),
          const SizedBox(width: 6),
          Text(
            label,
            style: TextStyle(
              fontSize: 13,
              color: Colors.grey.shade700,
              fontWeight: FontWeight.w500,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTag(String tag, BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.primary.withOpacity(0.1),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(
          color: Theme.of(context).colorScheme.primary.withOpacity(0.3),
        ),
      ),
      child: Text(
        '#$tag',
        style: TextStyle(
          color: Theme.of(context).colorScheme.primary,
          fontSize: 13,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }
}


