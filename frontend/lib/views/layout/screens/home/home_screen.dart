import 'package:flutter/material.dart';
import '../../../../repositories/photo_repository.dart';
import '../../../../viewmodels/photo_list_view_model.dart';
import '../../../../repositories/album_repository.dart';
import '../../../features/photo/photo_form_page.dart';
import '../../../features/photo/photo_slider_page.dart';
import '../../../features/photo/image_detail_page.dart';
import '../../../components/widgets/custom_appbar.dart';
import '../../../components/widgets/custom_drawer.dart';
import '../../../components/widgets/custom_fab.dart';
import '../../../components/widgets/empty_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  static const String _currentUserId = 'user1';
  final PhotoRepository _photoRepository = InMemoryPhotoRepository();
  final AlbumRepository _albumRepository = InMemoryAlbumRepository();
  
  late final PhotoListViewModel viewModel;

  @override
  void initState() {
    super.initState();
    viewModel = PhotoListViewModel(
      repository: _photoRepository,
      currentUserId: _currentUserId,
    );
    viewModel.loadPhotos();
  }

  @override
  void dispose() {
    viewModel.dispose();
    super.dispose();
  }

  Future<void> _openCreatePage() async {
    await Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => PhotoFormPage(
          currentUserId: _currentUserId,
          photoRepository: _photoRepository,
          albumRepository: _albumRepository,
          returnToAlbumTitle: 'Home',
        ),
      ),
    );
    await viewModel.loadPhotos();
  }

  Future<void> _openPhotoSlider(String photoId) async {
    await Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => PhotoSliderPage(
          items: viewModel.photos,
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

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: viewModel,
      builder: (context, _) {
        return Scaffold(
          drawer: const CustomDrawer(),
          appBar: viewModel.selectionMode
              ? CustomAppBar(
                  title: "${viewModel.selectedPhotoIds.length} Selected",
                  leading: IconButton(
                    icon: const Icon(Icons.close),
                    onPressed: viewModel.clearSelection,
                  ),
                  actions: [
                    IconButton(
                      icon: const Icon(Icons.delete),
                      onPressed: viewModel.deleteSelectedPhotos,
                    ),
                  ],
                )
              : CustomAppBar(
                  title: "Home",
                  actions: [
                    IconButton(
                      onPressed: () {},
                      icon: const Icon(Icons.filter_list),
                    ),
                    IconButton(
                      onPressed: () {},
                      icon: const Icon(Icons.sort),
                    ),
                  ],
                ),
          body: Padding(
            padding: const EdgeInsets.all(8),
            child: Builder(
              builder: (context) {
                if (viewModel.isLoading) {
                  return const Center(child: CircularProgressIndicator());
                }
                if (viewModel.photos.isEmpty) {
                  return const EmptyState(
                    imagePath: 'assets/images/Image post-cuate.png',
                    title: "No photos yet",
                    subtitle: "Upload your first photo",
                  );
                }
                return GridView.builder(
                  itemCount: viewModel.photos.length,
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    crossAxisSpacing: 8,
                    mainAxisSpacing: 8,
                  ),
                  itemBuilder: (context, index) {
                    final photo = viewModel.photos[index];
                    final isSelected = viewModel.selectedPhotoIds.contains(photo.id);
                    return GestureDetector(
                      onLongPress: () => viewModel.enterSelection(photo.id),
                      onTap: () {
                        if (viewModel.selectionMode) {
                          viewModel.toggleSelection(photo.id);
                        } else {
                          _openPhotoSlider(photo.id);
                        }
                      },
                      child: Container(
                        decoration: BoxDecoration(
                          color: isSelected ? Colors.blue.withOpacity(0.3) : Colors.grey.shade200,
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Center(
                          child: Text(photo.photoName),
                        ),
                      ),
                    );
                  },
                );
              },
            ),
          ),
          floatingActionButton: viewModel.selectionMode
              ? null
              : CustomFAB(
                  onPressed: _openCreatePage,
                ),
        );
      },
    );
  }
}
