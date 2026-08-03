import 'package:flutter/material.dart';

import '../../components/widgets/custom_appbar.dart';
import '../../components/widgets/empty_screen.dart';
import '../../../models/post.dart';
import '../../../repositories/post_repository.dart';
import '../../../viewmodels/post_view_model.dart';

class UserPostsPage extends StatefulWidget {
  const UserPostsPage({
    super.key,
    required this.userId,
    required this.username,
    required this.fullName,
  });

  final String userId;
  final String username;
  final String fullName;

  @override
  State<UserPostsPage> createState() => _UserPostsPageState();
}

class _UserPostsPageState extends State<UserPostsPage> {
  late final UserPostsViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = UserPostsViewModel(postRepository: PostRepository());
    _viewModel.addListener(_onViewModelChange);
    
    // لود کردن پست‌های کاربر هنگام ورود به صفحه
    _viewModel.loadUserPosts(widget.userId);
  }

  void _onViewModelChange() {
    if (mounted) {
      setState(() {});
    }
  }

  @override
  void dispose() {
    _viewModel.removeListener(_onViewModelChange);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(
        title: '@${widget.username}',
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => Navigator.of(context).pop(),
        ),
      ),
      body: Builder(
        builder: (context) {
          if (_viewModel.isLoading) {
            return const Center(child: CircularProgressIndicator());
          }

          if (_viewModel.errorMessage != null) {
            return Center(
              child: Text(
                _viewModel.errorMessage!,
                style: const TextStyle(color: Colors.red),
              ),
            );
          }

          if (_viewModel.posts.isEmpty) {
            return const EmptyState(
              imagePath: 'assets/images/Image post-cuate.png',
              title: 'No Posts Found',
              subtitle: 'This user has not shared any posts yet.',
            );
          }

          return GridView.builder(
            padding: const EdgeInsets.all(12),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 3,
              crossAxisSpacing: 8,
              mainAxisSpacing: 8,
            ),
            itemCount: _viewModel.posts.length,
            itemBuilder: (context, index) {
              final Post post = _viewModel.posts[index];
              return Container(
                decoration: BoxDecoration(
                  color: Colors.purple.shade50,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.purple.shade100),
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(Icons.photo_library, color: Color(0xFF5B21B6)),
                    const SizedBox(height: 4),
                    Text(
                      '${post.photoIds.length} Photos',
                      style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold),
                    ),
                  ],
                ),
              );
            },
          );
        },
      ),
    );
  }
}