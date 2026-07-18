import 'package:flutter/material.dart';

class SharePage extends StatelessWidget {
  const SharePage({
    super.key,
    required this.photoId,
    required this.albumTitle,
  });

  final String photoId;
  final String albumTitle;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Share Photo')),
      body: Center(
        child: FilledButton(
          onPressed: () {
            Navigator.of(context).pop();
          },
          child: Text('Back To $albumTitle'),
        ),
      ),
    );
  }
}
