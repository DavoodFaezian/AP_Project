import 'package:flutter/foundation.dart';

import '../models/comment.dart';
import '../repositories/comment_repository.dart';

class CommentViewModel extends ChangeNotifier {
  CommentViewModel(this._repository);

  final CommentRepository _repository;

  final ValueNotifier<List<Comment>> commentsNotifier =
  ValueNotifier<List<Comment>>(<Comment>[]);

  final ValueNotifier<bool> isLoadingNotifier = ValueNotifier<bool>(false);

  final ValueNotifier<String?> errorNotifier = ValueNotifier<String?>(null);

  Future<void> loadComments() async {
    isLoadingNotifier.value = true;
    errorNotifier.value = null;

    try {
      final comments = await _repository.getAllComments();
      commentsNotifier.value = comments;
    } catch (e) {
      errorNotifier.value = e.toString();
    } finally {
      isLoadingNotifier.value = false;
    }
  }

  Future<void> loadCommentsForPhoto(String photoId) async {
    isLoadingNotifier.value = true;
    errorNotifier.value = null;

    try {
      final comments = await _repository.getCommentsForPhoto(photoId);
      commentsNotifier.value = comments;
    } catch (e) {
      errorNotifier.value = e.toString();
    } finally {
      isLoadingNotifier.value = false;
    }
  }

  Future<void> addComment(Comment comment) async {
    errorNotifier.value = null;

    try {
      final newComment = await _repository.addComment(comment);
      commentsNotifier.value = [
        ...commentsNotifier.value,
        newComment,
      ];
    } catch (e) {
      errorNotifier.value = e.toString();
    }
  }

  Future<void> updateComment(Comment comment) async {
    errorNotifier.value = null;

    try {
      final updatedComment = await _repository.updateComment(comment);

      final updatedList = commentsNotifier.value
          .map((c) => c.id == updatedComment.id ? updatedComment : c)
          .toList();

      commentsNotifier.value = updatedList;
    } catch (e) {
      errorNotifier.value = e.toString();
    }
  }

  Future<void> deleteComment(String commentId) async {
    errorNotifier.value = null;

    try {
      await _repository.deleteComment(commentId);
      commentsNotifier.value = commentsNotifier.value
          .where((comment) => comment.id != commentId)
          .toList();
    } catch (e) {
      errorNotifier.value = e.toString();
    }
  }

  void clearError() {
    errorNotifier.value = null;
  }

  @override
  void dispose() {
    commentsNotifier.dispose();
    isLoadingNotifier.dispose();
    errorNotifier.dispose();
    super.dispose();
  }
}
