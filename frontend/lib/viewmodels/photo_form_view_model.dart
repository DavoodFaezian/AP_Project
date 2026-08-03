import 'package:flutter/foundation.dart';
import '../models/photo.dart';
import '../repositories/photo_repository.dart';

class PhotoFormViewModel extends ChangeNotifier {
  PhotoFormViewModel({
    required PhotoRepository repository,
    this.initialPhoto,
    this.sourceAlbumId,
  }) : _repository = repository {
    if (initialPhoto != null) {
      photoName = initialPhoto!.photoName;
      caption = initialPhoto!.caption;
      tagsText = initialPhoto!.tags.join(', ');
      permissionForLeavingComment = initialPhoto!.permissionForLeavingComment;
      fileName = initialPhoto!.id;
      selectedAlbumIds = Set<String>.from(initialPhoto!.albumIds);
    } else {
      selectedAlbumIds = sourceAlbumId == null ? <String>{} : <String>{sourceAlbumId!};
    }
  }

  final PhotoRepository _repository;
  final Photo? initialPhoto;
  final String? sourceAlbumId;

  String photoName = '';
  String caption = '';
  String tagsText = '';
  bool permissionForLeavingComment = true;
  Set<String> selectedAlbumIds = <String>{};

  String? fileName; // می‌تواند شناسه بایت‌های آپلود شده (Base64) یا آدرس فایل باشد
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

  Future<bool> submit() async {
    if (photoName.trim().isEmpty) {
      errorMessage = 'Photo name is required';
      notifyListeners();
      return false;
    }

    if (!isEdit && (fileName == null || fileName!.trim().isEmpty)) {
      errorMessage = 'Please pick a photo';
      notifyListeners();
      return false;
    }

    isSubmitting = true;
    errorMessage = null;
    notifyListeners();

    try {
      if (isEdit) {
        final updatedPhoto = initialPhoto!.copyWith(
          photoName: photoName.trim(),
          caption: caption.trim(),
          tags: _parseTags(),
          permissionForLeavingComment: permissionForLeavingComment,
          lastModified: DateTime.now(),
          albumIds: Set<String>.from(selectedAlbumIds),
        );
        await _repository.editPhoto(updatedPhoto);
      } else {
        // انتخاب آلبوم اول در صورت وجود (چون addPhoto آلبوم اصلی را می‌گیرد)
        String mainAlbumId = selectedAlbumIds.isNotEmpty ? selectedAlbumIds.first : '';

        await _repository.addPhoto(
          photoName: photoName.trim(),
          title: photoName.trim(),
          albumId: mainAlbumId,
          tags: _parseTags(),
          caption: caption.trim(),
          favorable: false,
        );
      }
      return true;
    } catch (e) {
      errorMessage = e.toString();
      return false;
    } finally {
      isSubmitting = false;
      notifyListeners();
    }
  }

  Future<void> deletePhoto() async {
    if (initialPhoto == null) return;

    isSubmitting = true;
    notifyListeners();

    try {
      await _repository.deletePhoto(initialPhoto!.id);
    } catch (e) {
      errorMessage = e.toString();
    } finally {
      isSubmitting = false;
      notifyListeners();
    }
  }
}