import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import '../models/comment.dart';

class CommentRepository {
  CommentRepository({
    this.host = '127.0.0.1',
    this.port = 4040,
  });

  final String host;
  final int port;

  static final List<Comment> _mockComments = [
    Comment(id: '1', ownerId: 'user1', script: 'Nice photo!', photoId: 'photo1'),
    Comment(id: '2', ownerId: 'user2', script: 'Looks great!', photoId: 'photo1'),
    Comment(id: '3', ownerId: 'user3', script: 'Amazing shot!', photoId: 'photo2'),
  ];

  Future<List<Comment>> getAllComments() async {
    // TODO: Replace this with a socket request to the Java backend.
    return List<Comment>.from(_mockComments);
  }

  Future<List<Comment>> getCommentsForPhoto(String photoId) async {
    // TODO: Replace this with a socket request to the Java backend.
    return _mockComments.where((comment) => comment.photoId == photoId).toList();
  }

  Future<Comment> addComment(Comment comment) async {
    // TODO: Send "ADD_COMMENT" to the Java socket server and wait for a response.
    final newComment = comment.copyWith(
      id: DateTime.now().microsecondsSinceEpoch.toString(),
    );
    _mockComments.add(newComment);
    return newComment;
  }

  Future<Comment> updateComment(Comment comment) async {
    // TODO: Send "UPDATE_COMMENT" to the Java socket server and wait for a response.
    final index = _mockComments.indexWhere((c) => c.id == comment.id);
    if (index == -1) {
      throw Exception('Comment not found');
    }
    _mockComments[index] = comment;
    return comment;
  }

  Future<void> deleteComment(String commentId) async {
    // TODO: Send "DELETE_COMMENT" to the Java socket server and wait for a response.
    _mockComments.removeWhere((comment) => comment.id == commentId);
  }

  Future<Map<String, dynamic>> _sendRequest(Map<String, dynamic> request) async {
    final socket = await Socket.connect(host, port);

    try {
      socket.write('${jsonEncode(request)}\n');

      final response = await socket
          .transform(utf8.decoder as StreamTransformer<Uint8List, dynamic>)
          .transform(const LineSplitter())
          .first;

      return jsonDecode(response) as Map<String, dynamic>;
    } finally {
      socket.destroy();
    }
  }

  Future<List<Comment>> fetchCommentsFromSocket() async {
    // TODO: Java server should accept this command and return:
    // { "success": true, "comments": [ ... ] }
    final response = await _sendRequest({
      'action': 'get_all_comments',
    });

    if (response['success'] != true) {
      throw Exception(response['message'] ?? 'Failed to load comments');
    }

    final list = response['comments'] as List<dynamic>;
    return list
        .map((item) => Comment.fromJson(item as Map<String, dynamic>))
        .toList();
  }

  Future<Comment> addCommentToSocket(Comment comment) async {
    // TODO: Java server should create/persist the comment and return it.
    final response = await _sendRequest({
      'action': 'add_comment',
      'comment': comment.toJson(),
    });

    if (response['success'] != true) {
      throw Exception(response['message'] ?? 'Failed to add comment');
    }

    return Comment.fromJson(response['comment'] as Map<String, dynamic>);
  }
}
