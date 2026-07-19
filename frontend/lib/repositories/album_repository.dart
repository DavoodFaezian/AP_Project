import '../models/album.dart';

abstract class AlbumRepository {
  Future<List<Album>> getAlbumsByOwner(String ownerId);
  Future<Album> createAlbum({
    required String ownerId,
    required String albumName,
  });
  Future<Album> updateAlbum({
    required String albumId,
    required String albumName,
  });
  Future<void> deleteAlbum(String albumId);
}

class InMemoryAlbumRepository implements AlbumRepository {
  final List<Album> _albums = [
    Album(
      id: 'a1',
      ownerId: 'user1',
      albumName: 'Vacation',
      photoIds: {'p1', 'p2'},
    ),
    Album(
      id: 'a2',
      ownerId: 'user1',
      albumName: 'Family',
      photoIds: {'p3'},
    ),
  ];

  @override
  Future<List<Album>> getAlbumsByOwner(String ownerId) async {
    await Future<void>.delayed(const Duration(milliseconds: 300));
    return _albums.where((album) => album.ownerId == ownerId).toList();
  }

  @override
  Future<Album> createAlbum({
    required String ownerId,
    required String albumName,
  }) async {
    await Future<void>.delayed(const Duration(milliseconds: 300));

    final album = Album(
      id: DateTime.now().microsecondsSinceEpoch.toString(),
      ownerId: ownerId,
      albumName: albumName,
      photoIds: <String>{},
    );

    _albums.add(album);
    return album;
  }

  @override
  Future<Album> updateAlbum({
    required String albumId,
    required String albumName,
  }) async {
    await Future<void>.delayed(const Duration(milliseconds: 300));

    final index = _albums.indexWhere((album) => album.id == albumId);
    if (index == -1) {
      throw Exception('Album not found');
    }

    final updatedAlbum = _albums[index].copyWith(albumName: albumName);
    _albums[index] = updatedAlbum;
    return updatedAlbum;
  }

  @override
  Future<void> deleteAlbum(String albumId) async {
    await Future<void>.delayed(const Duration(milliseconds: 300));
    _albums.removeWhere((album) => album.id == albumId);
  }
}
