import 'package:flutter/foundation.dart';
import '../models/post.dart';
import '../repositories/post_repository.dart';

class PostListViewModel extends ChangeNotifier {
  final PostRepository _postRepository;
  final bool isMe;

  PostListViewModel({
    required PostRepository postRepository,
    this.isMe = false,
  }) : _postRepository = postRepository;

  List<Post> _posts = [];
  List<Post> get posts => _posts;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  Future<void> loadPosts() async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      if (isMe) {
        _posts = await _postRepository.getAllPostsByOwner();
      } else {
        _posts = await _postRepository.getAllPostsOfFollowings();
      }
      
      // Sort by date (descending) if possible
      _posts.sort((a, b) {
        final dateA = a.lastModified ?? a.createdAt ?? DateTime(0);
        final dateB = b.lastModified ?? b.createdAt ?? DateTime(0);
        return dateB.compareTo(dateA);
      });
      
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> deletePost(String postId) async {
    try {
      await _postRepository.deletePost(postId);
      _posts.removeWhere((p) => p.id == postId);
      notifyListeners();
    } catch (e) {
      _errorMessage = e.toString();
      notifyListeners();
    }
  }
}
