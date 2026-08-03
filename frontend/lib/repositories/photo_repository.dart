import 'dart:convert';
import '../models/photo.dart';
import '../services/socket_service.dart';
import '../services/session_manager.dart';

class PhotoRepository {
  
  // 1. دریافت همه عکس‌های کاربر (getPhotosByOwnerId)
  Future<List<Photo>> getPhotosByOwnerId() async {
    final requestMap = {
      "actionName": "Photo/getPhotosByOwnerId",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] == 200 || responseMap['status'] == 'SUCCESS') {
      List<dynamic> photosJson = responseMap['data'] ?? responseMap['photos'] ?? [];
      return photosJson.map((json) => Photo.fromJson(json)).toList();
    } else {
      throw Exception(responseMap['message'] ?? 'Failed to fetch user photos');
    }
  }

  // 2. دریافت عکس بر اساس ID (getPhotoById)
  Future<Photo> getPhotoById(String photoId) async {
    final requestMap = {
      "actionName": "Photo/getPhotoById",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "photoId": photoId,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] == 200 || responseMap['status'] == 'SUCCESS') {
      return Photo.fromJson(responseMap['photo'] ?? responseMap['data']);
    } else {
      throw Exception(responseMap['message'] ?? 'Photo not found');
    }
  }

  // 3. دریافت بایت‌های عکس (getPhotoBytes - Base64)
  Future<String> getPhotoBytes(String photoId) async {
    final requestMap = {
      "actionName": "Photo/getPhotoBytes",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "photoId": photoId,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] == 200 || responseMap['status'] == 'SUCCESS') {
      return responseMap['photoData'] ?? responseMap['data']; // رشته Base64 بایت‌ها
    } else {
      throw Exception(responseMap['message'] ?? 'Failed to fetch photo bytes');
    }
  }

  // 4. آپلود بایت‌های عکس به سرور (uploadPhoto)
  Future<String> uploadPhoto(String base64PhotoData) async {
    final requestMap = {
      "actionName": "Photo/uploadPhoto",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "photoData": base64PhotoData,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] == 200 || responseMap['status'] == 'SUCCESS') {
      return responseMap['photoId'] ?? responseMap['data']; // شناسه فایل ذخیره شده
    } else {
      throw Exception(responseMap['message'] ?? 'Failed to upload photo bytes');
    }
  }

  // 5. ایجاد عکس جدید در دیتابیس (addPhoto)
  Future<String> addPhoto({
    required String photoName,
    required String title,
    required String albumId,
    required Set<String> tags,
    required String caption,
    required bool favorable,
  }) async {
    final requestMap = {
      "actionName": "Photo/addPhoto",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "photoName": photoName,
        "title": title,
        "albumId": albumId,
        "tags": tags.toList(),
        "caption": caption,
        "favorable": favorable,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] == 200 || responseMap['status'] == 'SUCCESS') {
      return responseMap['photoId'] ?? responseMap['data']; // آیدی عکس ساخته شده
    } else {
      throw Exception(responseMap['message'] ?? 'Failed to add photo');
    }
  }

  // 6. ویرایش اطلاعات عکس (editPhoto)
  Future<void> editPhoto(Photo photo) async {
    final requestMap = {
      "actionName": "Photo/editPhoto",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "photo": photo.toJson(),
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] != 200 && responseMap['status'] != 'SUCCESS') {
      throw Exception(responseMap['message'] ?? 'Failed to edit photo');
    }
  }

  // 7. حذف عکس (deletePhoto)
  Future<void> deletePhoto(String photoId) async {
    final requestMap = {
      "actionName": "Photo/deletePhoto",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "photoId": photoId,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] != 200 && responseMap['status'] != 'SUCCESS') {
      throw Exception(responseMap['message'] ?? 'Failed to delete photo');
    }
  }

  // 8. دریافت عکس‌های یک آلبوم مشخص (getPhotosByAlbumId)
  Future<List<Photo>> getPhotosByAlbumId(String albumId) async {
    final requestMap = {
      "actionName": "PhotoAlbum/getPhotosByAlbumId",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "albumId": albumId,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] == 200 || responseMap['status'] == 'SUCCESS') {
      List<dynamic> photosJson = responseMap['photos'] ?? responseMap['data'] ?? [];
      return photosJson.map((json) => Photo.fromJson(json)).toList();
    } else {
      throw Exception(responseMap['message'] ?? 'Failed to fetch album photos');
    }
  }

  // 9. اضافه کردن عکس به آلبوم (addPhotoToAlbum)
  Future<void> addPhotoToAlbum(String photoId, String albumId) async {
    final requestMap = {
      "actionName": "PhotoAlbum/addPhotoToAlbum",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "photoId": photoId,
        "albumId": albumId,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] != 200 && responseMap['status'] != 'SUCCESS') {
      throw Exception(responseMap['message'] ?? 'Failed to add photo to album');
    }
  }

  // 10. حذف عکس از آلبوم (removePhotoFromAlbum)
  Future<void> removePhotoFromAlbum(String photoId, String albumId) async {
    final requestMap = {
      "actionName": "PhotoAlbum/removePhotoFromAlbum",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "photoId": photoId,
        "albumId": albumId,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] != 200 && responseMap['status'] != 'SUCCESS') {
      throw Exception(responseMap['message'] ?? 'Failed to remove photo from album');
    }
  }

  // 11. جابجایی عکس بین دو آلبوم (movePhoto)
  Future<void> movePhoto({
    required String photoId,
    required String fromAlbumId,
    required String toAlbumId,
  }) async {
    final requestMap = {
      "actionName": "PhotoAlbum/movePhoto",
      "payload": {
        "sessionId": SessionManager.instance.sessionId,
        "photoId": photoId,
        "fromAlbumId": fromAlbumId,
        "toAlbumId": toAlbumId,
      }
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['statusCode'] != '200' && responseMap['status'] != 'SUCCESS') {
      throw Exception(responseMap['message'] ?? 'Failed to move photo');
    }
  }
}