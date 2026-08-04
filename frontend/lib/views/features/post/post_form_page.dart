import 'package:flutter/material.dart';
import 'package:test_app/models/album.dart';
import 'package:test_app/models/photo.dart';
import 'package:test_app/models/post.dart';
import 'package:test_app/repositories/album_repository.dart';
import 'package:test_app/repositories/photo_repository.dart';
import 'package:test_app/repositories/post_repository.dart';
import 'package:test_app/services/session_manager.dart';
import 'package:test_app/viewmodels/post_form_view_model.dart';
import 'package:test_app/views/components/widgets/custom_appbar.dart';
import 'package:test_app/views/components/widgets/socket_image.dart';

class PostFormPage extends StatefulWidget {
  final PhotoRepository photoRepository;
  final AlbumRepository albumRepository;
  final PostRepository postRepository;
  final Post? initialPost;

  const PostFormPage({
    super.key,
    required this.photoRepository,
    required this.albumRepository,
    required this.postRepository,
    this.initialPost,
  });

  @override
  State<PostFormPage> createState() => _PostFormPageState();
}

class _PostFormPageState extends State<PostFormPage> {
  late final PostFormViewModel _viewModel;
  List<Album> _albums = [];
  bool _isLoading = true;
  final Color _brandPurple = const Color(0xFF5B21B6);

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
    final photos = await widget.photoRepository.getPhotosByAlbumId(album.id, SessionManager.instance.userId);
    _viewModel.onAlbumPhotosLoaded(album.id, photos.map((p) => p.id).toList());

    if (!mounted) return;

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
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
                    Container(
                      width: 40,
                      height: 4,
                      margin: const EdgeInsets.symmetric(vertical: 12),
                      decoration: BoxDecoration(
                        color: Colors.grey.shade300,
                        borderRadius: BorderRadius.circular(2),
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text(
                        "Select Photos in ${album.albumName}",
                        style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                      ),
                    ),
                    Expanded(
                      child: GridView.builder(
                        controller: scrollController,
                        padding: const EdgeInsets.all(12),
                        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                          crossAxisCount: 3,
                          crossAxisSpacing: 10,
                          mainAxisSpacing: 10,
                        ),
                        itemCount: photos.length,
                        itemBuilder: (context, index) {
                          final photo = photos[index];
                          final isSelected = _viewModel.isPhotoSelected(album.id, photo.id);
                          
                          return GestureDetector(
                            onTap: () {
                              _viewModel.togglePhotoSelection(album.id, photo.id);
                              setModalState(() {});
                              setState(() {}); // Update main page
                            },
                            child: AnimatedContainer(
                              duration: const Duration(milliseconds: 150),
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(12),
                                border: isSelected 
                                  ? Border.all(color: _brandPurple, width: 2.5) 
                                  : Border.all(color: Colors.grey.shade200, width: 1),
                              ),
                              child: Stack(
                                fit: StackFit.expand,
                                children: [
                                  ClipRRect(
                                    borderRadius: BorderRadius.circular(10),
                                    child: SocketImage(
                                      photoName: photo.photoName,
                                      sessionId: SessionManager.instance.sessionId!,
                                      ownerId: photo.ownerId,
                                      loadingPlaceholder: const Center(child: CircularProgressIndicator(strokeWidth: 2)),
                                      errorPlaceholder: Container(color: Colors.grey.shade100),
                                      builder: (context, provider) => Image(image: provider, fit: BoxFit.cover),
                                    ),
                                  ),
                                  // Checkbox on the left
                                  Positioned(
                                    top: 6,
                                    left: 6,
                                    child: Container(
                                      width: 22,
                                      height: 22,
                                      decoration: BoxDecoration(
                                        color: isSelected ? _brandPurple : Colors.white.withOpacity(0.8),
                                        shape: BoxShape.circle,
                                        border: Border.all(
                                          color: isSelected ? _brandPurple : Colors.grey.shade400,
                                          width: 1.5,
                                        ),
                                      ),
                                      child: isSelected 
                                        ? const Icon(Icons.check, size: 14, color: Colors.white) 
                                        : null,
                                    ),
                                  ),
                                ],
                              ),
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

  Future<void> _submit() async {
    if (await _viewModel.submit()) {
      if (mounted) Navigator.pop(context, true);
    }
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _viewModel,
      builder: (context, _) {
        return Scaffold(
          backgroundColor: const Color(0xFFF9F9F9),
          appBar: CustomAppBar(
            title: _viewModel.isEdit ? "Edit Post" : "Create Post",
            actions: [
              if (!_isLoading)
                TextButton(
                  onPressed: _viewModel.isSubmitting ? null : _submit,
                  child: _viewModel.isSubmitting 
                    ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
                    : const Text("POST", style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                ),
            ],
            leading: IconButton(
              icon: const Icon(Icons.arrow_back),
              onPressed: () => Navigator.pop(context),
            ),
          ),
          body: _isLoading 
            ? const Center(child: CircularProgressIndicator())
            : ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  const Text(
                    "Hold to select entire album, Tap to select specific images",
                    style: TextStyle(color: Colors.grey, fontSize: 14),
                  ),
                  const SizedBox(height: 16),
                  ListView.builder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: _albums.length,
                    itemBuilder: (context, index) {
                      final album = _albums[index];
                      final isSelected = _viewModel.isAlbumSelected(album.id);
                      final photoCount = _viewModel.getSelectedPhotoCountForAlbum(album.id);
                      
                      return Card(
                        margin: const EdgeInsets.only(bottom: 12),
                        elevation: isSelected ? 2 : 0,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(16),
                          side: BorderSide(
                            color: isSelected ? _brandPurple : Colors.transparent,
                            width: 2,
                          ),
                        ),
                        child: ListTile(
                          contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                          leading: CircleAvatar(
                            backgroundColor: isSelected ? _brandPurple.withOpacity(0.1) : Colors.grey.shade100,
                            child: Icon(
                              Icons.photo_album, 
                              color: isSelected ? _brandPurple : Colors.grey,
                            ),
                          ),
                          title: Text(
                            album.albumName,
                            style: const TextStyle(fontWeight: FontWeight.bold),
                          ),
                          subtitle: Text(
                            isSelected 
                              ? "Entire album selected" 
                              : (photoCount > 0 ? "$photoCount photos selected" : "${album.photoIds.length} photos"),
                            style: const TextStyle(
                              color: Colors.black, // Selected count text color changed to Black
                              fontSize: 13,
                            ),
                          ),
                          trailing: isSelected 
                            ? Icon(Icons.check_circle, color: _brandPurple) // Purple checkbox
                            : const Icon(Icons.chevron_right),
                          onTap: () => _showAlbumPhotos(album),
                          onLongPress: () {
                            _viewModel.toggleAlbumSelection(album.id);
                          },
                        ),
                      );
                    },
                  ),
                  const SizedBox(height: 24),
                  const Divider(),
                  SwitchListTile(
                    title: const Text("Allow Comments"),
                    subtitle: const Text("People can leave comments on this post"),
                    value: _viewModel.commentsAllowed,
                    onChanged: (val) {
                      _viewModel.setCommentsAllowed(val);
                    },
                  ),
                  if (_viewModel.errorMessage != null)
                    Padding(
                      padding: const EdgeInsets.only(top: 16),
                      child: Text(
                        _viewModel.errorMessage!, 
                        style: const TextStyle(color: Colors.red, fontWeight: FontWeight.bold),
                        textAlign: TextAlign.center,
                      ),
                    ),
                  const SizedBox(height: 80),
                ],
              ),
        );
      },
    );
  }
}
