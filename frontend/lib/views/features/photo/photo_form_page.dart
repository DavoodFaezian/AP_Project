import 'package:flutter/material.dart';
import 'package:test_app/views/features/album/album_selection_field.dart';

import '../../../models/photo.dart';
import '../../../repositories/album_repository.dart';
import '../../../repositories/photo_repository.dart';
import '../../../viewmodels/photo_form_view_model.dart';
import '../share/share_page.dart';

class PhotoFormPage extends StatefulWidget {
  const PhotoFormPage({
    super.key,
    required this.currentUserId,
    required this.photoRepository,
    required this.albumRepository,
    required this.returnToAlbumTitle,
    this.sourceAlbumId,
    this.initialPhoto,
  });

  final String currentUserId;
  final PhotoRepository photoRepository;
  final AlbumRepository albumRepository;
  final String returnToAlbumTitle;
  final String? sourceAlbumId;
  final Photo? initialPhoto;

  @override
  State<PhotoFormPage> createState() => _PhotoFormPageState();
}

class _PhotoFormPageState extends State<PhotoFormPage> {
  late final PhotoFormViewModel viewModel;
  late final TextEditingController photoNameController;
  late final TextEditingController captionController;
  late final TextEditingController tagsController;

  @override
  void initState() {
    super.initState();

    viewModel = PhotoFormViewModel(
      repository: widget.photoRepository,
      currentUserId: widget.currentUserId,
      initialPhoto: widget.initialPhoto,
      sourceAlbumId: widget.sourceAlbumId,
    );

    photoNameController = TextEditingController(text: viewModel.photoName);
    captionController = TextEditingController(text: viewModel.caption);
    tagsController = TextEditingController(text: viewModel.tagsText);
  }

  @override
  void dispose() {
    viewModel.dispose();
    photoNameController.dispose();
    captionController.dispose();
    tagsController.dispose();
    super.dispose();
  }

  Future<void> _pickPhoto() async {
    viewModel.setFileName( DateTime.now().microsecondsSinceEpoch.toString());
  }

  Future<void> _submitCreate() async {
    viewModel.setPhotoName(photoNameController.text);
    viewModel.setCaption(captionController.text);
    viewModel.setTagsText(tagsController.text);

    final createdPhoto = await viewModel.submit();
    if (createdPhoto == null || !mounted) {
      return;
    }

    await Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => SharePage(
          photoId: createdPhoto.id,
          albumTitle: widget.returnToAlbumTitle,
        ),
      ),
    );

    if (!mounted) {
      return;
    }

    Navigator.of(context).pop();
  }

  Future<void> _submitEdit() async {
    viewModel.setPhotoName(photoNameController.text);
    viewModel.setCaption(captionController.text);
    viewModel.setTagsText(tagsController.text);

    final updated = await viewModel.submit();
    if (updated == null || !mounted) {
      return;
    }

    Navigator.of(context).pop();
  }

  Future<void> _deletePhoto() async {
    await viewModel.deletePhoto();
    if (!mounted) return;
    Navigator.of(context).pop();
  }



  void _openShare() {
    final photo = widget.initialPhoto;
    if (photo == null) {
      return;
    }

    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => SharePage(
          photoId: photo.id,
          albumTitle: widget.returnToAlbumTitle,
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final isEdit = widget.initialPhoto != null;

    return AnimatedBuilder(
      animation: viewModel,
      builder: (context, _) {
        return Scaffold(
          appBar: AppBar(
            title: Text(isEdit ? 'Edit Photo' : 'Add Photo'),
          ),
          body: ListView(
            padding: const EdgeInsets.all(16),
            children: [
              OutlinedButton.icon(
                onPressed: _pickPhoto,
                icon: const Icon(Icons.image),
                label: Text(
                  viewModel.fileName == null ? 'Pick Photo' : viewModel.fileName!,
                ),
              ),
              const SizedBox(height: 16),
              TextField(
                controller: photoNameController,
                decoration: const InputDecoration(
                  labelText: 'Photo name',
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 16),
              TextField(
                controller: captionController,
                maxLines: 3,
                decoration: const InputDecoration(
                  labelText: 'Caption',
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 16),
              TextField(
                controller: tagsController,
                decoration: const InputDecoration(
                  labelText: 'Tags (comma separated)',
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 16),
              SwitchListTile(
                value: viewModel.permissionForLeavingComment,
                onChanged: viewModel.setPermissionForLeavingComment,
                contentPadding: EdgeInsets.zero,
                title: const Text('Allow comments'),
              ),
              const SizedBox(height: 16),
              AlbumMultiSelectField(
                albumRepository: widget.albumRepository,
                ownerId: widget.currentUserId,
                initialValue: viewModel.selectedAlbumIds,
                onChanged: viewModel.setSelectedAlbumIds,
              ),
              if (viewModel.errorMessage != null) ...[
                const SizedBox(height: 12),
                Text(
                  viewModel.errorMessage!,
                  style: const TextStyle(color: Colors.red),
                ),
              ],
              const SizedBox(height: 20),
              FilledButton(
                onPressed: viewModel.isSubmitting
                    ? null
                    : (isEdit ? _submitEdit : _submitCreate),
                child: Text(isEdit ? 'Save Changes' : 'Create Photo'),
              ),
              if (isEdit) ...[

                const SizedBox(height: 12),
                OutlinedButton(
                  onPressed: _openShare,
                  child: const Text('Share'),
                ),
                const SizedBox(height: 12),
                OutlinedButton(
                  onPressed: _deletePhoto,
                  child: const Text('Delete'),
                ),
              ],
            ],
          ),
        );
      },
    );
  }
}
