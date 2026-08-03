import 'dart:convert';
import '../models/album.dart';
import '../services/socket_service.dart';
import '../services/session_manager.dart';

abstract class AlbumRepository {
  Future<List<Album>> getAlbumsByOwner();
  Future<Album> createAlbum({
    required String albumName,
  });
  Future<Album> updateAlbum({
    required String albumId,
    required String albumName,
  });
  Future<void> deleteAlbum(String albumId);
}

class SocketAlbumRepository implements AlbumRepository {
  @override
  Future<List<Album>> getAlbumsByOwner() async {
    final requestMap = {
      "action": "Album/getAllAlbumsByOwnerId",
      "sessionId": SessionManager.instance.sessionId,
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] == 200 || responseMap['status'] == 'SUCCESS') {
      List<dynamic> albumsJson = responseMap['albums'] ?? [];
      return albumsJson.map((json) => Album.fromJson(json)).toList();
    } else {
      throw Exception(responseMap['message'] ?? 'Failed to fetch albums');
    }
  }

  @override
  Future<Album> createAlbum({
    required String albumName,
  }) async {
    final requestMap = {
      "action": "Album/addAlbum",
      "sessionId": SessionManager.instance.sessionId,
      "name": albumName,
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] == 200 || responseMap['status'] == 'SUCCESS') {
      return Album.fromJson(responseMap['album']);
    } else {
      throw Exception(responseMap['message'] ?? 'Failed to create album');
    }
  }

  @override
  Future<Album> updateAlbum({
    required String albumId,
    required String albumName,
  }) async {
    final requestMap = {
      "action": "Album/editAlbum",
      "sessionId": SessionManager.instance.sessionId,
      "album": albumName,
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] == 200 || responseMap['status'] == 'SUCCESS') {
      return Album.fromJson(responseMap['album']);
    } else {
      throw Exception(responseMap['message'] ?? 'Failed to update album');
    }
  }

  @override
  Future<void> deleteAlbum(String albumId) async {
    final requestMap = {
      "action": "Album/deleteAlbum",
      "sessionId": SessionManager.instance.sessionId,
      "albumId": albumId,
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] != 200 && responseMap['status'] != 'SUCCESS') {
      throw Exception(responseMap['message'] ?? 'Failed to delete album');
    }
  }
}