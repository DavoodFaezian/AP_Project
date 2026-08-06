import 'package:flutter/foundation.dart';
import '../models/album.dart';
import '../models/post.dart';
import '../repositories/post_repository.dart';

class PostFormViewModel extends ChangeNotifier {
  final PostRepository _postRepository;
  final Post? initialPost;

  PostFormViewModel({
    required PostRepository postRepository,
    this.initialPost,
  }) : _postRepository = postRepository {
    if (initialPost != null) {
      selectedAlbumIds = Set<String>.from(initialPost!.albumIds);
      _remainingInitialPhotoIds = Set<String>.from(initialPost!.photoIds);
      commentsAllowed = initialPost!.commentsAllowed;
    }
  }

  bool get isEdit => initialPost != null;

  Set<String> selectedAlbumIds = {};
  Set<String> _remainingInitialPhotoIds = {};
  
  // Maps albumId to a Set of specific photoIds selected within that album
  Map<String, Set<String>> selectedPhotosPerAlbum = {};
  
  // Set of photoIds selected globally (via search)
  Set<String> globalSelectedPhotoIds = {};
  
  // Maps photoId to its set of parent albumIds
  final Map<String, Set<String>> _photoIdToAlbumIds = {};
  
  bool commentsAllowed = true;
  bool isSubmitting = false;
  String? errorMessage;

  void onAlbumPhotosLoaded(String albumId, List<String> allPhotosInAlbum) {
    for (var pid in allPhotosInAlbum) {
      _photoIdToAlbumIds.putIfAbsent(pid, () => {}).add(albumId);
    }
    
    if (isEdit && !selectedAlbumIds.contains(albumId) && !selectedPhotosPerAlbum.containsKey(albumId)) {
      final matchingIds = _remainingInitialPhotoIds.where((id) => allPhotosInAlbum.contains(id)).toSet();
      if (matchingIds.isNotEmpty) {
        selectedPhotosPerAlbum[albumId] = matchingIds;
        _remainingInitialPhotoIds.removeAll(matchingIds);
        notifyListeners();
      }
    }
  }

  void initializeSelections(List<Album> allUserAlbums) {
    for (var album in allUserAlbums) {
      for (var pid in album.photoIds) {
        _photoIdToAlbumIds.putIfAbsent(pid, () => {}).add(album.id);
      }
    }
    
    if (!isEdit) return;

    for (final album in allUserAlbums) {
      if (selectedAlbumIds.contains(album.id)) continue;

      final matchingIds = _remainingInitialPhotoIds.where((id) => album.photoIds.contains(id)).toSet();
      if (matchingIds.isNotEmpty) {
        selectedPhotosPerAlbum[album.id] = matchingIds;
        _remainingInitialPhotoIds.removeAll(matchingIds);
      }
    }
    notifyListeners();
  }

  void toggleAlbumSelection(String albumId) {
    if (selectedAlbumIds.contains(albumId)) {
      selectedAlbumIds.remove(albumId);
      selectedPhotosPerAlbum.remove(albumId);
    } else {
      selectedAlbumIds.add(albumId);
      // When whole album is selected, we don't need to track individual photos
      selectedPhotosPerAlbum.remove(albumId);
    }
    notifyListeners();
  }

  void togglePhotoSelection(String albumId, String photoId) {
    if (selectedAlbumIds.contains(albumId)) {
      selectedAlbumIds.remove(albumId);
      selectedPhotosPerAlbum[albumId] = {photoId};
    } else {
      if (!selectedPhotosPerAlbum.containsKey(albumId)) {
        selectedPhotosPerAlbum[albumId] = {};
      }
      
      if (selectedPhotosPerAlbum[albumId]!.contains(photoId)) {
        selectedPhotosPerAlbum[albumId]!.remove(photoId);
        if (selectedPhotosPerAlbum[albumId]!.isEmpty) {
          selectedPhotosPerAlbum.remove(albumId);
        }
      } else {
        selectedPhotosPerAlbum[albumId]!.add(photoId);
      }
    }
    notifyListeners();
  }

  void toggleGlobalPhotoSelection(String photoId, {List<String>? albumIds}) {
    if (albumIds != null) {
      _photoIdToAlbumIds.putIfAbsent(photoId, () => {}).addAll(albumIds);
    }
    
    if (globalSelectedPhotoIds.contains(photoId)) {
      globalSelectedPhotoIds.remove(photoId);
    } else {
      globalSelectedPhotoIds.add(photoId);
    }
    notifyListeners();
  }

  bool isAlbumSelected(String albumId) => selectedAlbumIds.contains(albumId);
  
  int getSelectedPhotoCountForAlbum(Album album) {
    if (selectedAlbumIds.contains(album.id)) return album.photoIds.length;
    
    // Set of photos that are "selected" and belong to this album
    final selectedPhotos = <String>{};
    
    // 1. Photos specifically selected in this album's bucket
    if (selectedPhotosPerAlbum.containsKey(album.id)) {
      selectedPhotos.addAll(selectedPhotosPerAlbum[album.id]!);
    }
    
    // 2. Photos from initial pool that belong to this album
    selectedPhotos.addAll(_remainingInitialPhotoIds.where((id) => 
      album.photoIds.contains(id) || (_photoIdToAlbumIds[id]?.contains(album.id) ?? false)));

    // 3. Photos from global selection that belong to this album
    selectedPhotos.addAll(globalSelectedPhotoIds.where((id) => 
      album.photoIds.contains(id) || (_photoIdToAlbumIds[id]?.contains(album.id) ?? false)));
    
    return selectedPhotos.length;
  }

  bool isPhotoSelected(String? albumId, String photoId, {List<String>? photoAlbumIds}) {
    // 1. Explicitly part of a selected album (if albumId provided)
    if (albumId != null && selectedAlbumIds.contains(albumId)) return true;
    
    // 2. Part of the global search selection pool
    if (globalSelectedPhotoIds.contains(photoId)) return true;
    
    // 3. Specifically selected within an album
    if (albumId != null && (selectedPhotosPerAlbum[albumId]?.contains(photoId) ?? false)) return true;

    // 4. If in Search Mode (albumId is null), check if any of the photo's albums are selected
    if (albumId == null && photoAlbumIds != null) {
      for (final id in photoAlbumIds) {
        if (selectedAlbumIds.contains(id)) return true;
      }
    }

    // 5. Check if it exists in ANY specific selection pool
    for (final set in selectedPhotosPerAlbum.values) {
      if (set.contains(photoId)) return true;
    }
    if (_remainingInitialPhotoIds.contains(photoId)) return true;

    return false;
  }

  void setCommentsAllowed(bool value) {
    commentsAllowed = value;
    notifyListeners();
  }

  Future<bool> submit() async {
    final allPhotoIds = {
      ...selectedPhotosPerAlbum.values.expand((element) => element),
      ...globalSelectedPhotoIds,
      ..._remainingInitialPhotoIds,
    }.toList();
    final allAlbumIds = selectedAlbumIds.toList();

    if (allPhotoIds.isEmpty && allAlbumIds.isEmpty) {
      errorMessage = "Please select at least one album or photo.";
      notifyListeners();
      return false;
    }

    isSubmitting = true;
    errorMessage = null;
    notifyListeners();

    try {
      if (isEdit) {
        await _postRepository.editPost(
          postId: initialPost!.id,
          photoIds: allPhotoIds,
          albumIds: allAlbumIds,
          commentsAllowed: commentsAllowed,
        );
      } else {
        await _postRepository.addPost(
          photoIds: allPhotoIds,
          albumIds: allAlbumIds,
          commentsAllowed: commentsAllowed,
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
}
