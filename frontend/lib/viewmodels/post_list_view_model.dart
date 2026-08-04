import 'package:flutter/foundation.dart';
import '../models/post.dart';
import '../repositories/post_repository.dart';

class PostListViewModel extends ChangeNotifier {
  final PostRepository _postRepository;
  final bool isMe;
  final String? targetUserId;

  PostListViewModel({
    required PostRepository postRepository,
    this.isMe = false,
    this.targetUserId,
  }) : _postRepository = postRepository;

  List<Post> _posts = [];
  List<Post> get posts => _posts;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  Future<void> loadFeedPosts({bool showLoading = true}) async {
    await loadPosts(showLoading: showLoading);
  }

  Future<void> loadPosts({bool showLoading = true}) async {
    if (showLoading) {
      _isLoading = true;
      _errorMessage = null;
      notifyListeners();
    }

    try {
      List<Post> fetchedPosts;
      if (targetUserId != null) {
        fetchedPosts = await _postRepository.getPostsByUserId(targetUserId!);
      } else if (isMe) {
        fetchedPosts = await _postRepository.getAllPostsByOwner();
      } else {
        fetchedPosts = await _postRepository.getAllPostsOfFollowings();
      }
      
      // Sort by date (descending) if possible
      fetchedPosts.sort((a, b) {
        final dateA = a.lastModified ?? a.createdAt ?? DateTime(0);
        final dateB = b.lastModified ?? b.createdAt ?? DateTime(0);
        return dateB.compareTo(dateA);
      });

      _posts = fetchedPosts;
      
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      if (showLoading) {
        _isLoading = false;
      }
      notifyListeners();
    }
  }

  Future<void> refreshSinglePost(String postId) async {
    try {
      final updatedPost = await _postRepository.getPostById(postId, ""); // ownerId might not be needed if session works
      final index = _posts.indexWhere((p) => p.id == postId);
      if (index != -1) {
        _posts[index] = updatedPost;
        notifyListeners();
      }
    } catch (e) {
      debugPrint("Failed to refresh single post: $e");
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
