import 'package:flutter/material.dart';
import 'package:test_app/services/session_manager.dart';
import 'package:test_app/views/components/widgets/socket_image.dart';

import '../../layout/screens/settings/setting_screen.dart';
import '../dialogs/logout_confirmation_dialog.dart';

class CustomDrawer extends StatelessWidget {
  const CustomDrawer({super.key});

  @override
  Widget build(BuildContext context) {
    return ListenableBuilder(
      listenable: SessionManager.instance,
      builder: (context, _) {
        final user = SessionManager.instance.currentUser;
        final username = user?.username ?? "Guest";
        final profilePhoto = user?.profilePhotoId;

        return Drawer(
          child: ListView(
            padding: EdgeInsets.zero,
            children: [
              Container(
                decoration: const BoxDecoration(
                  gradient: LinearGradient(
                    colors: [
                      Color(0xFF5B21B6),
                      Color(0xA855F7FF),
                    ],
                  ),
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const SizedBox(height: 50),
                    Stack(
                      children: [
                        CircleAvatar(
                          radius: 50,
                          backgroundColor: Colors.white24,
                          child: profilePhoto != null && profilePhoto.isNotEmpty
                              ? ClipOval(
                                  child: SocketImage(
                                    photoName: profilePhoto,
                                    sessionId: SessionManager.instance.sessionId!,
                                    builder: (context, provider) => Image(
                                      image: provider,
                                      fit: BoxFit.cover,
                                      width: 100,
                                      height: 100,
                                    ),
                                  ),
                                )
                              : const Icon(
                                  Icons.person,
                                  size: 60,
                                  color: Colors.white,
                                ),
                        ),
                        Positioned(
                          bottom: -11,
                          right: -11,
                          child: IconButton(
                            onPressed: () {},
                            icon: const Icon(
                              Icons.photo_camera,
                              color: Colors.lightBlue,
                              size: 35,
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 25),
                    Text(
                      username,
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 25),
                  ],
                ),
              ),
              Column(
                children: [
                  ListTile(
                    leading: const Icon(Icons.person),
                    title: const Text("Profile"),
                    onTap: () {},
                  ),
                  ListTile(
                    leading: const Icon(Icons.settings),
                    title: const Text("Settings"),
                    onTap: () {
                      Navigator.pop(context);
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => const SettingsScreen(),
                        ),
                      );
                    },
                  ),
                  ListTile(
                    leading: const Icon(Icons.info),
                    title: const Text("About"),
                    onTap: () {},
                  ),
                  const Divider(
                    thickness: 1,
                    color: Colors.grey,
                  ),
                  ListTile(
                    leading: const Icon(
                      Icons.logout,
                      color: Colors.red,
                    ),
                    title: const Text("Log out"),
                    onTap: () {
                      Navigator.pop(context);
                      showLogoutDialog(context);
                    },
                  ),
                ],
              ),
            ],
          ),
        );
      },
    );
  }
}
