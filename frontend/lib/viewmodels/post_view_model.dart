import 'package:flutter/foundation.dart';
import '../models/post.dart';
import '../repositories/post_repository.dart';

class UserPostsViewModel extends ChangeNotifier {
  UserPostsViewModel({
    required PostRepository postRepository,
  }) : _postRepository = postRepository;

  final PostRepository _postRepository;

  final List<Post> _posts = <Post>[];
  bool _isLoading = false;
  String? _errorMessage;

  List<Post> get posts => List.unmodifiable(_posts);
  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;

  /// بارگیری پست‌های کاربر جاری
/// بارگیری پست‌های یک کاربر مشخص
  Future<void> loadUserPosts(String userId) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final fetchedPosts = await _postRepository.getPostsByUserId(userId);
      _posts
        ..clear()
        ..addAll(fetchedPosts);
    } catch (e) {
      _errorMessage = 'Failed to load user posts: $e';
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// بارگیری پست‌های یک کاربر مشخص
  
  /// بارگیری پست‌های تایم‌لاین (Followings)
  Future<void> loadFeedPosts() async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final fetchedPosts = await _postRepository.getAllPostsOfFollowings();
      _posts
        ..clear()
        ..addAll(fetchedPosts);
    } catch (e) {
      _errorMessage = 'Failed to load feed posts: $e';
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// حذف پست
  Future<void> deletePost(String postId) async {
    _errorMessage = null;

    try {
      await _postRepository.deletePost(postId);
      _posts.removeWhere((post) => post.id == postId);
      notifyListeners();
    } catch (e) {
      _errorMessage = 'Failed to delete post: $e';
      notifyListeners();
    }
  }
}