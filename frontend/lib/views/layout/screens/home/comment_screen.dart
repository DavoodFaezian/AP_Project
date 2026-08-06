import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
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
      backgroundColor: Colors.white,
      appBar: AppBar(
        title: const Text("Comments", style: TextStyle(fontWeight: FontWeight.bold)),
        elevation: 0,
        backgroundColor: Colors.white,
        foregroundColor: Colors.black,
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
                  return Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.chat_bubble_outline, size: 80, color: Colors.grey.shade300),
                        const SizedBox(height: 16),
                        Text("No comments yet", style: TextStyle(color: Colors.grey.shade500)),
                        Text("Be the first to share your thoughts!", style: TextStyle(color: Colors.grey.shade400, fontSize: 12)),
                      ],
                    ),
                  ).animate().fadeIn();
                }

                return ListView.separated(
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  itemCount: _viewModel.comments.length,
                  separatorBuilder: (context, index) => const SizedBox(height: 16),
                  itemBuilder: (context, index) {
                    final comment = _viewModel.comments[index];
                    final user = _viewModel.userCache[comment.ownerId];
                    final isMyComment = comment.ownerId == SessionManager.instance.userId;

                    return Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          CircleAvatar(
                            radius: 18,
                            backgroundColor: const Color(0xFFF3E8FF),
                            child: user?.profilePhotoName != null
                                ? ClipOval(
                              child: SizedBox.expand(
                                child: SocketImage(
                                  photoName: user!.profilePhotoName!,
                                  sessionId: SessionManager.instance.sessionId!,
                                  ownerId: user!.userId,
                                  builder: (context, provider) => Image(
                                    image: provider,
                                    fit: BoxFit.cover,
                                  ),
                                ),
                              ),
                            )
                                : const Icon(Icons.person, size: 20, color: Color(0xFF5B21B6)),
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Row(
                                  children: [
                                    Text(
                                      user?.userName ?? "Loading...",
                                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
                                    ),
                                    const Spacer(),
                                    if (isMyComment) ...[
                                      GestureDetector(
                                        onTap: () => _showEditDialog(comment),
                                        child: Icon(Icons.edit, size: 14, color: Colors.grey.shade400),
                                      ),
                                      const SizedBox(width: 12),
                                      GestureDetector(
                                        onTap: () => _viewModel.deleteComment(comment.id),
                                        child: Icon(Icons.delete_outline, size: 14, color: Colors.red.shade300),
                                      ),
                                    ],
                                  ],
                                ),
                                const SizedBox(height: 4),
                                Text(
                                  comment.script,
                                  style: const TextStyle(fontSize: 14, color: Colors.black87),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ).animate().fadeIn(delay: (index * 50).ms).slideX(begin: 0.05, end: 0);
                  },
                );
              },
            ),
          ),
          Container(
            padding: EdgeInsets.fromLTRB(16, 8, 16, 16 + MediaQuery.of(context).viewInsets.bottom),
            decoration: BoxDecoration(
              color: Colors.white,
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.05),
                  blurRadius: 10,
                  offset: const Offset(0, -5),
                ),
              ],
            ),
            child: Row(
              children: [
                Expanded(
                  child: Container(
                    decoration: BoxDecoration(
                      color: Colors.grey.shade100,
                      borderRadius: BorderRadius.circular(24),
                    ),
                    child: TextField(
                      controller: _commentController,
                      decoration: const InputDecoration(
                        hintText: "Add a comment...",
                        border: InputBorder.none,
                        enabledBorder: InputBorder.none,
                        focusedBorder: InputBorder.none,
                        contentPadding: EdgeInsets.symmetric(horizontal: 20, vertical: 10),
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                GestureDetector(
                  onTap: () async {
                    if (await _viewModel.addComment(_commentController.text)) {
                      _commentController.clear();
                    }
                  },
                  child: Container(
                    padding: const EdgeInsets.all(10),
                    decoration: const BoxDecoration(
                      color: Color(0xFF5B21B6),
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(Icons.send, color: Colors.white, size: 20),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
