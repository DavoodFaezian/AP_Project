import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:test_app/repositories/photo_repository.dart';
// ...
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
  late final AlbumListViewModel viewModel;

  // در صورتی که ریپازیتوری‌های واقعی خود را دارید جایگزین کنید
  final AlbumRepository albumRepository = SocketAlbumRepository();
  final PhotoRepository photoRepository = PhotoRepository();

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
    viewModel.loadAlbums(); // شناسایی کاربر در بک‌اند با sessionId انجام می‌شود
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
    final String? albumName = await showDialog<String>(
      context: context,
      builder: (context) => const NameInputDialog(
        title: 'Create Album',
        label: 'Album name',
        actionLabel: 'Create',
      ),
    );

    if (albumName != null && mounted) {
      WidgetsBinding.instance.addPostFrameCallback((_) async {
        if (mounted) await _createAlbum(albumName);
      });
    }
  }

  Future<void> _showEditDialog(Album album) async {
    final String? newName = await showDialog<String>(
      context: context,
      builder: (context) => NameInputDialog(
        title: 'Edit Album Name',
        label: 'Album name',
        actionLabel: 'Save',
        initialValue: album.albumName,
      ),
    );

    if (newName != null && mounted) {
      WidgetsBinding.instance.addPostFrameCallback((_) async {
        if (mounted) await _updateAlbumName(album.id, newName);
      });
    }
  }

  Future<void> _createAlbum(String albumName) async {
    final trimmedName = albumName.trim();

    if (trimmedName.isEmpty) {
      return;
    }

    await viewModel.addAlbum(
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

  PreferredSizeWidget _buildAppBar(BuildContext context) {
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
        PopupMenuButton<String>(
          icon: const Icon(Icons.sort),
          onSelected: (value) {
            switch (value) {
              case 'name_asc':
                viewModel.sortByName(ascending: true);
                break;
              case 'name_desc':
                viewModel.sortByName(ascending: false);
                break;
              case 'count_asc':
                viewModel.sortByPhotoCount(ascending: true);
                break;
              case 'count_desc':
                viewModel.sortByPhotoCount(ascending: false);
                break;
            }
          },
          itemBuilder: (context) => [
            const PopupMenuItem(value: 'name_asc', child: Text('Name (A-Z)')),
            const PopupMenuItem(value: 'name_desc', child: Text('Name (Z-A)')),
            const PopupMenuItem(value: 'count_asc', child: Text('Photos (Fewest)')),
            const PopupMenuItem(value: 'count_desc', child: Text('Photos (Most)')),
          ],
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
          appBar: _buildAppBar(context),
          floatingActionButton: _selectionMode
              ? null
              : CustomFAB(
                  onPressed: _showCreateDialog,
                ),
          body: _buildBody(context),
        );
      },
    );
  }

  Widget _buildBody(BuildContext context) {
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

    if (viewModel.albums.isEmpty) {
      return const EmptyState(
        key: ValueKey('empty_albums'),
        imagePath: "assets/images/Image post-cuate.png",
        title: "No albums",
        subtitle: "Create your first album.",
      );
    }

    return GridView.builder(
      key: const ValueKey('album_grid'),
      padding: const EdgeInsets.all(16),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 2,
        crossAxisSpacing: 16,
        mainAxisSpacing: 16,
        childAspectRatio: 1.1,
      ),
      itemCount: viewModel.albums.length,
      itemBuilder: (context, index) {
        final album = viewModel.albums[index];
        final isSelected = _selectedAlbumIds.contains(album.id);

        return GestureDetector(
          key: ValueKey(album.id),
          onLongPress: () => _enterSelection(album.id),
          onTap: () {
            if (_selectionMode) {
              _toggleSelection(album.id);
              return;
            }

            Navigator.of(context).push(
              MaterialPageRoute(
                builder: (_) => PhotoListPage(
                  albumId: album.id,
                  albumName: album.albumName,
                  albumOwnerId: album.ownerId,
                  photoRepository: photoRepository,
                  albumRepository: albumRepository,
                ),
              ),
            );
          },
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 200),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(20),
              border: isSelected
                  ? Border.all(color: const Color(0xFF5B21B6), width: 2)
                  : Border.all(color: Colors.grey.shade100),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.04),
                  blurRadius: 10,
                  offset: const Offset(0, 4),
                ),
              ],
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  isSelected ? Icons.check_circle : Icons.folder_rounded,
                  size: 48,
                  color: isSelected ? const Color(0xFF5B21B6) : Colors.blueAccent.shade400,
                ),
                const SizedBox(height: 12),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 8),
                  child: Text(
                    album.albumName,
                    textAlign: TextAlign.center,
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
                Text(
                  '${album.photoIds.length} Photos',
                  style: TextStyle(color: Colors.grey.shade500, fontSize: 12),
                ),
              ],
            ),
          ).animate().fadeIn(delay: (index * 50).ms).scale(delay: (index * 50).ms),
        );
      },
    );
  }
}

class NameInputDialog extends StatefulWidget {
  final String title;
  final String label;
  final String actionLabel;
  final String? initialValue;

  const NameInputDialog({
    super.key,
    required this.title,
    required this.label,
    required this.actionLabel,
    this.initialValue,
  });

  @override
  State<NameInputDialog> createState() => _NameInputDialogState();
}

class _NameInputDialogState extends State<NameInputDialog> {
  late final TextEditingController _controller;

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController(text: widget.initialValue);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(widget.title),
      content: TextField(
        controller: _controller,
        autofocus: true,
        decoration: InputDecoration(
          labelText: widget.label,
          border: const OutlineInputBorder(),
        ),
        onSubmitted: (value) => Navigator.of(context).pop(value),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.of(context).pop(),
          child: const Text('Cancel'),
        ),
        FilledButton(
          onPressed: () => Navigator.of(context).pop(_controller.text),
          child: Text(widget.actionLabel),
        ),
      ],
    );
  }
}
