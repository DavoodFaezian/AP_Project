import 'package:flutter/foundation.dart';

import '../models/photo.dart';
import '../repositories/photo_repository.dart';

class PhotoListViewModel extends ChangeNotifier {
  PhotoListViewModel({
    required PhotoRepository repository,
    required this.currentUserId,
    this.albumId,
    this.albumOwnerId,
  }) : _repository = repository;

  final PhotoRepository _repository;
  final String currentUserId;
  final String? albumId;
  final String? albumOwnerId;

  List<Photo> _photos = [];
  bool _isLoading = false;
  String? _errorMessage;

  bool _selectionMode = false;
  final Set<String> _selectedPhotoIds = {};

  List<Photo> get photos => List.unmodifiable(_photos);
  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;
  bool get selectionMode => _selectionMode;
  Set<String> get selectedPhotoIds => Set.unmodifiable(_selectedPhotoIds);

  bool get isOwnerContext {
    if (albumOwnerId == null) {
      return true;
    }
    return albumOwnerId == currentUserId;
  }

  bool get isSingleSelection => _selectedPhotoIds.length == 1;
  bool get hasSelection => _selectedPhotoIds.isNotEmpty;

  Future<void> loadPhotos() async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      if (albumId != null) {
        _photos = await _repository.getPhotosByAlbum(albumId!);
      } else {
        _photos = await _repository.getPhotosByOwner(currentUserId);
      }
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  void enterSelection(String photoId) {
    _selectionMode = true;
    _selectedPhotoIds.add(photoId);
    notifyListeners();
  }

  void toggleSelection(String photoId) {
    if (_selectedPhotoIds.contains(photoId)) {
      _selectedPhotoIds.remove(photoId);
    } else {
      _selectedPhotoIds.add(photoId);
    }

    if (_selectedPhotoIds.isEmpty) {
      _selectionMode = false;
    }

    notifyListeners();
  }

  void clearSelection() {
    _selectedPhotoIds.clear();
    _selectionMode = false;
    notifyListeners();
  }

  Photo? get selectedPhoto {
    if (!isSingleSelection) {
      return null;
    }

    final id = _selectedPhotoIds.first;
    try {
      return _photos.firstWhere((photo) => photo.id == id);
    } catch (_) {
      return null;
    }
  }

  Future<void> deleteSelectedPhotos() async {
    for (final id in _selectedPhotoIds) {
      await _repository.deletePhoto(id);
    }
    clearSelection();
    await loadPhotos();
  }
}
