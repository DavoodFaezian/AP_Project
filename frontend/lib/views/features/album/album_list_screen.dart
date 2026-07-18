import 'package:flutter/material.dart';
import 'package:test_app/repositories/photo_repository.dart';
import 'package:test_app/views/features/photo/photo_list_page.dart';

import '../../../models/album.dart';
import '../../../repositories/album_repository.dart';
import '../../../viewmodels/album_list_view_model.dart';
import '../../components/widgets/custom_appbar.dart';
import '../../components/widgets/custom_drawer.dart';
import '../../components/widgets/custom_fab.dart';
import '../../components/widgets/empty_screen.dart';


class AlbumListPage extends StatefulWidget {
  const AlbumListPage({super.key});

  @override
  State<AlbumListPage> createState() => _AlbumListPageState();
}

class _AlbumListPageState extends State<AlbumListPage> {
  static const String _currentUserId = 'user1';

  late final AlbumListViewModel viewModel;

  final AlbumRepository albumRepository = InMemoryAlbumRepository();
  final PhotoRepository photoRepository = InMemoryPhotoRepository();

  final Set<String> _selectedAlbumIds = {};

  bool get _selectionMode => _selectedAlbumIds.isNotEmpty;
  bool get _isSingleSelection => _selectedAlbumIds.length == 1;

  Album? get _selectedAlbum {
    if (!_isSingleSelection) {
      return null;
    }

    final selectedId = _selectedAlbumIds.first;

    try {
      return viewModel.albums.firstWhere((album) => album.id == selectedId);
    } catch (_) {
      return null;
    }
  }

  @override
  void initState() {
    super.initState();
    viewModel = AlbumListViewModel(albumRepository: albumRepository);
    viewModel.loadAlbums(_currentUserId);
  }

  @override
  void dispose() {
    viewModel.dispose();
    super.dispose();
  }

  void _enterSelection(String albumId) {
    setState(() {
      _selectedAlbumIds
        ..clear()
        ..add(albumId);
    });
  }

  void _toggleSelection(String albumId) {
    setState(() {
      if (_selectedAlbumIds.contains(albumId)) {
        _selectedAlbumIds.remove(albumId);
      } else {
        _selectedAlbumIds.add(albumId);
      }
    });
  }

  void _clearSelection() {
    setState(_selectedAlbumIds.clear);
  }

  Future<void> _showCreateDialog() async {
    final controller = TextEditingController();

    await showDialog<void>(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Create Album'),
          content: TextField(
            controller: controller,
            autofocus: true,
            decoration: const InputDecoration(
              labelText: 'Album name',
              border: OutlineInputBorder(),
            ),
            onSubmitted: (_) async {
              await _createAlbum(controller.text);

              if (!context.mounted) {
                return;
              }

              Navigator.of(context).pop();
            },
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const Text('Cancel'),
            ),
            FilledButton(
              onPressed: () async {
                await _createAlbum(controller.text);

                if (!context.mounted) {
                  return;
                }

                Navigator.of(context).pop();
              },
              child: const Text('Create'),
            ),
          ],
        );
      },
    );

    controller.dispose();
  }

  Future<void> _showEditDialog(Album album) async {
    final controller = TextEditingController(text: album.albumName);

    await showDialog<void>(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Edit Album Name'),
          content: TextField(
            controller: controller,
            autofocus: true,
            decoration: const InputDecoration(
              labelText: 'Album name',
              border: OutlineInputBorder(),
            ),
            onSubmitted: (_) async {
              await _updateAlbumName(album.id, controller.text);

              if (!context.mounted) {
                return;
              }

              Navigator.of(context).pop();
            },
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const Text('Cancel'),
            ),
            FilledButton(
              onPressed: () async {
                await _updateAlbumName(album.id, controller.text);

                if (!context.mounted) {
                  return;
                }

                Navigator.of(context).pop();
              },
              child: const Text('Save'),
            ),
          ],
        );
      },
    );

    controller.dispose();
  }

  Future<void> _createAlbum(String albumName) async {
    final trimmedName = albumName.trim();

    if (trimmedName.isEmpty) {
      return;
    }

    await viewModel.addAlbum(
      ownerId: _currentUserId,
      albumName: trimmedName,
    );
  }

  Future<void> _updateAlbumName(String albumId, String albumName) async {
    final trimmedName = albumName.trim();

    if (trimmedName.isEmpty) {
      return;
    }

    await viewModel.updateAlbumName(
      albumId: albumId,
      newName: trimmedName,
    );

    _clearSelection();
  }

  Future<void> _editSelectedAlbum() async {
    final album = _selectedAlbum;

    if (album == null) {
      return;
    }

    await _showEditDialog(album);
  }

  Future<void> _deleteSelectedAlbums() async {
    final albumIds = _selectedAlbumIds.toList();

    for (final albumId in albumIds) {
      await viewModel.deleteAlbum(albumId);
    }

    _clearSelection();
  }

  PreferredSizeWidget _buildAppBar() {
    if (_selectionMode) {
      return CustomAppBar(
        title: '${_selectedAlbumIds.length} Selected',
        leading: IconButton(
          icon: const Icon(Icons.close),
          onPressed: _clearSelection,
        ),
        actions: [
          if (_isSingleSelection)
            IconButton(
              icon: const Icon(Icons.edit),
              onPressed: _editSelectedAlbum,
            ),
          IconButton(
            icon: const Icon(Icons.delete),
            onPressed: _deleteSelectedAlbums,
          ),
        ],
      );
    }

    return CustomAppBar(
      title: 'Albums',
      leading: Builder(
        builder: (context) => IconButton(
          icon: const Icon(Icons.menu),
          onPressed: () => Scaffold.of(context).openDrawer(),
        ),
      ),
      actions: [
        IconButton(
          onPressed: () {
            // Sort operation placeholder
          },
          icon: const Icon(Icons.sort),
        ),
        IconButton(
          onPressed: () {
            // Filter operation placeholder
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
          drawer: const CustomDrawer(),
          appBar: _buildAppBar(),
          floatingActionButton: _selectionMode
              ? null
              : CustomFAB(
            onPressed: _showCreateDialog,
          ),
          body: Builder(
            builder: (context) {
              if (viewModel.isLoading) {
                return const Center(child: CircularProgressIndicator());
              }

              if (viewModel.errorMessage != null) {
                return Center(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Text(viewModel.errorMessage!),
                  ),
                );
              }

              // Displaying the EmptyState when the album list is empty
              if (viewModel.albums.isEmpty) {
                return const Padding(
                  padding: EdgeInsets.all(8.0),
                  child: EmptyState(
                    imagePath: "assets/images/Image post-cuate.png",
                    title: "No albums",
                    subtitle: "Create your first album.",
                  ),
                );
              }

              return ListView.builder(
                padding: const EdgeInsets.all(8),
                itemCount: viewModel.albums.length,
                itemBuilder: (context, index) {
                  final album = viewModel.albums[index];
                  final isSelected = _selectedAlbumIds.contains(album.id);

                  return ListTile(
                    selected: isSelected,
                    selectedTileColor: Theme.of(context)
                        .colorScheme
                        .primaryContainer
                        .withOpacity(0.35),
                    leading: Icon(
                      isSelected
                          ? Icons.check_circle
                          : Icons.photo_album,
                      color: isSelected
                          ? Theme.of(context).colorScheme.primary
                          : null,
                    ),
                    title: Text(album.albumName),
                    subtitle: Text('${album.photoIds.length} photos'),
                    onLongPress: () => _enterSelection(album.id),
                    onTap: () {
                      if (_selectionMode) {
                        _toggleSelection(album.id);
                        return;
                      }

                      Navigator.of(context).push(
                        MaterialPageRoute(
                          builder: (_) => PhotoListPage(
                            currentUserId: _currentUserId,
                            albumId: album.id,
                            albumName: album.albumName,
                            albumOwnerId: album.ownerId,
                            photoRepository: photoRepository,
                            albumRepository: albumRepository,
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
