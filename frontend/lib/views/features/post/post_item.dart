import 'package:flutter/material.dart';
import 'package:test_app/models/album.dart';
import 'package:test_app/models/photo.dart';
import 'package:test_app/models/post.dart';
import 'package:test_app/models/user.dart';
import 'package:test_app/models/user_profile.dart';
import 'package:test_app/repositories/album_repository.dart';
import 'package:test_app/repositories/photo_repository.dart';
import 'package:test_app/repositories/post_repository.dart';
import 'package:test_app/repositories/user_repository.dart';
import 'package:test_app/services/session_manager.dart';
import 'package:test_app/views/components/widgets/socket_image.dart';
import 'package:test_app/views/features/photo/photo_slider_page.dart';
import 'package:test_app/views/features/photo/image_detail_page.dart';
import 'package:test_app/views/features/post/post_form_popup.dart';
import 'package:test_app/views/layout/screens/home/comment_screen.dart';

class PostItem extends StatefulWidget {
  final Post post;
  final PhotoRepository photoRepository;
  final AlbumRepository albumRepository;
  final PostRepository postRepository;
  final bool showActions;
  final VoidCallback? onRefresh;

  const PostItem({
    super.key,
    required this.post,
    required this.photoRepository,
    required this.albumRepository,
    required this.postRepository,
    this.showActions = false,
    this.onRefresh,
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
    if (oldWidget.post.id != widget.post.id || 
        oldWidget.post.lastModified != widget.post.lastModified ||
        oldWidget.post.photoIds.length != widget.post.photoIds.length ||
        oldWidget.post.albumIds.length != widget.post.albumIds.length) {
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
      // Fetch owner profile
      final owner = await UserRepository().getUserProfileById(widget.post.ownerId);

      // Fetch albums
      final albumFutures = widget.post.albumIds.map((id) => widget.albumRepository.getAlbumById(id));
      final albums = await Future.wait(albumFutures);

      // Fetch photos from albumIds
      List<Photo> albumPhotos = [];
      for (var album in albums) {
        final photos = await widget.photoRepository.getPhotosByAlbumId(album.id);
        albumPhotos.addAll(photos);
      }

      // Fetch specific photoIds
      final photoFutures = widget.post.photoIds.map((id) => widget.photoRepository.getPhotoById(id));
      List<Photo> specificPhotos = await Future.wait(photoFutures);

      // Combine and remove duplicates
      final combined = [...albumPhotos, ...specificPhotos];
      final seenIds = <String>{};
      final uniquePhotos = combined.where((p) => seenIds.add(p.id)).toList();

      if (mounted) {
        setState(() {
          _owner = owner;
          _albums = albums;
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

  void _openPhotoSlider(String photoId) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => PhotoSliderPage(
          items: _allPhotos,
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
    final result = await showDialog<bool>(
      context: context,
      builder: (context) => PostFormPopup(
        photoRepository: widget.photoRepository,
        albumRepository: widget.albumRepository,
        postRepository: widget.postRepository,
        initialPost: widget.post,
      ),
    );

    if (result == true && widget.onRefresh != null) {
      widget.onRefresh!();
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
        borderRadius: BorderRadius.circular(12),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
            child: Row(
              children: [
                CircleAvatar(
                  radius: 16,
                  backgroundColor: Colors.grey.shade300,
                  child: _owner?.profilePhotoName != null
                      ? ClipOval(
                    child: SizedBox.expand(
                      child: SocketImage(
                        photoName: _owner!.profilePhotoName!,
                        sessionId: SessionManager.instance.sessionId!,
                        builder: (context, provider) => Image(
                          image: provider,
                          fit: BoxFit.cover,
                        ),
                      ),
                    ),
                  )
                      : const Icon(Icons.person),
                ),
                const SizedBox(width: 10),
                Expanded(
                  child: Text(
                    _owner?.userName ?? widget.post.ownerId,
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
                  ),
                ),
                if (widget.showActions)
                  IconButton(
                    icon: const Icon(Icons.more_vert, size: 20),
                    onPressed: () {
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
                              fit: BoxFit.contain, // Fit to see full width and height
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
                    if (_allPhotos.isNotEmpty) {
                      final albumPhoto = _allPhotos.firstWhere(
                        (p) => album.photoIds.contains(p.id),
                        orElse: () => _allPhotos.first,
                      );
                      _openPhotoSlider(albumPhoto.id);
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

          // Date
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
            child: Text(
              widget.post.createdAt != null 
                ? widget.post.createdAt!.toString().split(' ')[0] 
                : "",
              style: const TextStyle(color: Colors.grey, fontSize: 11),
            ),
          ),
        ],
      ),
    );
  }
}
