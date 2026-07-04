import 'package:flutter/material.dart';

class CustomFAB extends StatelessWidget {

  final VoidCallback onPressed;
  final IconData icon;

  const CustomFAB({
    super.key,
    required this.onPressed,
    this.icon = Icons.add,
  });

  @override
  Widget build(BuildContext context) {
    return FloatingActionButton(
      onPressed: onPressed,
      shape: const CircleBorder(),
      backgroundColor: const Color(0xFF1257FA),
      child: Icon(
        icon,
        color: Colors.white,
      ),
    );
  }
}