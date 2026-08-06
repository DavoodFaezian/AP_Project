import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:test_app/models/album.dart';
import 'package:test_app/models/photo.dart';
import 'package:test_app/models/post.dart';
import 'package:test_app/models/user_profile.dart';
import 'package:test_app/repositories/album_repository.dart';
import 'package:test_app/repositories/photo_repository.dart';
import 'package:test_app/repositories/post_repository.dart';
import 'package:test_app/repositories/user_repository.dart';
import 'package:test_app/services/session_manager.dart';
import 'package:test_app/views/components/widgets/socket_image.dart';
import 'package:test_app/views/features/photo/photo_slider_page.dart';
import 'package:test_app/views/features/photo/image_detail_page.dart';
import 'package:test_app/views/features/post/post_form_page.dart';
import 'package:test_app/views/layout/screens/home/comment_screen.dart';

import '../profile/profile_screen.dart';

class PostItem extends StatefulWidget {
  final Post post;
  final PhotoRepository photoRepository;
  final AlbumRepository albumRepository;
  final PostRepository postRepository;
  final bool showActions;
  final VoidCallback? onRefresh;
  final Function(String postId, String ownerId)? onPostUpdated;

  const PostItem({
    super.key,
    required this.post,
    required this.photoRepository,
    required this.albumRepository,
    required this.postRepository,
    this.showActions = false,
    this.onRefresh,
    this.onPostUpdated,
  });

  @override
  State<PostItem> createState() => _PostItemState();
}

class _PostItemState extends State<PostItem> with AutomaticKeepAliveClientMixin {
  List<Photo> _allPhotos = [];
  List<Album> _albums = [];
  UserProfile? _owner;
  bool _isLoading = true;
  int _currentImageIndex = 0;
  final PageController _pageController = PageController();

  @override
  bool get wantKeepAlive => true;

  @override
  void initState() {
    super.initState();
    _fetchData();
  }

  @override
  void didUpdateWidget(covariant PostItem oldWidget) {
    super.didUpdateWidget(oldWidget);
    
    final bool idChanged = oldWidget.post.id != widget.post.id;
    final bool modifiedChanged = oldWidget.post.lastModified != widget.post.lastModified;
    final bool photosChanged = !setEquals(oldWidget.post.photoIds, widget.post.photoIds);
    final bool albumsChanged = !setEquals(oldWidget.post.albumIds, widget.post.albumIds);
    final bool commentsChanged = oldWidget.post.commentsAllowed != widget.post.commentsAllowed ||
                                !setEquals(oldWidget.post.commentIds, widget.post.commentIds);

    if (idChanged || modifiedChanged || photosChanged || albumsChanged || commentsChanged) {
      debugPrint("PostItem: Refreshing data for post ${widget.post.id}");
      _fetchData();
    }
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  Future<void> _fetchData() async {
    if (!mounted) return;
    
    setState(() {
      _isLoading = true;
    });

    try {
      // Fetch owner profile (non-blocking)
      UserRepository().getUserProfileById(widget.post.ownerId).then((owner) {
        if (mounted) setState(() => _owner = owner);
      }).catchError((e) => debugPrint("Error fetching owner profile: $e"));

      // Fetch albums individually
      List<Album> loadedAlbums = [];
      for (final albumId in widget.post.albumIds) {
        try {
          final album = await widget.albumRepository.getAlbumById(albumId, widget.post.ownerId);
          loadedAlbums.add(album);
        } catch (e) {
          debugPrint("Error fetching album $albumId: $e");
        }
      }

      // Fetch photos from albums
      List<Photo> albumPhotos = [];
      for (final album in loadedAlbums) {
        try {
          final photos = await widget.photoRepository.getPhotosByAlbumId(album.id, widget.post.ownerId);
          albumPhotos.addAll(photos);
        } catch (e) {
          debugPrint("Error fetching photos for album ${album.id}: $e");
        }
      }

      // Fetch specific photoIds
      List<Photo> specificPhotos = [];
      for (final photoId in widget.post.photoIds) {
        try {
          final photo = await widget.photoRepository.getPhotoById(photoId, widget.post.ownerId);
          specificPhotos.add(photo);
        } catch (e) {
          debugPrint("Error fetching specific photo $photoId: $e");
        }
      }

      // Combine and remove duplicates
      final combined = [...albumPhotos, ...specificPhotos];
      final seenIds = <String>{};
      final uniquePhotos = combined.where((p) => seenIds.add(p.id)).toList();

      if (mounted) {
        setState(() {
          _albums = loadedAlbums;
          _allPhotos = uniquePhotos;
          _currentImageIndex = 0;
          _isLoading = false;
        });
      }
    } catch (e) {
      debugPrint("Error fetching post data: $e");
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  void _openPhotoSlider(String photoId, {List<Photo>? items}) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => PhotoSliderPage(
          items: items ?? _allPhotos,
          initialItemId: photoId,
          idBuilder: (photo) => photo.id,
          titleBuilder: (photo) => photo.title.isNotEmpty ? photo.title : photo.photoName,
          imageBuilder: (photo) {
            return SocketImage(
              photoName: photo.photoName,
              sessionId: SessionManager.instance.sessionId!,
              ownerId: photo.ownerId,
              loadingPlaceholder: const Center(child: CircularProgressIndicator()),
              errorPlaceholder: const Icon(Icons.broken_image, color: Colors.white, size: 48),
              builder: (context, provider) => Image(
                image: provider,
                fit: BoxFit.contain,
              ),
            );
          },
          onEyePressed: () {
            Navigator.of(context).push(
              MaterialPageRoute(
                builder: (_) => ImageDetailPage(
                  photoId: photoId,
                  ownerId: widget.post.ownerId,
                  photoRepository: widget.photoRepository,
                ),
              ),
            );
          },
        ),
      ),
    );
  }

  void _editPost() async {
    final result = await Navigator.push<bool>(
      context,
      MaterialPageRoute(
        builder: (context) => PostFormPage(
          photoRepository: widget.photoRepository,
          albumRepository: widget.albumRepository,
          postRepository: widget.postRepository,
          initialPost: widget.post,
        ),
      ),
    );

    if (result == true) {
      if (widget.onPostUpdated != null) {
        widget.onPostUpdated!(widget.post.id, widget.post.ownerId);
      } else if (widget.onRefresh != null) {
        widget.onRefresh!();
      }
    }
  }

  void _deletePost() async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Delete Post"),
        content: const Text("Are you sure you want to delete this post?"),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text("Cancel")),
          TextButton(
            onPressed: () => Navigator.pop(context, true), 
            child: const Text("Delete", style: TextStyle(color: Colors.red))
          ),
        ],
      ),
    );

    if (confirmed == true) {
      try {
        await widget.postRepository.deletePost(widget.post.id);
        if (widget.onRefresh != null) {
          widget.onRefresh!();
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(e.toString())));
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20), // More rounded corners
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.06),
            blurRadius: 20,
            offset: const Offset(0, 10),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            child: Row(
              children: [
                GestureDetector(
                  onTap: () => Navigator.push(
                    context,
                    MaterialPageRoute(builder: (_) => ProfileScreen(userId: widget.post.ownerId)),
                  ),
                  child: CircleAvatar(
                    radius: 20,
                    backgroundColor: const Color(0xFFF3E8FF),
                    child: _owner?.profilePhotoName != null
                        ? ClipOval(
                      child: SizedBox.expand(
                        child: SocketImage(
                          photoName: _owner!.profilePhotoName!,
                          sessionId: SessionManager.instance.sessionId!,
                          ownerId: _owner!.userId,
                          builder: (context, provider) => Image(
                            image: provider,
                            fit: BoxFit.cover,
                          ),
                        ),
                      ),
                    )
                        : const Icon(Icons.person, size: 24, color: Color(0xFF5B21B6)),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: GestureDetector(
                    onTap: () => Navigator.push(
                      context,
                      MaterialPageRoute(builder: (_) => ProfileScreen(userId: widget.post.ownerId)),
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          _owner?.userName ?? widget.post.ownerId,
                          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                        ),
                        if (widget.post.createdAt != null)
                          Text(
                            widget.post.createdAt!.toString().split(' ')[0],
                            style: TextStyle(color: Colors.grey.shade500, fontSize: 11),
                          ),
                      ],
                    ),
                  ),
                ),
                if (widget.showActions)
                  IconButton(
                    icon: const Icon(Icons.more_horiz, size: 22),
                    onPressed: () {
// ...
                       showModalBottomSheet(
                         context: context, 
                         builder: (ctx) => SafeArea(
                           child: Wrap(
                             children: [
                               ListTile(
                                 leading: const Icon(Icons.edit),
                                 title: const Text('Edit'),
                                 onTap: () {
                                   Navigator.pop(ctx);
                                   _editPost();
                                 },
                               ),
                               ListTile(
                                 leading: const Icon(Icons.delete, color: Colors.red),
                                 title: const Text('Delete', style: TextStyle(color: Colors.red)),
                                 onTap: () {
                                   Navigator.pop(ctx);
                                   _deletePost();
                                 },
                               ),
                             ],
                           ),
                         )
                       );
                    },
                  ),
              ],
            ),
          ),

          // Main Content (Image Slider)
          if (_isLoading)
            const AspectRatio(
              aspectRatio: 1,
              child: Center(child: CircularProgressIndicator()),
            )
          else if (_allPhotos.isNotEmpty)
            Column(
              children: [
                AspectRatio(
                  aspectRatio: 1,
                  child: Container(
                    color: Colors.grey.shade50,
                    child: PageView.builder(
                      controller: _pageController,
                      itemCount: _allPhotos.length,
                      onPageChanged: (index) {
                        setState(() {
                          _currentImageIndex = index;
                        });
                      },
                      itemBuilder: (context, index) {
                        final photo = _allPhotos[index];
                        return GestureDetector(
                          onTap: () => _openPhotoSlider(photo.id),
                          child: SocketImage(
                            photoName: photo.photoName,
                            sessionId: SessionManager.instance.sessionId!,
                            ownerId: photo.ownerId,
                            loadingPlaceholder: const Center(child: CircularProgressIndicator()),
                            errorPlaceholder: Container(
                              color: Colors.grey.shade100,
                              child: const Icon(Icons.broken_image, size: 48, color: Colors.grey),
                            ),
                            builder: (context, provider) => Image(
                              image: provider,
                              fit: BoxFit.contain, // Show full width and height
                              width: double.infinity,
                            ),
                          ),
                        );
                      },
                    ),
                  ),
                ),
                if (_allPhotos.length > 1)
                  Padding(
                    padding: const EdgeInsets.symmetric(vertical: 8),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: List.generate(_allPhotos.length, (index) {
                        return Container(
                          width: 6,
                          height: 6,
                          margin: const EdgeInsets.symmetric(horizontal: 2),
                          decoration: BoxDecoration(
                            shape: BoxShape.circle,
                            color: _currentImageIndex == index 
                              ? Colors.blue 
                              : Colors.grey.shade300,
                          ),
                        );
                      }),
                    ),
                  ),
              ],
            )
          else
            Container(
              height: 200,
              width: double.infinity,
              color: Colors.grey.shade100,
              child: const Center(child: Text("No photos in this post")),
            ),

          // Interaction Bar
          if (widget.post.commentsAllowed)
            Padding(
              padding: const EdgeInsets.fromLTRB(12, 4, 12, 4),
              child: Row(
                children: [
                  IconButton(
                    padding: EdgeInsets.zero,
                    constraints: const BoxConstraints(),
                    icon: const Icon(Icons.chat_bubble_outline, size: 24, color: Colors.black87),
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => CommentsScreen(
                            postId: widget.post.id,
                            postOwnerId: widget.post.ownerId,
                          ),
                        ),
                      );
                    },
                  ),
                  const SizedBox(width: 4),
                  Text(
                    widget.post.commentIds.length.toString(),
                    style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w500),
                  ),
                ],
              ),
            ),

          // Albums Section
          if (_albums.isNotEmpty)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
              child: Wrap(
                spacing: 6,
                children: _albums.map((album) => GestureDetector(
                  onTap: () {
                    final albumPhotos = _allPhotos.where((p) => album.photoIds.contains(p.id)).toList();
                    if (albumPhotos.isNotEmpty) {
                      _openPhotoSlider(albumPhotos.first.id, items: albumPhotos);
                    }
                  },
                  child: Text(
                    "#${album.albumName}",
                    style: const TextStyle(
                      color: Colors.blue, 
                      fontWeight: FontWeight.w600,
                      fontSize: 13,
                    ),
                  ),
                )).toList(),
              ),
            ),

          // Date removed from bottom since it's now in the header
        ],
      ),
    );
  }
}
