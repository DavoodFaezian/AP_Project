import 'package:flutter/material.dart';
import 'package:test_app/models/comment.dart';
import 'package:test_app/repositories/comment_repository.dart';
import 'package:test_app/services/session_manager.dart';
import 'package:test_app/viewmodels/comment_view_model.dart';
import 'package:test_app/views/components/widgets/socket_image.dart';

class CommentsScreen extends StatefulWidget {
  final String postId;
  final String postOwnerId;

  const CommentsScreen({
    super.key,
    required this.postId,
    required this.postOwnerId,
  });

  @override
  State<CommentsScreen> createState() => _CommentsScreenState();
}

class _CommentsScreenState extends State<CommentsScreen> {
  late final CommentViewModel _viewModel;
  final TextEditingController _commentController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _viewModel = CommentViewModel(
      commentRepository: CommentRepository(),
      postId: widget.postId,
      postOwnerId: widget.postOwnerId,
    );
    _viewModel.loadComments();
  }

  @override
  void dispose() {
    _commentController.dispose();
    _viewModel.dispose();
    super.dispose();
  }

  void _showEditDialog(Comment comment) {
    final controller = TextEditingController(text: comment.script);
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Edit Comment"),
        content: TextField(
          controller: controller,
          decoration: const InputDecoration(hintText: "Enter comment"),
          maxLines: 3,
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text("Cancel")),
          TextButton(
            onPressed: () async {
              if (await _viewModel.editComment(comment.id, controller.text)) {
                if (mounted) Navigator.pop(context);
              }
            },
            child: const Text("Save"),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Comments"),
      ),
      body: Column(
        children: [
          Expanded(
            child: AnimatedBuilder(
              animation: _viewModel,
              builder: (context, _) {
                if (_viewModel.isLoading) {
                  return const Center(child: CircularProgressIndicator());
                }

                if (_viewModel.comments.isEmpty) {
                  return const Center(child: Text("No comments yet. Be the first to comment!"));
                }

                return ListView.builder(
                  itemCount: _viewModel.comments.length,
                  itemBuilder: (context, index) {
                    final comment = _viewModel.comments[index];
                    final user = _viewModel.userCache[comment.ownerId];
                    final isMyComment = comment.ownerId == SessionManager.instance.userId;

                    return ListTile(
                      leading: CircleAvatar(
                        radius: 16,
                        backgroundColor: Colors.grey.shade300,
                        child: user?.profilePhotoName != null
                            ? ClipOval(
                          child: SizedBox.expand(
                            child: SocketImage(
                              photoName: user!.profilePhotoName!,
                              sessionId: SessionManager.instance.sessionId!,
                              builder: (context, provider) => Image(
                                image: provider,
                                fit: BoxFit.cover,
                              ),
                            ),
                          ),
                        )
                            : const Icon(Icons.person),
                      ),
                      title: Text(
                        user?.userName ?? "Loading...",
                        style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
                      ),
                      subtitle: Text(comment.script),
                      trailing: isMyComment
                          ? Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                IconButton(
                                  icon: const Icon(Icons.edit, size: 18),
                                  onPressed: () => _showEditDialog(comment),
                                ),
                                IconButton(
                                  icon: const Icon(Icons.delete, size: 18, color: Colors.red),
                                  onPressed: () => _viewModel.deleteComment(comment.id),
                                ),
                              ],
                            )
                          : null,
                    );
                  },
                );
              },
            ),
          ),
          const Divider(height: 1),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _commentController,
                    decoration: const InputDecoration(
                      hintText: "Add a comment...",
                      border: OutlineInputBorder(),
                      contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                    ),
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.send, color: Colors.blue),
                  onPressed: () async {
                    if (await _viewModel.addComment(_commentController.text)) {
                      _commentController.clear();
                    }
                  },
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
