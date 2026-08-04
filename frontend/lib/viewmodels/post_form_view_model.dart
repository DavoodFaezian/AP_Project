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
  
  bool commentsAllowed = true;
  bool isSubmitting = false;
  String? errorMessage;

  void onAlbumPhotosLoaded(String albumId, List<String> allPhotosInAlbum) {
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
    // If the whole album was selected, and we tap a specific photo, 
    // maybe we want to switch to individual selection? 
    // The requirement says: "User can select by holding and go to it's images by tapping"
    // "User can select the entire album or the images inside the album"
    
    if (selectedAlbumIds.contains(albumId)) {
      // If entire album was selected, we deselect it and select only this photo
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

  bool isAlbumSelected(String albumId) => selectedAlbumIds.contains(albumId);
  
  int getSelectedPhotoCountForAlbum(String albumId) {
    return selectedPhotosPerAlbum[albumId]?.length ?? 0;
  }

  bool isPhotoSelected(String albumId, String photoId) {
    return selectedPhotosPerAlbum[albumId]?.contains(photoId) ?? false;
  }

  void setCommentsAllowed(bool value) {
    commentsAllowed = value;
    notifyListeners();
  }

  Future<bool> submit() async {
    final allPhotoIds = {
      ...selectedPhotosPerAlbum.values.expand((element) => element),
      ..._remainingInitialPhotoIds, // Keep photos that weren't assigned to any loaded album
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
