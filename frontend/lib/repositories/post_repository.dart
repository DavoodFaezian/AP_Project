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
    return responseMap['postId'] ?? responseMap['id'] ?? '';
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
      actionName: "Post/getAllPostsByOwner",
      payload: {
        "sessionId": SessionManager.instance.sessionId,
      },
    );

    List<dynamic> postsJson = responseMap['posts'] ?? responseMap['data'] ?? [];
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

    List<dynamic> postsJson = responseMap['posts'] ?? responseMap['data'] ?? [];
    return postsJson.map((json) => Post.fromJson(json)).toList();
  }

  /// دریافت پست‌های یک کاربر دیگر با ارسال targetUserId به بک‌اند
    Future<List<Post>> getPostsByUserId(String targetUserId) async {
      final responseMap = await _sendSocketRequest(
        actionName: "Post/getPostsByUserId",
        payload: {
          "sessionId": SessionManager.instance.sessionId,
          "targetUserId": targetUserId, // آیدی کاربری که می‌خواهیم پست‌هایش را ببینیم
        },
      );

      List<dynamic> postsJson = responseMap['posts'] ?? responseMap['data'] ?? [];
      return postsJson.map((json) => Post.fromJson(json)).toList();
    }

  /// ۶. دریافت یک پست با شناسه (getPostById)
  Future<Post> getPostById(String postId) async {
    final responseMap = await _sendSocketRequest(
      actionName: "Post/getPostById",
      payload: {
        "sessionId": SessionManager.instance.sessionId,
        "postId": postId,
      },
    );

    return Post.fromJson(responseMap['post'] ?? responseMap);
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

    List<dynamic> photoIds = responseMap['photoIds'] ?? [];
    return List<String>.from(photoIds);
  }

  /// ۸. دریافت آیدی آلبوم‌های یک پست (getAlbumIdsOfPost)
  Future<List<String>> getAlbumIdsOfPost(String postId) async {
    final responseMap = await _sendSocketRequest(
      actionName: "Post/getAlbumIdsPost",
      payload: {
        "sessionId": SessionManager.instance.sessionId,
        "postId": postId,
      },
    );

    List<dynamic> albumIds = responseMap['albumIds'] ?? [];
    return List<String>.from(albumIds);
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

    if (responseMap['status'] == 'SUCCESS' || responseMap['statusCode'] == '200') {
      return responseMap;
    } else {
      throw Exception(responseMap['message'] ?? 'Action $actionName failed');
    }
  }
}