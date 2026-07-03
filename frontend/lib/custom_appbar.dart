import 'package:flutter/material.dart';
import 'app_bar_background.dart';

class CustomAppBar extends StatelessWidget
    implements PreferredSizeWidget {

  final String title;
  final List<Widget>? actions;
  final Widget? leading;

  const CustomAppBar({
    super.key,
    required this.title,
    this.actions,
    this.leading
  });

  @override
  Widget build(BuildContext context) {
    return AppBar(

      title: Text(title),

      flexibleSpace: const AppBarBackground(),

      foregroundColor: Colors.white,

      leading: leading?? 
        Builder(
          builder: (context) {
            return IconButton(
              icon: const Icon(Icons.menu),
              onPressed: () {
                Scaffold.of(context).openDrawer();
              },
            );
          },
        ),

      actions: actions,
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}