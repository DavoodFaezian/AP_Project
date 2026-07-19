import '../models/photo.dart';

abstract class PhotoRepository {
  Future<List<Photo>> getPhotosByOwner(String ownerId);
  Future<List<Photo>> getPhotosByAlbum(String albumId);
  Future<Photo> getPhotoById(String photoId);
  Future<Photo> createPhoto(Photo photo);
  Future<Photo> updatePhoto(Photo photo);
  Future<void> deletePhoto(String photoId);
  Future<void> assignPhotoToAlbums({
    required String photoId,
    required Set<String> albumIds,
  });
}

class InMemoryPhotoRepository implements PhotoRepository {
  final List<Photo> _photos = [];

  @override
  Future<List<Photo>> getPhotosByOwner(String ownerId) async {
    return _photos.where((photo) => photo.ownerId == ownerId).toList();
  }

  @override
  Future<List<Photo>> getPhotosByAlbum(String albumId) async {
    return _photos.where((photo) => photo.albumIds.contains(albumId)).toList();
  }

  @override
  Future<Photo> getPhotoById(String photoId) async {
    final photo = _photos.where((item) => item.id == photoId).firstOrNull;
    if (photo == null) {
      throw Exception('Photo not found');
    }

    return photo;
  }

  @override
  Future<Photo> createPhoto(Photo photo) async {
    _photos.add(photo);
    return photo;
  }

  @override
  Future<Photo> updatePhoto(Photo photo) async {
    final index = _photos.indexWhere((item) => item.id == photo.id);
    if (index == -1) {
      throw Exception('Photo not found');
    }

    _photos[index] = photo;
    return photo;
  }

  @override
  Future<void> deletePhoto(String photoId) async {
    _photos.removeWhere((photo) => photo.id == photoId);
  }

  @override
  Future<void> assignPhotoToAlbums({
    required String photoId,
    required Set<String> albumIds,
  }) async {
    final index = _photos.indexWhere((item) => item.id == photoId);
    if (index == -1) {
      throw Exception('Photo not found');
    }

    final current = _photos[index];
    _photos[index] = current.copyWith(
      albumIds: albumIds,
      lastModified: DateTime.now(),
    );
  }
}
