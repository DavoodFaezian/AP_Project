import 'dart:convert';
import '../models/comment.dart';
import '../services/session_manager.dart';
import '../services/socket_service.dart';

class CommentRepository {
  Future<String> addComment({
    required String script,
    required String postId,
    required String postOwnerId,
  }) async {
    final responseMap = await _sendSocketRequest(
      actionName: "Comment/addComment",
      payload: {
        "sessionId": SessionManager.instance.sessionId,
        "script": script,
        "postId": postId,
        "postOwnerId": postOwnerId,
      },
    );
    // returns StringResultDto
    return responseMap['payload']?['id'] ?? responseMap['id'] ?? '';
  }

  Future<void> editComment({
    required String commentId,
    required String script,
    required String postId,
  }) async {
    await _sendSocketRequest(
      actionName: "Comment/editComment",
      payload: {
        "Id": commentId,
        "sessionId": SessionManager.instance.sessionId,
        "postId": postId,
        "script": script,
      },
    );
  }

  Future<void> deleteComment({
    required String commentId,
    required String postId,
    required String postOwnerId,
  }) async {
    await _sendSocketRequest(
      actionName: "Comment/deleteComment",
      payload: {
        "id": commentId,
        "sessionId": SessionManager.instance.sessionId,
        "postId": postId,
        "postOwnerId": postOwnerId,
      },
    );
  }

  Future<List<Comment>> getAllCommentsByPostId(String postId) async {
    final responseMap = await _sendSocketRequest(
      actionName: "Comment/getAllCommentsByPostId",
      payload: {
        "postId": postId,
        "sessionId": SessionManager.instance.sessionId,
      },
    );

    final payload = responseMap['payload'];
    List<dynamic> commentsJson = [];
    if (payload is Map && payload.containsKey('comments')) {
      commentsJson = payload['comments'];
    } else if (payload is List) {
      commentsJson = payload;
    }

    return commentsJson.map((json) => Comment.fromJson(json)).toList();
  }

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

    if (responseMap['status'] == 'SUCCESS' || responseMap['status'] == '200' || responseMap['statusCode'] == 200 || responseMap['statusCode'] == '200') {
      return responseMap;
    } else {
      throw Exception(responseMap['message'] ?? 'Action $actionName failed');
    }
  }
}
