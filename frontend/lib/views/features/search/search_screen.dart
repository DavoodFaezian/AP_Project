import 'package:flutter/material.dart';
import 'package:test_app/services/session_manager.dart';
import 'package:test_app/views/components/widgets/socket_image.dart';
import '../../../models/photo.dart';
import '../../../repositories/photo_repository.dart';
import '../../components/widgets/custom_appbar.dart';
import '../../components/widgets/custom_drawer.dart';
import '../../components/widgets/empty_screen.dart';
import '../photo/photo_slider_page.dart';
import '../photo/image_detail_page.dart';

class SearchScreen extends StatefulWidget {
  const SearchScreen({super.key});

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {
  static const String _currentUserId = 'user1';
  bool hasSearched = false;
  final TextEditingController searchController = TextEditingController();
  final List<Photo> searchResult = [];
  final PhotoRepository _photoRepository = PhotoRepository();

  Future<void> _performSearch(String query) async {
    if (query.isEmpty) {
      setState(() {
        hasSearched = false;
        searchResult.clear();
      });
      return;
    }

    final allPhotos = await _photoRepository.getPhotosByOwnerId();
    final filtered = allPhotos.where((photo) {
      final q = query.toLowerCase();
      return photo.title.toLowerCase().contains(q) ||
          photo.photoName.toLowerCase().contains(q) ||
          photo.caption.toLowerCase().contains(q) ||
          photo.tags.any((tag) => tag.toLowerCase().contains(q));
    }).toList();

    setState(() {
      hasSearched = true;
      searchResult.clear();
      searchResult.addAll(filtered);
    });
  }

  void _openPhotoSlider(String photoId) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => PhotoSliderPage(
          items: searchResult,
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
            final photo = searchResult.firstWhere((p) => p.id == photoId);
            Navigator.of(context).push(
              MaterialPageRoute(
                builder: (_) => ImageDetailPage(
                  photoId: photoId,
                  ownerId: photo.ownerId,
                  photoRepository: _photoRepository,
                ),
              ),
            );
          },
        ),
      ),
    );
  }

  Widget _buildResultGrid() {
    return GridView.builder(
      padding: const EdgeInsets.all(8),
      itemCount: searchResult.length,
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 2,
        crossAxisSpacing: 10,
        mainAxisSpacing: 10,
        childAspectRatio: 0.9,
      ),
      itemBuilder: (context, index) {
        final photo = searchResult[index];
        return GestureDetector(
          onTap: () => _openPhotoSlider(photo.id),
          child: Card(
            elevation: 3,
            clipBehavior: Clip.antiAlias,
            child: Column(
              children: [
                Expanded(
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
                      width: double.infinity,
                      height: double.infinity,
                    ),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Text(
                    photo.title.isNotEmpty ? photo.title : photo.photoName,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(

      drawer: CustomDrawer(),

      appBar: const CustomAppBar(
        title: "Search",
      ),

      body: Column(

        children: [

          //------------------------------------
          // Search Box
          //------------------------------------

          Padding(
            padding: const EdgeInsets.all(12),

            child: TextField(

              controller: searchController,

              decoration: InputDecoration(

                hintText: "Search images...",

                prefixIcon: const Icon(Icons.search),

                suffixIcon: IconButton(
                  onPressed: () {
                    searchController.clear();
                    _performSearch('');
                  },
                  icon: const Icon(Icons.clear),
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(30),
                ),
              ),
              onChanged: _performSearch,
            ),
          ),

          //------------------------------------
          // Search Result
          //------------------------------------

          Expanded(
            child: !hasSearched

                ? const EmptyState(
                    imagePath: "assets/images/File searching-rafiki (1).png",
                    title: "Search photos",
                    subtitle: "Search by file name, date or description.",
                  )

                : searchResult.isEmpty

                    ? const EmptyState(
                        imagePath: "assets/images/File searching-rafiki (1).png",
                        title: "No photos found",
                        subtitle: "Try another keyword.",
                      )

                    : _buildResultGrid(),
          ),
        ],
      ),
    );
  }
}