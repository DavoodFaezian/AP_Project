import 'package:flutter/foundation.dart';

import '../models/album.dart';
import '../repositories/album_repository.dart';

class AlbumListViewModel extends ChangeNotifier {
  AlbumListViewModel({
    required AlbumRepository albumRepository,
  }) : _albumRepository = albumRepository;

  final AlbumRepository _albumRepository;

  final List<Album> _albums = <Album>[];
  bool _isLoading = false;
  String? _errorMessage;

  List<Album> get albums => List.unmodifiable(_albums);
  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;

  Future<void> loadAlbums(String ownerId) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final albums = await _albumRepository.getAlbumsByOwner(ownerId);
      _albums
        ..clear()
        ..addAll(albums);
    } catch (e) {
      _errorMessage = 'Failed to load albums: $e';
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> addAlbum({
    required String ownerId,
    required String albumName,
  }) async {
    final trimmedName = albumName.trim();
    if (trimmedName.isEmpty) {
      return;
    }

    _errorMessage = null;

    try {
      final createdAlbum = await _albumRepository.createAlbum(
        ownerId: ownerId,
        albumName: trimmedName,
      );
      _albums.add(createdAlbum);
      notifyListeners();
    } catch (e) {
      _errorMessage = 'Failed to add album: $e';
      notifyListeners();
    }
  }

  Future<void> updateAlbumName({
    required String albumId,
    required String newName,
  }) async {
    final trimmedName = newName.trim();
    if (trimmedName.isEmpty) {
      return;
    }

    final index = _albums.indexWhere((album) => album.id == albumId);
    if (index == -1) {
      return;
    }

    _errorMessage = null;

    try {
      final updatedAlbum = await _albumRepository.updateAlbum(
        albumId: albumId,
        albumName: trimmedName,
      );
      _albums[index] = updatedAlbum;
      notifyListeners();
    } catch (e) {
      _errorMessage = 'Failed to update album: $e';
      notifyListeners();
    }
  }

  Future<void> deleteAlbum(String albumId) async {
    _errorMessage = null;

    try {
      await _albumRepository.deleteAlbum(albumId);
      _albums.removeWhere((album) => album.id == albumId);
      notifyListeners();
    } catch (e) {
      _errorMessage = 'Failed to delete album: $e';
      notifyListeners();
    }
  }
}
