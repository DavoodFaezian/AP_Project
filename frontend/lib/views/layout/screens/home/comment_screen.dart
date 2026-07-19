// The secondary screen to display the comments
import 'package:flutter/material.dart';

class CommentsScreen extends StatelessWidget {
  final Set<String> commentIds;

  const CommentsScreen({
    super.key,
    required this.commentIds,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Comments')),
      body: ListView(
        padding: const EdgeInsets.all(16.0),
        // Using a constructor tear-off (Text.new) for clean mapping
        children: commentIds.map(Text.new).toList(),
      ),
    );
  }
}
