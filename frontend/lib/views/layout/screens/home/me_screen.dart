import 'package:flutter/material.dart';
import '../../../../views/features/profile/profile_screen.dart';

class MeScreen extends StatelessWidget {
  const MeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const ProfileScreen(isMePage: true);
  }
}
