import 'package:flutter/foundation.dart';

import '../models/comment.dart';
import '../repositories/comment_repository.dart';

class CommentViewModel extends ChangeNotifier {
  CommentViewModel(this._repository);

  final CommentRepository _repository;

  List<Comment> _comments = [];
  bool _isLoading = false;
  String? _error;

  List<Comment> get comments => List.unmodifiable(_comments);
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> loadComments() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _comments = await _repository.getAllComments();
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> loadCommentsForPhoto(String photoId) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _comments = await _repository.getCommentsForPhoto(photoId);
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> addComment(Comment comment) async {
    _error = null;

    try {
      final newComment = await _repository.addComment(comment);
      _comments = [..._comments, newComment];
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  Future<void> updateComment(Comment comment) async {
    _error = null;

    try {
      final updatedComment = await _repository.updateComment(comment);
      _comments = _comments
          .map((c) => c.id == updatedComment.id ? updatedComment : c)
          .toList();
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  Future<void> deleteComment(String commentId) async {
    _error = null;

    try {
      await _repository.deleteComment(commentId);
      _comments = _comments
          .where((comment) => comment.id != commentId)
          .toList();
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }
}
