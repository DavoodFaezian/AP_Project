import 'package:flutter/material.dart';
import 'package:test_app/models/post.dart';
import 'package:test_app/repositories/album_repository.dart';
import 'package:test_app/repositories/photo_repository.dart';
import 'package:test_app/repositories/post_repository.dart';
import 'package:test_app/views/components/widgets/empty_screen.dart';
import 'post_item.dart';

class PostList extends StatelessWidget {
  final List<Post> posts;
  final bool isLoading;
  final PhotoRepository photoRepository;
  final AlbumRepository albumRepository;
  final PostRepository postRepository;
  final bool showActions;
  final VoidCallback? onRefresh;
  final Function(String postId, String ownerId)? onPostUpdated;

  const PostList({
    super.key,
    required this.posts,
    required this.isLoading,
    required this.photoRepository,
    required this.albumRepository,
    required this.postRepository,
    this.showActions = false,
    this.onRefresh,
    this.onPostUpdated,
  });

  @override
  Widget build(BuildContext context) {
    if (isLoading && posts.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }

    if (posts.isEmpty) {
      return const EmptyState(
        imagePath: 'assets/images/Image post-cuate.png',
        title: "No posts yet",
        subtitle: "Start by creating your first post!",
      );
    }

    return ListView.builder(
      itemCount: posts.length,
      padding: const EdgeInsets.only(bottom: 80),
      // Adding a physics that might help if there are conflicts
      physics: const AlwaysScrollableScrollPhysics(),
      itemBuilder: (context, index) {
        final post = posts[index];
        return PostItem(
          key: ValueKey("${post.id}_${post.lastModified?.millisecondsSinceEpoch ?? 0}"), // Dynamic key to force refresh
          post: post,
          photoRepository: photoRepository,
          albumRepository: albumRepository,
          postRepository: postRepository,
          showActions: showActions,
          onRefresh: onRefresh,
          onPostUpdated: onPostUpdated,
        );
      },
    );
  }
}
