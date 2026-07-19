import 'package:flutter/foundation.dart';

import '../models/photo.dart';
import '../repositories/photo_repository.dart';

class PhotoFormViewModel extends ChangeNotifier {
  PhotoFormViewModel({
    required PhotoRepository repository,
    required this.currentUserId,
    this.initialPhoto,
    this.sourceAlbumId,
  }) : _repository = repository {
    if (initialPhoto != null) {
      photoName = initialPhoto!.photoName;
      caption = initialPhoto!.caption;
      tagsText = initialPhoto!.tags.join(', ');
      permissionForLeavingComment =
          initialPhoto!.permissionForLeavingComment;
      fileName = initialPhoto!.id;
      selectedAlbumIds = Set<String>.from(initialPhoto!.albumIds);
    } else {
      selectedAlbumIds =
      sourceAlbumId == null ? <String>{} : <String>{sourceAlbumId!};
    }
  }

  final PhotoRepository _repository;
  final String currentUserId;
  final Photo? initialPhoto;
  final String? sourceAlbumId;

  String photoName = '';
  String caption = '';
  String tagsText = '';
  bool permissionForLeavingComment = true;
  Set<String> selectedAlbumIds = <String>{};

  String? fileName;
  bool isSubmitting = false;
  String? errorMessage;

  bool get isEdit => initialPhoto != null;

  void setPhotoName(String value) => photoName = value;
  void setCaption(String value) => caption = value;
  void setTagsText(String value) => tagsText = value;

  void setPermissionForLeavingComment(bool value) {
    permissionForLeavingComment = value;
    notifyListeners();
  }

  void setFileName(String value) {
    fileName = value;
    notifyListeners();
  }

  void setSelectedAlbumIds(Set<String> value) {
    selectedAlbumIds = Set<String>.from(value);
    notifyListeners();
  }

  Set<String> _parseTags() {
    return tagsText
        .split(',')
        .map((tag) => tag.trim())
        .where((tag) => tag.isNotEmpty)
        .toSet();
  }

  Future<Photo?> submit() async {
    if (photoName.trim().isEmpty) {
      errorMessage = 'Photo name is required';
      notifyListeners();
      return null;
    }

    if (fileName == null || fileName!.trim().isEmpty) {
      errorMessage = 'Please pick a photo';
      notifyListeners();
      return null;
    }

    isSubmitting = true;
    errorMessage = null;
    notifyListeners();

    try {
      if (isEdit) {
        final updated = initialPhoto!.copyWith(
          photoName: photoName.trim(),
          caption: caption.trim(),
          tags: _parseTags(),
          permissionForLeavingComment: permissionForLeavingComment,
          lastModified: DateTime.now(),
          albumIds: Set<String>.from(selectedAlbumIds),
        );
        return await _repository.updatePhoto(updated);
      }

      final now = DateTime.now();

      final photo = Photo(
        id: fileName!.trim(),
        ownerId: currentUserId,
        photoName: photoName.trim(),
        tags: _parseTags(),
        caption: caption.trim(),
        isFavorable: false,
        permissionForLeavingComment: permissionForLeavingComment,
        dateOfShare: null,
        lastModified: now,
        commentIds: <String>{},
        albumIds: Set<String>.from(selectedAlbumIds),
        sharedUserIds: <String>{},
        createdAt: now,
      );
      return await _repository.createPhoto(photo);
    } catch (e) {
      errorMessage = e.toString();
      return null;
    } finally {
      isSubmitting = false;
      notifyListeners();
    }
  }

  Future<void> deletePhoto() async {
    if (initialPhoto == null) {
      return;
    }

    isSubmitting = true;
    notifyListeners();

    try {
      await _repository.deletePhoto(initialPhoto!.id);
    } finally {
      isSubmitting = false;
      notifyListeners();
    }
  }
}
