import 'dart:convert';
import '../models/album.dart';
import '../services/socket_service.dart';
import '../services/session_manager.dart';

abstract class AlbumRepository {
  Future<List<Album>> getAlbumsByOwner();
  Future<List<Album>> getAllAlbums(); // Alias for selecting
  Future<Album> getAlbumById(String albumId);
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
      "actionName": "Album/getAllAlbumsByOwnerId",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['status'] == "200" || responseMap['statusCode'] == 200) {
      List<dynamic> albumsJson = responseMap['payload']["albums"] ?? [];
      return albumsJson.map((json) => Album.fromJson(json)).toList();
    } else {
      throw Exception(responseMap['message'] ?? 'Failed to fetch albums');
    }
  }

  @override
  Future<List<Album>> getAllAlbums() => getAlbumsByOwner();

  @override
  Future<Album> getAlbumById(String albumId) async {
    final requestMap = {
      "actionName": "Album/getAlbumById",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "albumId": albumId,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['status'] == "200" || responseMap['statusCode'] == 200) {
      return Album.fromJson(responseMap['payload'] ?? responseMap['data']);
    } else {
      throw Exception(responseMap['message'] ?? 'Album not found');
    }
  }

  @override
  Future<Album> createAlbum({
    required String albumName,
  }) async {
    final requestMap = {
      "actionName": "Album/addAlbum",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "name": albumName,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['status'] == "200") {
      String id = responseMap['payload']['id'];
      return Album(id: id, albumName: albumName);
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
      "actionName": "Album/editAlbum",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "albumId": albumId,
        "albumName": albumName,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['status'] == "200") {
      return Album(id: albumId, albumName: albumName);
    }
    else {
      throw Exception(responseMap['message'] ?? 'Failed to update album');
    }
  }

  @override
  Future<void> deleteAlbum(String albumId) async {
    final requestMap = {
      "actionName": "Album/deleteAlbum",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "albumId": albumId,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['status'] != "200") {
      throw Exception(responseMap['message'] ?? 'Failed to delete album');
    }
  }
}