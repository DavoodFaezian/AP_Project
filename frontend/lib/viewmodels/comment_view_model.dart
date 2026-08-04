import 'package:flutter/foundation.dart';
import 'package:test_app/models/user_profile.dart';
import '../models/comment.dart';
import '../models/user.dart';
import '../repositories/comment_repository.dart';
import '../repositories/user_repository.dart';

class CommentViewModel extends ChangeNotifier {
  final CommentRepository _commentRepository;
  final UserRepository _userRepository = UserRepository();
  final String postId;
  final String postOwnerId;

  CommentViewModel({
    required CommentRepository commentRepository,
    required this.postId,
    required this.postOwnerId,
  }) : _commentRepository = commentRepository;

  List<Comment> _comments = [];
  List<Comment> get comments => _comments;

  final Map<String, UserProfile> _userCache = {};
  Map<String, UserProfile> get userCache => _userCache;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  Future<void> loadComments() async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      _comments = await _commentRepository.getAllCommentsByPostId(postId);
      
      // Load user profiles for each comment owner
      for (var comment in _comments) {
        if (!_userCache.containsKey(comment.ownerId)) {
          try {
            final user = await _userRepository.getUserProfileById(comment.ownerId);
            _userCache[comment.ownerId] = user;
          } catch (e) {
            debugPrint("Error loading profile for ${comment.ownerId}: $e");
          }
        }
      }
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<bool> addComment(String script) async {
    if (script.trim().isEmpty) return false;

    try {
      await _commentRepository.addComment(
        script: script,
        postId: postId,
        postOwnerId: postOwnerId,
      );
      await loadComments();
      return true;
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<void> deleteComment(String commentId) async {
    try {
      await _commentRepository.deleteComment(
        commentId: commentId,
        postId: postId,
        postOwnerId: postOwnerId,
      );
      _comments.removeWhere((c) => c.id == commentId);
      notifyListeners();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    }
  }

  Future<bool> editComment(String commentId, String newScript) async {
    try {
      await _commentRepository.editComment(
        commentId: commentId,
        script: newScript,
        postId: postId,
      );
      await loadComments();
      return true;
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
      return false;
    }
  }
}
