import 'package:flutter/material.dart';

class AppBarBackground extends StatelessWidget {
  const AppBarBackground({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: const BoxDecoration(
        gradient: LinearGradient(
          colors: [
            Color(0xFF5B21B6),
            Color(0xFFA855F7),
          ],
        ),
      ),
    );
  }
}