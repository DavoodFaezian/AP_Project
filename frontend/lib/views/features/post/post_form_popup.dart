import 'package:flutter/material.dart';
import 'package:test_app/models/album.dart';
import 'package:test_app/models/photo.dart';
import 'package:test_app/repositories/album_repository.dart';
import 'package:test_app/repositories/photo_repository.dart';
import 'package:test_app/repositories/post_repository.dart';
import 'package:test_app/services/session_manager.dart';
import 'package:test_app/viewmodels/post_form_view_model.dart';
import 'package:test_app/views/components/widgets/socket_image.dart';

import '../../../models/post.dart';

class PostFormPopup extends StatefulWidget {
  final PhotoRepository photoRepository;
  final AlbumRepository albumRepository;
  final PostRepository postRepository;
  final Post? initialPost;

  const PostFormPopup({
    super.key,
    required this.photoRepository,
    required this.albumRepository,
    required this.postRepository,
    this.initialPost,
  });

  @override
  State<PostFormPopup> createState() => _PostFormPopupState();
}

class _PostFormPopupState extends State<PostFormPopup> {
  late final PostFormViewModel _viewModel;
  List<Album> _albums = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _viewModel = PostFormViewModel(
      postRepository: widget.postRepository,
      initialPost: widget.initialPost,
    );
    _fetchAlbums();
  }

  Future<void> _fetchAlbums() async {
    try {
      _albums = await widget.albumRepository.getAllAlbums();
      _viewModel.initializeSelections(_albums);
    } catch (e) {
      debugPrint("Error fetching albums: $e");
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  void _showAlbumPhotos(Album album) async {
    final photos = await widget.photoRepository.getPhotosByAlbumId(album.id);
    _viewModel.onAlbumPhotosLoaded(album.id, photos.map((p) => p.id).toList());

    if (!mounted) return;

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (ctx) {
        return StatefulBuilder(
          builder: (context, setModalState) {
            return DraggableScrollableSheet(
              initialChildSize: 0.7,
              maxChildSize: 0.9,
              expand: false,
              builder: (_, scrollController) {
                return Column(
                  children: [
                    Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text("Select Photos in ${album.albumName}",
                        style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                    ),
                    Expanded(
                      child: GridView.builder(
                        controller: scrollController,
                        padding: const EdgeInsets.all(8),
                        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                          crossAxisCount: 3,
                          crossAxisSpacing: 8,
                          mainAxisSpacing: 8,
                        ),
                        itemCount: photos.length,
                        itemBuilder: (context, index) {
                          final photo = photos[index];
                          final isSelected = _viewModel.isPhotoSelected(album.id, photo.id);
                          return GestureDetector(
                            onTap: () {
                              _viewModel.togglePhotoSelection(album.id, photo.id);
                              setModalState(() {});
                            },
                            child: Stack(
                              fit: StackFit.expand,
                              children: [
                                ClipRRect(
                                  borderRadius: BorderRadius.circular(8),
                                  child: SocketImage(
                                    photoName: photo.photoName,
                                    sessionId: SessionManager.instance.sessionId!,
                                    ownerId: photo.ownerId,
                                    loadingPlaceholder: const Center(child: CircularProgressIndicator()),
                                    errorPlaceholder: Container(color: Colors.grey),
                                    builder: (context, provider) => Image(image: provider, fit: BoxFit.cover),
                                  ),
                                ),
                                if (isSelected)
                                  Container(
                                    color: Colors.blue.withOpacity(0.3),
                                    child: const Icon(Icons.check_circle, color: Colors.white),
                                  ),
                              ],
                            ),
                          );
                        },
                      ),
                    ),
                  ],
                );
              },
            );
          },
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _viewModel,
      builder: (context, _) {
        return AlertDialog(
          title: Text(_viewModel.isEdit ? "Edit Post" : "Create Post"),
          content: SizedBox(
            width: double.maxFinite,
            child: _isLoading 
              ? const Center(child: CircularProgressIndicator())
              : Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Text("Hold to select entire album, Tap to select specific images"),
                    const SizedBox(height: 12),
                    Flexible(
                      child: ListView.builder(
                        shrinkWrap: true,
                        itemCount: _albums.length,
                        itemBuilder: (context, index) {
                          final album = _albums[index];
                          final isSelected = _viewModel.isAlbumSelected(album.id);
                          final photoCount = _viewModel.getSelectedPhotoCountForAlbum(album.id);
                          
                          return ListTile(
                            leading: const Icon(Icons.album),
                            title: Text(album.albumName),
                            subtitle: photoCount > 0 && !isSelected 
                              ? Text("$photoCount photos selected") 
                              : null,
                            trailing: isSelected ? const Icon(Icons.check_circle, color: Colors.blue) : null,
                            onTap: () => _showAlbumPhotos(album),
                            onLongPress: () {
                              _viewModel.toggleAlbumSelection(album.id);
                            },
                          );
                        },
                      ),
                    ),
                    SwitchListTile(
                      title: const Text("Allow Comments"),
                      value: _viewModel.commentsAllowed,
                      onChanged: (val) {
                        _viewModel.setCommentsAllowed(val);
                      },
                    ),
                    if (_viewModel.errorMessage != null)
                      Text(_viewModel.errorMessage!, style: const TextStyle(color: Colors.red)),
                  ],
                ),
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(context), child: const Text("Cancel")),
            ElevatedButton(
              onPressed: _viewModel.isSubmitting ? null : () async {
                if (await _viewModel.submit()) {
                  if (mounted) Navigator.pop(context, true);
                }
              },
              child: _viewModel.isSubmitting ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(strokeWidth: 2)) : const Text("Post"),
            ),
          ],
        );
      },
    );
  }
}
