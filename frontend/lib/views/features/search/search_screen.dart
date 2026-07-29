import 'package:flutter/material.dart';
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
  final PhotoRepository _photoRepository = InMemoryPhotoRepository();

  Future<void> _performSearch(String query) async {
    if (query.isEmpty) {
      setState(() {
        hasSearched = false;
        searchResult.clear();
      });
      return;
    }

    final allPhotos = await _photoRepository.getPhotosByOwner(_currentUserId);
    final filtered = allPhotos.where((photo) {
      final q = query.toLowerCase();
      return photo.photoName.toLowerCase().contains(q) ||
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
          titleBuilder: (photo) => photo.photoName,
          imageProviderBuilder: (photo) => const AssetImage('assets/images/Image post-cuate.png'),
          onEyePressed: () {
            Navigator.of(context).push(
              MaterialPageRoute(
                builder: (_) => ImageDetailPage(
                  photoId: photoId,
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
            child: Column(
              children: [
                Expanded(
                  child: Container(
                    color: Colors.grey.shade300,
                    child: const Center(
                      child: Icon(Icons.image, size: 60),
                    ),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Text(photo.photoName, overflow: TextOverflow.ellipsis),
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