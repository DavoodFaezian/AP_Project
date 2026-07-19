import 'package:flutter/material.dart';
import 'package:test_app/views/features/photo/photo_slider_page.dart';

import '../../../repositories/album_repository.dart';
import '../../../repositories/photo_repository.dart';
import '../../../viewmodels/photo_list_view_model.dart';
import '../../components/widgets/custom_appbar.dart';
import '../../components/widgets/custom_fab.dart';
import '../../components/widgets/empty_screen.dart';
import '../album/album_selection_field.dart';
import 'image_detail_page.dart';
import 'photo_form_page.dart';
import '../share/share_page.dart';

class PhotoListPage extends StatefulWidget {
  const PhotoListPage({
    super.key,
    required this.currentUserId,
    required this.photoRepository,
    required this.albumRepository,
    this.albumId,
    this.albumName,
    this.albumOwnerId,
  });

  final String currentUserId;
  final PhotoRepository photoRepository;
  final AlbumRepository albumRepository;
  final String? albumId;
  final String? albumName;
  final String? albumOwnerId;

  @override
  State<PhotoListPage> createState() => _PhotoListPageState();
}

class _PhotoListPageState extends State<PhotoListPage> {
  late final PhotoListViewModel viewModel;

  @override
  void initState() {
    super.initState();
    viewModel = PhotoListViewModel(
      repository: widget.photoRepository,
      currentUserId: widget.currentUserId,
      albumId: widget.albumId,
      albumOwnerId: widget.albumOwnerId,
    );
    viewModel.loadPhotos();
  }

  @override
  void dispose() {
    viewModel.dispose();
    super.dispose();
  }

  int _crossAxisCount(double width) {
    if (width >= 1200) return 5;
    if (width >= 1000) return 4;
    if (width >= 700) return 3;
    if (width >= 450) return 2;
    return 1;
  }

  Future<void> _openCreatePage() async {
    await Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => PhotoFormPage(
          currentUserId: widget.currentUserId,
          photoRepository: widget.photoRepository,
          albumRepository: widget.albumRepository,
          sourceAlbumId: widget.albumId,
          returnToAlbumTitle: widget.albumName ?? 'Photos',
        ),
      ),
    );
    await viewModel.loadPhotos();
  }

  Future<void> _openEditPage() async {
    final photo = viewModel.selectedPhoto;
    if (photo == null) {
      return;
    }

    await Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => PhotoFormPage(
          currentUserId: widget.currentUserId,
          photoRepository: widget.photoRepository,
          albumRepository: widget.albumRepository,
          sourceAlbumId: widget.albumId,
          initialPhoto: photo,
          returnToAlbumTitle: widget.albumName ?? 'Photos',
        ),
      ),
    );

    viewModel.clearSelection();
    await viewModel.loadPhotos();
  }

  Future<void> _openTransferPage() async {
    final photo = viewModel.selectedPhoto;
    if (photo == null) {
      return;
    }

    final initialAlbumIds = photo.albumIds;
    var selectedAlbumIds = {...initialAlbumIds};

    final transferred = await showDialog<bool>(
      context: context,
      builder: (dialogContext) {
        return AlertDialog(
          title: const Text('Transfer Photo'),
          content: SizedBox(
            width: double.maxFinite,
            child: AlbumMultiSelectField(
              albumRepository: widget.albumRepository,
              ownerId: widget.currentUserId,
              initialValue: selectedAlbumIds,
              title: 'Albums',
              hintText: 'Select destination albums',
              onChanged: (value) {
                selectedAlbumIds = value;
              },
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(dialogContext).pop(false),
              child: const Text('Cancel'),
            ),
            FilledButton(
              onPressed: () async {
                await widget.photoRepository.assignPhotoToAlbums(
                  photoId: photo.id,
                  albumIds: selectedAlbumIds,
                );

                if (!dialogContext.mounted) {
                  return;
                }

                Navigator.of(dialogContext).pop(true);
              },
              child: const Text('Transfer'),
            ),
          ],
        );
      },
    );

    if (transferred == true) {
      viewModel.clearSelection();
      await viewModel.loadPhotos();
    }
  }
  Future<void> _openPhotoSlider(String photoId) async {
    await Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => PhotoSliderPage(
          items: viewModel.photos,
          initialItemId: photoId,
          idBuilder: (photo) => photo.id,
          titleBuilder: (photo) => photo.photoName,
          imageProviderBuilder: (photo) {
            // Replace this with your real image source.
            // Examples:
            // return NetworkImage(photo.imageUrl);
            // return FileImage(File(photo.filePath));
            // return MemoryImage(photo.bytes);
            return const AssetImage('assets/images/Image post-cuate.png');
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


  Future<void> _openSharePage() async {
    final photo = viewModel.selectedPhoto;
    if (photo == null) {
      return;
    }

    await Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => SharePage(
          photoId: photo.id,
          albumTitle: widget.albumName ?? 'Photos',
        ),
      ),
    );
  }

  PreferredSizeWidget _buildAppBar() {
    if (viewModel.selectionMode) {
      return CustomAppBar(
        title: '${viewModel.selectedPhotoIds.length} Selected',
        leading: IconButton(
          icon: const Icon(Icons.close),
          onPressed: viewModel.clearSelection,
        ),
        actions: [
          if (viewModel.isOwnerContext && viewModel.isSingleSelection)
            IconButton(
              icon: const Icon(Icons.edit),
              onPressed: _openEditPage,
            ),
          if (viewModel.isOwnerContext && viewModel.isSingleSelection)
            IconButton(
              icon: const Icon(Icons.drive_file_move_outline),
              onPressed: _openTransferPage,
            ),
          if (viewModel.isSingleSelection)
            IconButton(
              icon: const Icon(Icons.share),
              onPressed: _openSharePage,
            ),
          if (viewModel.isOwnerContext)
            IconButton(
              icon: const Icon(Icons.delete),
              onPressed: viewModel.deleteSelectedPhotos,
            ),
        ],
      );
    }

    return CustomAppBar(
      title: widget.albumName ?? 'Photos',
      leading: IconButton(
        icon: const Icon(Icons.arrow_back),
        onPressed: () => Navigator.of(context).pop(),
      ),
      actions: [
        IconButton(
          onPressed: () {
            // Sort action placeholder
          },
          icon: const Icon(Icons.sort),
        ),
        IconButton(
          onPressed: () {
            // Filter action placeholder
          },
          icon: const Icon(Icons.filter_list),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: viewModel,
      builder: (context, _) {
        return Scaffold(
          backgroundColor: const Color(0xFFFCF9FC),
          appBar: _buildAppBar(),
          floatingActionButton: viewModel.selectionMode
              ? null
              : CustomFAB(
            onPressed: _openCreatePage,
          ),
          body: Builder(
            builder: (context) {
              if (viewModel.isLoading) {
                return const Center(
                  child: CircularProgressIndicator(),
                );
              }

              if (viewModel.photos.isEmpty) {
                return const Padding(
                  padding: EdgeInsets.all(8),
                  child: EmptyState(
                    imagePath: 'assets/images/Image post-cuate.png',
                    title: 'No photos yet',
                    subtitle: 'Add your first photo.',
                  ),
                );
              }

              return LayoutBuilder(
                builder: (context, constraints) {
                  return GridView.builder(
                    padding: const EdgeInsets.all(12),
                    itemCount: viewModel.photos.length,
                    gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: _crossAxisCount(constraints.maxWidth),
                      crossAxisSpacing: 12,
                      mainAxisSpacing: 12,
                      childAspectRatio: 0.82,
                    ),
                    itemBuilder: (context, index) {
                      final photo = viewModel.photos[index];
                      final isSelected = viewModel.selectedPhotoIds.contains(photo.id);
                      final selectedColor = Theme.of(context).colorScheme.primary;

                      return GestureDetector(
                        onLongPress: () => viewModel.enterSelection(photo.id),
                        onTap: () {
                          if (viewModel.selectionMode) {
                            viewModel.toggleSelection(photo.id);
                            return;
                          }
                          _openPhotoSlider(photo.id);
                        },
                        child: AnimatedContainer(
                          duration: const Duration(milliseconds: 180),
                          curve: Curves.easeOut,
                          padding: EdgeInsets.all(isSelected ? 4 : 0),
                          decoration: BoxDecoration(
                            color: isSelected ? selectedColor.withOpacity(0.12) : Colors.transparent,
                            borderRadius: BorderRadius.circular(16),
                            border: isSelected ? Border.all(color: selectedColor, width: 2) : null,
                          ),
                          child: Stack(
                            children: [
                              DecoratedBox(
                                decoration: BoxDecoration(
                                  color: Colors.white,
                                  borderRadius: BorderRadius.circular(12),
                                  border: Border.all(
                                    color: Colors.grey.shade300,
                                    width: 1,
                                  ),
                                ),
                                child: Column(
                                  children: [
                                    Expanded(
                                      child: ClipRRect(
                                        borderRadius: const BorderRadius.vertical(
                                          top: Radius.circular(12),
                                        ),
                                        child: Container(
                                          color: Colors.grey.shade200,
                                          alignment: Alignment.center,
                                          child: Text(
                                            photo.id,
                                            textAlign: TextAlign.center,
                                          ),
                                        ),
                                      ),
                                    ),
                                    Padding(
                                      padding: const EdgeInsets.all(8),
                                      child: Text(
                                        photo.photoName,
                                        maxLines: 1,
                                        overflow: TextOverflow.ellipsis,
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                              if (viewModel.hasSelection)
                                Positioned(
                                  top: 10,
                                  left: 10,
                                  child: AnimatedScale(
                                    duration: const Duration(milliseconds: 180),
                                    scale: 1,
                                    child: IgnorePointer(
                                      child: Container(
                                        width: 24,
                                        height: 24,
                                        decoration: BoxDecoration(
                                          color: isSelected ? selectedColor : Colors.white,
                                          shape: BoxShape.circle,
                                          border: Border.all(
                                            color: isSelected ? selectedColor : Colors.grey.shade400,
                                            width: 2,
                                          ),
                                          boxShadow: const [
                                            BoxShadow(
                                              color: Color(0x22000000),
                                              blurRadius: 4,
                                              offset: Offset(0, 1),
                                            ),
                                          ],
                                        ),
                                        child: isSelected
                                            ? const Icon(
                                          Icons.check,
                                          size: 16,
                                          color: Colors.white,
                                        )
                                            : null,
                                      ),
                                    ),
                                  ),
                                ),


                            ],
                          ),
                        ),
                      );
                    },

                  );
                },
              );
            },
          ),
        );
      },
    );
  }
}
