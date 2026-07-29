import 'package:flutter/material.dart';
import '../../../../models/comment.dart';
import '../../../../repositories/comment_repository.dart';
import '../../../../viewmodels/comment_view_model.dart';

class CommentsScreen extends StatefulWidget {
  final String photoId;

  const CommentsScreen({
    super.key,
    required this.photoId,
  });

  @override
  State<CommentsScreen> createState() => _CommentsScreenState();
}

class _CommentsScreenState extends State<CommentsScreen> {
  late final CommentViewModel viewModel;
  final TextEditingController _commentController = TextEditingController();

  @override
  void initState() {
    super.initState();
    viewModel = CommentViewModel(CommentRepository());
    viewModel.loadCommentsForPhoto(widget.photoId);
  }

  @override
  void dispose() {
    viewModel.dispose();
    _commentController.dispose();
    super.dispose();
  }

  void _submitComment() {
    final text = _commentController.text.trim();
    if (text.isEmpty) return;

    final newComment = Comment(
      ownerId: 'user1', // Replace with actual current user ID
      script: text,
      photoId: widget.photoId,
    );

    viewModel.addComment(newComment);
    _commentController.clear();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Comments')),
      body: AnimatedBuilder(
        animation: viewModel,
        builder: (context, _) {
          if (viewModel.isLoading && viewModel.comments.isEmpty) {
            return const Center(child: CircularProgressIndicator());
          }

          if (viewModel.error != null && viewModel.comments.isEmpty) {
            return Center(child: Text('Error: ${viewModel.error}'));
          }

          return Column(
            children: [
              Expanded(
                child: viewModel.comments.isEmpty
                    ? const Center(child: Text('No comments yet.'))
                    : ListView.builder(
                        itemCount: viewModel.comments.length,
                        itemBuilder: (context, index) {
                          final comment = viewModel.comments[index];
                          return ListTile(
                            leading: CircleAvatar(
                              child: Text(comment.ownerId[0].toUpperCase()),
                            ),
                            title: Text(comment.ownerId),
                            subtitle: Text(comment.script),
                          );
                        },
                      ),
              ),
              const Divider(height: 1),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8.0),
                child: Row(
                  children: [
                    Expanded(
                      child: TextField(
                        controller: _commentController,
                        decoration: const InputDecoration(
                          hintText: 'Add a comment...',
                          border: InputBorder.none,
                        ),
                      ),
                    ),
                    IconButton(
                      icon: const Icon(Icons.send),
                      onPressed: _submitComment,
                    ),
                  ],
                ),
              ),
            ],
          );
        },
      ),
    );
  }
}
