import 'package:flutter/material.dart';
import 'package:test_app/views/components/widgets/photo_upload_input.dart';
import 'package:test_app/views/features/album/album_selection_field.dart';

import '../../../models/photo.dart';
import '../../../repositories/album_repository.dart';
import '../../../repositories/photo_repository.dart';
import '../../../viewmodels/photo_form_view_model.dart';


class PhotoFormPage extends StatefulWidget {
  const PhotoFormPage({
    super.key,
    required this.photoRepository,
    required this.albumRepository,
    required this.returnToAlbumTitle,
    this.sourceAlbumId,
    this.initialPhoto,
  });

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
  late final TextEditingController titleController;
  late final TextEditingController captionController;
  late final TextEditingController tagsController;

  @override
  void initState() {
    super.initState();

    viewModel = PhotoFormViewModel(
      repository: widget.photoRepository,
      initialPhoto: widget.initialPhoto,
      sourceAlbumId: widget.sourceAlbumId,
    );

    titleController = TextEditingController(text: viewModel.title);
    captionController = TextEditingController(text: viewModel.caption);
    tagsController = TextEditingController(text: viewModel.tagsText);
  }

  @override
  void dispose() {
    viewModel.dispose();
    titleController.dispose();
    captionController.dispose();
    tagsController.dispose();
    super.dispose();
  }

  Future<void> _submitCreate() async {
    viewModel.setTitle(titleController.text);
    viewModel.setCaption(captionController.text);
    viewModel.setTagsText(tagsController.text);

    // دریافت مستقیم photoId از متد submit
    final result = await viewModel.submit();
    if (!result || !mounted) {
      return;
    }

    Navigator.of(context).pop();
  }

  Future<void> _submitEdit() async {
    viewModel.setTitle(titleController.text);
    viewModel.setCaption(captionController.text);
    viewModel.setTagsText(tagsController.text);

    final result = await viewModel.submit();
    if (!result || !mounted) {
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
              PhotoUploadInput(
                onPhotoUploaded: (photoName) {
                  viewModel.setPhotoName(photoName);
                  viewModel.setFileName(photoName);
                },
                initialPhotoName: viewModel.photoName,
                title: 'Upload Photo',
                buttonText: isEdit ? 'Change Photo' : 'Select Photo',
              ),
              const SizedBox(height: 24),
              TextField(
                controller: titleController,
                decoration: const InputDecoration(
                  labelText: 'Title',
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
              AlbumMultiSelectField(
                albumRepository: widget.albumRepository,
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
