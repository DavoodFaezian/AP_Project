import 'dart:convert';
import '../models/post.dart';
import '../services/session_manager.dart';
import '../services/socket_service.dart';

class PostRepository {
  /// ۱. افزودن پست جدید (addPost)
  Future<String> addPost({
    required List<String> photoIds,
    required List<String> albumIds,
    required bool commentsAllowed,
  }) async {
    final responseMap = await _sendSocketRequest(
      actionName: "Post/addPost",
      payload: {
        "sessionId": SessionManager.instance.sessionId,
        "photoIds": photoIds,
        "albumIds": albumIds,
        "commentsAllowed": commentsAllowed,
      },
    );
    // Based on StringResultDto return id
    return responseMap['payload']['id'] ?? '';
  }

  /// ۲. ویرایش پست (editPost)
  Future<void> editPost({
    required String postId,
    required List<String> photoIds,
    required List<String> albumIds,
    required bool commentsAllowed,
  }) async {
    await _sendSocketRequest(
      actionName: "Post/editPost",
      payload: {
        "sessionId": SessionManager.instance.sessionId,
        "id": postId,
        "photoIds": photoIds,
        "albumIds": albumIds,
        "commentsAllowed": commentsAllowed,
      },
    );
  }

  /// ۳. حذف پست (deletePost)
  Future<void> deletePost(String postId) async {
    await _sendSocketRequest(
      actionName: "Post/deletePost",
      payload: {
        "sessionId": SessionManager.instance.sessionId,
        "id": postId,
      },
    );
  }

  /// ۴. دریافت تمامی پست‌های کاربر جاری (getAllPostsByOwnerId)
  Future<List<Post>> getAllPostsByOwner() async {
    final responseMap = await _sendSocketRequest(
      actionName: "Post/getAllPostsByOwnerId",
      payload: {
        "sessionId": SessionManager.instance.sessionId,
      },
    );

    final payload = responseMap['payload'];
    List<dynamic> postsJson = [];
    if (payload is Map && payload.containsKey('posts')) {
      postsJson = payload['posts'];
    } else if (payload is List) {
      postsJson = payload;
    }

    return postsJson.map((json) => Post.fromJson(json)).toList();
  }

  /// ۵. دریافت پست‌های افراد دنبال‌شده (getAllPostsOfFollowings)
  Future<List<Post>> getAllPostsOfFollowings() async {
    final responseMap = await _sendSocketRequest(
      actionName: "Post/getAllPostsOfFollowings",
      payload: {
        "sessionId": SessionManager.instance.sessionId,
      },
    );

    final payload = responseMap['payload'];
    List<dynamic> postsJson = [];
    if (payload is Map && payload.containsKey('posts')) {
      postsJson = payload['posts'];
    } else if (payload is List) {
      postsJson = payload;
    }

    return postsJson.map((json) => Post.fromJson(json)).toList();
  }

  /// ۶. دریافت یک پست با شناسه (getPostById)
  Future<Post> getPostById(String postId, String ownerId) async {
    final responseMap = await _sendSocketRequest(
      actionName: "Post/getPostById",
      payload: {
        "sessionId": SessionManager.instance.sessionId,
        "postId": postId,
        "ownerId": ownerId,
      },
    );

    return Post.fromJson(responseMap['payload'] ?? responseMap);
  }


  /// ۷. دریافت آیدی عکس‌های یک پست (getPhotoIdsOfPost)
  Future<List<String>> getPhotoIdsOfPost(String postId) async {
    final responseMap = await _sendSocketRequest(
      actionName: "Post/getPhotoIdsOfPost",
      payload: {
        "sessionId": SessionManager.instance.sessionId,
        "postId": postId,
      },
    );

    final payload = responseMap['payload'];
    List<dynamic> ids = [];
    if (payload is Map && payload.containsKey('ids')) {
      ids = payload['ids'];
    } else if (payload is List) {
      ids = payload;
    }
    return List<String>.from(ids);
  }

  /// ۸. دریافت آیدی آلبوم‌های یک پست (getAlbumIdsOfPost)
  Future<List<String>> getAlbumIdsOfPost(String postId) async {
    final responseMap = await _sendSocketRequest(
      actionName: "Post/getAlbumIdsOfPost",
      payload: {
        "sessionId": SessionManager.instance.sessionId,
        "postId": postId,
      },
    );

    final payload = responseMap['payload'];
    List<dynamic> ids = [];
    if (payload is Map && payload.containsKey('ids')) {
      ids = payload['ids'];
    } else if (payload is List) {
      ids = payload;
    }
    return List<String>.from(ids);
  }

  /// متد کمکی جهت کپسوله‌سازی ارسال درخواست سوکت
  Future<Map<String, dynamic>> _sendSocketRequest({
    required String actionName,
    required Map<String, dynamic> payload,
  }) async {
    final requestMap = {
      "actionName": actionName,
      "payload": payload,
    };

    String jsonRequest = jsonEncode(requestMap) + "\n";
    String rawResponse = await SocketService.sendRequest(jsonRequest);
    Map<String, dynamic> responseMap = jsonDecode(rawResponse);

    if (responseMap['status'] == 'SUCCESS' || responseMap['status'] == '200' || responseMap['statusCode'] == '200' || responseMap['statusCode'] == 200) {
      return responseMap;
    } else {
      throw Exception(responseMap['message'] ?? 'Action $actionName failed');
    }
  }

}
