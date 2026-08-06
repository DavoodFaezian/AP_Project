import 'dart:async';
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
import 'package:test_app/views/features/post/photo_selection_card.dart';

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

  // Search state
  bool _isSearching = false;
  final TextEditingController _searchController = TextEditingController();
  Timer? _debounceTimer;
  List<Photo> _searchResults = [];
  bool _isSearchLoading = false;

  @override
  void initState() {
    super.initState();
    _viewModel = PostFormViewModel(
      postRepository: widget.postRepository,
      initialPost: widget.initialPost,
    );
    _fetchAlbums();
    _searchController.addListener(_onSearchChanged);
  }

  @override
  void dispose() {
    _searchController.removeListener(_onSearchChanged);
    _searchController.dispose();
    _debounceTimer?.cancel();
    super.dispose();
  }

  void _onSearchChanged() {
    if (!_isSearching) return;
    
    if (_debounceTimer?.isActive ?? false) _debounceTimer!.cancel();
    _debounceTimer = Timer(const Duration(milliseconds: 500), () {
      _performSearch(_searchController.text);
    });
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

  Future<void> _performSearch(String query) async {
    setState(() => _isSearchLoading = true);
    try {
      final photos = await widget.photoRepository.searchPhotos(query);
      if (mounted) {
        setState(() {
          _searchResults = photos;
          _isSearchLoading = false;
        });
      }
    } catch (e) {
      debugPrint("Search error: $e");
      if (mounted) setState(() => _isSearchLoading = false);
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
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
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
                        "Photos in ${album.albumName}",
                        style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                      ),
                    ),
                    Expanded(
                      child: GridView.builder(
                        controller: scrollController,
                        padding: const EdgeInsets.all(12),
                        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                          crossAxisCount: 2,
                          crossAxisSpacing: 12,
                          mainAxisSpacing: 12,
                          childAspectRatio: 0.85,
                        ),
                        itemCount: photos.length,
                        itemBuilder: (context, index) {
                          final photo = photos[index];
                          final isSelected = _viewModel.isPhotoSelected(
                            album.id, 
                            photo.id,
                            photoAlbumIds: photo.albumIds.toList(),
                          );

                          bool isImplicit = _viewModel.isAlbumSelected(album.id);
                          Color selectionColor = isImplicit ? Colors.grey : _brandPurple;
                          
                          return PhotoSelectionCard(
                            photo: photo,
                            isSelected: isSelected,
                            selectionColor: selectionColor,
                            onTap: () {
                              _viewModel.togglePhotoSelection(album.id, photo.id);
                              setModalState(() {});
                              setState(() {}); 
                            },
                          );
                        },
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: SafeArea(child:SizedBox(
                        width: double.infinity,
                        child: FilledButton(
                          onPressed: () => Navigator.pop(context),
                          style: FilledButton.styleFrom(
                            backgroundColor: _brandPurple,
                            padding: const EdgeInsets.symmetric(vertical: 16),
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                          ),
                          child: const Text("Done", style: TextStyle(fontWeight: FontWeight.bold)),
                        ),
                      ),
                    ),
                    )
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
          appBar: _isSearching ? _buildSearchAppBar() : _buildNormalAppBar(),
          body: _isSearching ? _buildSearchBody() : _buildMainBody(),
        );
      },
    );
  }

  PreferredSizeWidget _buildNormalAppBar() {
    return CustomAppBar(
      title: _viewModel.isEdit ? "Edit Post" : "Create Post",
      actions: [
        IconButton(
          icon: const Icon(Icons.search),
          onPressed: () {
            setState(() => _isSearching = true);
            _performSearch(""); 
          },
        ),
        if (!_isLoading)
          IconButton(
            onPressed: _viewModel.isSubmitting ? null : _submit,
            icon: _viewModel.isSubmitting 
              ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
              : const Icon(Icons.send, color: Colors.white),
          ),
      ],
      leading: IconButton(
        icon: const Icon(Icons.arrow_back),
        onPressed: () => Navigator.pop(context),
      ),
    );
  }

  PreferredSizeWidget _buildSearchAppBar() {
    return CustomAppBar(
      title: "",
      leading: IconButton(
        icon: const Icon(Icons.close),
        onPressed: () {
          setState(() {
            _isSearching = false;
            _searchResults = [];
            _searchController.clear();
          });
        },
      ),
      actions: [
        SizedBox(
          width: MediaQuery.of(context).size.width - 80,
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 8),
            child: TextField(
              controller: _searchController,
              autofocus: true,
              cursorWidth: 3,
              cursorColor: Colors.white,
              style: const TextStyle(color: Colors.white),
              decoration: const InputDecoration(
                hintText: "Search photos...",
                hintStyle: TextStyle(color: Colors.white70),
                border: InputBorder.none,
                enabledBorder: InputBorder.none,
                focusedBorder: InputBorder.none,
                fillColor: Colors.transparent,
              ),
              onSubmitted: _performSearch,
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildMainBody() {
    if (_isLoading) return const Center(child: CircularProgressIndicator());
    
    return ListView(
      padding: const EdgeInsets.all(20),
      children: [
        const Text(
          "Selection",
          style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Color(0xFF1E1B4B)),
        ),
        const SizedBox(height: 4),
        const Text(
          "Hold to select entire album, Tap to select images",
          style: TextStyle(color: Colors.grey, fontSize: 13),
        ),
        const SizedBox(height: 20),
        ListView.builder(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          itemCount: _albums.length,
          itemBuilder: (context, index) {
            final album = _albums[index];
            final isSelected = _viewModel.isAlbumSelected(album.id);
            final photoCount = _viewModel.getSelectedPhotoCountForAlbum(album);
            
            return Card(
              margin: const EdgeInsets.only(bottom: 12),
              elevation: isSelected ? 4 : 0,
              shadowColor: _brandPurple.withOpacity(0.2),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
                side: BorderSide(
                  color: isSelected ? _brandPurple : Colors.grey.shade200,
                  width: isSelected ? 2 : 1,
                ),
              ),
              child: ListTile(
                contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                leading: Container(
                  width: 48,
                  height: 48,
                  decoration: BoxDecoration(
                    color: isSelected ? _brandPurple.withOpacity(0.1) : Colors.grey.shade100,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Icon(
                    Icons.photo_album_rounded, 
                    color: isSelected ? _brandPurple : Colors.grey.shade600,
                  ),
                ),
                title: Text(
                  album.albumName,
                  style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                ),
                subtitle: Text(
                  isSelected 
                    ? "Entire album selected" 
                    : (photoCount > 0 ? "$photoCount photos selected" : "${album.photoIds.length} photos"),
                  style: const TextStyle(
                    color: Colors.black,
                    fontSize: 13,
                  ),
                ),
                trailing: isSelected 
                  ? Icon(Icons.check_circle, color: _brandPurple)
                  : Icon(Icons.chevron_right, color: Colors.grey.shade400),
                onTap: () => _showAlbumPhotos(album),
                onLongPress: () {
                  _viewModel.toggleAlbumSelection(album.id);
                },
              ),
            );
          },
        ),
        const SizedBox(height: 24),
        const Text(
          "Settings",
          style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Color(0xFF1E1B4B)),
        ),
        const SizedBox(height: 12),
        Container(
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: Colors.grey.shade200),
          ),
          child: SwitchListTile(
            activeColor: _brandPurple,
            title: const Text("Allow Comments", style: TextStyle(fontWeight: FontWeight.w600)),
            subtitle: const Text("People can leave comments on this post", style: TextStyle(fontSize: 12)),
            value: _viewModel.commentsAllowed,
            onChanged: (val) {
              _viewModel.setCommentsAllowed(val);
            },
          ),
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
    );
  }

  Widget _buildSearchBody() {
    if (_isSearchLoading) return const Center(child: CircularProgressIndicator());

    return Column(
      children: [
        Expanded(
          child: _searchResults.isEmpty 
            ? Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.search, size: 80, color: Colors.grey.shade300),
                    const SizedBox(height: 16),
                    Text("Search globally across all photos", style: TextStyle(color: Colors.grey.shade500)),
                  ],
                ),
              )
            : GridView.builder(
                padding: const EdgeInsets.all(16),
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2,
                  crossAxisSpacing: 12,
                  mainAxisSpacing: 12,
                  childAspectRatio: 0.85,
                ),
                itemCount: _searchResults.length,
                itemBuilder: (context, index) {
                  final photo = _searchResults[index];
                  final isSelected = _viewModel.isPhotoSelected(
                    null, 
                    photo.id, 
                    photoAlbumIds: photo.albumIds.toList(),
                  );

                  bool isImplicit = false;
                  for (var albumId in photo.albumIds) {
                    if (_viewModel.isAlbumSelected(albumId)) {
                      isImplicit = true;
                      break;
                    }
                  }
                  Color selectionColor = isImplicit ? Colors.grey : _brandPurple;
                  
                  return PhotoSelectionCard(
                    photo: photo,
                    isSelected: isSelected,
                    selectionColor: selectionColor,
                    onTap: () {
                      _viewModel.toggleGlobalPhotoSelection(
                        photo.id, 
                        albumIds: photo.albumIds.toList(),
                      );
                      setState(() {});
                    },
                  );
                },
              ),
        ),
        Container(
          padding: const EdgeInsets.all(20),
          decoration: BoxDecoration(
            color: Colors.white,
            boxShadow: [BoxShadow(color: Colors.black.withOpacity(0.05), blurRadius: 10, offset: const Offset(0, -5))],
          ),
          child: SizedBox(
            width: double.infinity,
            height: 54,
            child: FilledButton(
              onPressed: () => setState(() => _isSearching = false),
              style: FilledButton.styleFrom(
                backgroundColor: _brandPurple,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
              child: const Text("Done", style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            ),
          ),
        ),
      ],
    );
  }
}
