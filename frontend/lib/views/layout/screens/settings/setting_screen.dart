import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';

import '../../../../services/session_manager.dart';
import '../../../../services/socket_service.dart';
import '../../../components/widgets/photo_upload_input.dart';
import '../../../components/widgets/socket_image.dart';
import '../../../layout/screens/auth/log_in_screen.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  final _usernameController = TextEditingController();
  final _currentPasswordController = TextEditingController();
  final _newPasswordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();


  // Loading States
  bool _isLoadingUserData = true;
  bool _isUpdatingUsername = false;
  bool _isUpdatingPassword = false;
  bool _isUpdatingTheme = false;

  // Selected & Fetched Values
  String _selectedTheme = 'LIGHT';
  String? _uploadedPhotoName;

  @override
  void initState() {
    super.initState();
    _fetchUserData();
  }

  @override
  void dispose() {
    _usernameController.dispose();
    _currentPasswordController.dispose();
    _newPasswordController.dispose();
    _confirmPasswordController.dispose();
    super.dispose();
  }

  void _showSnackBar(String message, {bool isError = false}) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red : null,
      ),
    );
  }

  // -------------------- FETCH USER DATA --------------------
  Future<void> _fetchUserData() async {
    setState(() {
      _isLoadingUserData = true;
    });

    try {
      final requestMap = {
        "actionName": "User/getUser",
        "payload": {
          "sessionId": SessionManager.instance.sessionId,
        }
      };

      String jsonRequest = jsonEncode(requestMap) + "\n";
      String rawResponse = await SocketService.sendRequest(jsonRequest);
      Map<String, dynamic> responseMap = jsonDecode(rawResponse);

      if (responseMap['status'] == "200") {
        final payload = responseMap['payload'];
        final userName = payload['userName'] ?? '';
        final photoName = payload['photoName'];

        setState(() {
          _usernameController.text = userName;
          _uploadedPhotoName = photoName;
          _isLoadingUserData = false;
        });
      } else {
        _showSnackBar(responseMap['message'] ?? 'Failed to load user info', isError: true);
        setState(() => _isLoadingUserData = false);
      }
    } catch (e) {
      _showSnackBar('Error loading settings details: $e', isError: true);
      setState(() => _isLoadingUserData = false);
    }
  }



  // -------------------- THEME --------------------
  Future<void> _handleUpdateTheme() async {
    setState(() {
      _isUpdatingTheme = true;
    });

    try {
      final requestMap = {
        "actionName": "User/changeTheme",
        "payload": {
          "sessionId": SessionManager.instance.sessionId,
          "theme": _selectedTheme, // "DARK" or "LIGHT"
        }
      };

      final jsonRequest = jsonEncode(requestMap) + "\n";
      final rawResponse = await SocketService.sendRequest(jsonRequest);
      final responseMap = jsonDecode(rawResponse) as Map<String, dynamic>;

      if (responseMap['status'] == '200') {
        _showSnackBar('Theme updated successfully');
      } else {
        _showSnackBar(
          responseMap['message'] ?? 'Failed to update theme',
          isError: true,
        );
      }
    } catch (e) {
      _showSnackBar('Connection error: $e', isError: true);
    } finally {
      if (mounted) {
        setState(() {
          _isUpdatingTheme = false;
        });
      }
    }
  }


  // -------------------- USERNAME --------------------
  Future<void> _handleUpdateUsername() async {
    final newUsername = _usernameController.text.trim();

    if (newUsername.isEmpty) {
      _showSnackBar('Username cannot be empty', isError: true);
      return;
    }

    setState(() {
      _isUpdatingUsername = true;
    });

    try {
      final requestMap = {
        "actionName": "User/changeUserName",
        "payload": {
          "sessionId": SessionManager.instance.sessionId,
          "newUserName": newUsername,
        }
      };

      final jsonRequest = jsonEncode(requestMap) + "\n";
      final rawResponse = await SocketService.sendRequest(jsonRequest);
      final responseMap = jsonDecode(rawResponse) as Map<String, dynamic>;

      if (responseMap['status'] == "200") {
        _showSnackBar('Username updated successfully');
      } else {
        _showSnackBar(responseMap['message'] ?? 'Failed to update username', isError: true);
      }
    } catch (e) {
      _showSnackBar('Connection error: $e', isError: true);
    } finally {
      if (mounted) {
        setState(() {
          _isUpdatingUsername = false;
        });
      }
    }
  }

  // -------------------- PASSWORD --------------------
  Future<void> _handleUpdatePassword() async {
    final currentPassword = _currentPasswordController.text;
    final newPassword = _newPasswordController.text;
    final confirmPassword = _confirmPasswordController.text;

    if (currentPassword.isEmpty || newPassword.isEmpty || confirmPassword.isEmpty) {
      _showSnackBar('Please fill in all password fields', isError: true);
      return;
    }

    if (newPassword != confirmPassword) {
      _showSnackBar('New password and confirmation do not match', isError: true);
      return;
    }

    setState(() {
      _isUpdatingPassword = true;
    });

    try {
      final requestMap = {
        "actionName": "User/changePassword",
        "payload": {
          "sessionId": SessionManager.instance.sessionId,
          "oldPassword": currentPassword,
          "newPassword": newPassword,
          "confirmNewPassword": confirmPassword,
        }
      };

      final jsonRequest = jsonEncode(requestMap) + "\n";
      final rawResponse = await SocketService.sendRequest(jsonRequest);
      final responseMap = jsonDecode(rawResponse) as Map<String, dynamic>;

      if (responseMap['status'] == "200") {
        _showSnackBar('Password updated successfully');
        _currentPasswordController.clear();
        _newPasswordController.clear();
        _confirmPasswordController.clear();
      } else {
        _showSnackBar(responseMap['message'] ?? 'Failed to update password', isError: true);
      }
    } catch (e) {
      _showSnackBar('Connection error: $e', isError: true);
    } finally {
      if (mounted) {
        setState(() {
          _isUpdatingPassword = false;
        });
      }
    }
  }

  // -------------------- LOGOUT --------------------
  Future<void> _handleLogout() async {
    try {
      final requestMap = {
        "actionName": "User/logOut",
        "payload": {
          "sessionId": SessionManager.instance.sessionId,
        }
      };

      final jsonRequest = jsonEncode(requestMap) + "\n";
      await SocketService.sendRequest(jsonRequest);
    } catch (_) {}

    SessionManager.instance.clearSession();

    if (!mounted) return;

    Navigator.pushAndRemoveUntil(
      context,
      MaterialPageRoute(builder: (_) => const LogInPage()),
          (route) => false,
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoadingUserData) {
      return const Scaffold(
        body: Center(
          child: CircularProgressIndicator(),
        ),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Settings'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          tooltip: 'Back',
          onPressed: () {
            Navigator.of(context).pop();
          },
        ),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // -------------------- PROFILE PHOTO UI --------------------
          

          Center(
            child: PhotoUploadInput(
              title: 'Profile Photo',
              buttonText: 'Upload Profile Photo',
              initialPhotoName: _uploadedPhotoName,
              isProfilePicture: true,
              ownerId: null,
              onPhotoUploaded: (photoName) {
                setState(() {
                  _uploadedPhotoName = photoName;
                });

                // `photoName` is now available here.
                // You can save it, send it in another request,
                // or give it to another component.
              },
            ),

          ),
          const SizedBox(height: 12),

          const Divider(height: 48),

          // -------------------- THEME UI --------------------
          const Text(
            'Appearance',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),

          DropdownButtonFormField<String>(
            value: _selectedTheme,
            decoration: const InputDecoration(
              labelText: 'Theme',
              border: OutlineInputBorder(),
            ),
            items: const [
              DropdownMenuItem(
                value: 'LIGHT',
                child: Text('Light Theme'),
              ),
              DropdownMenuItem(
                value: 'DARK',
                child: Text('Dark Theme'),
              ),
            ],
            onChanged: _isUpdatingTheme
                ? null
                : (value) {
              if (value == null) return;

              setState(() {
                _selectedTheme = value;
              });
            },
          ),

          const SizedBox(height: 12),

          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: _isUpdatingTheme ? null : _handleUpdateTheme,
              child: _isUpdatingTheme
                  ? const SizedBox(
                width: 20,
                height: 20,
                child: CircularProgressIndicator(strokeWidth: 2),
              )
                  : const Text('Save Theme'),
            ),
          ),

          const Divider(height: 48),

          // -------------------- USERNAME UI --------------------
          const Text(
            'Change Username',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),

          TextField(
            controller: _usernameController,
            decoration: const InputDecoration(
              labelText: 'New Username',
              border: OutlineInputBorder(),
            ),
          ),
          const SizedBox(height: 12),

          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: _isUpdatingUsername ? null : _handleUpdateUsername,
              child: _isUpdatingUsername
                  ? const SizedBox(
                width: 20,
                height: 20,
                child: CircularProgressIndicator(strokeWidth: 2),
              )
                  : const Text('Save Username'),
            ),
          ),

          const Divider(height: 48),

          // -------------------- PASSWORD UI --------------------
          const Text(
            'Change Password',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),

          TextField(
            controller: _currentPasswordController,
            obscureText: true,
            decoration: const InputDecoration(
              labelText: 'Current Password',
              border: OutlineInputBorder(),
            ),
          ),
          const SizedBox(height: 12),

          TextField(
            controller: _newPasswordController,
            obscureText: true,
            decoration: const InputDecoration(
              labelText: 'New Password',
              border: OutlineInputBorder(),
            ),
          ),
          const SizedBox(height: 12),

          TextField(
            controller: _confirmPasswordController,
            obscureText: true,
            decoration: const InputDecoration(
              labelText: 'Confirm New Password',
              border: OutlineInputBorder(),
            ),
          ),
          const SizedBox(height: 12),

          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: _isUpdatingPassword ? null : _handleUpdatePassword,
              child: _isUpdatingPassword
                  ? const SizedBox(
                width: 20,
                height: 20,
                child: CircularProgressIndicator(strokeWidth: 2),
              )
                  : const Text('Save Password'),
            ),
          ),

          const Divider(height: 48),

          // -------------------- LOGOUT UI --------------------
          const Text(
            'Account Actions',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: Colors.red,
            ),
          ),
          const SizedBox(height: 12),

          SizedBox(
            width: double.infinity,
            height: 50,
            child: ElevatedButton.icon(
              onPressed: _handleLogout,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.red,
                foregroundColor: Colors.white,
              ),
              icon: const Icon(Icons.logout),
              label: const Text(
                'Log Out',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),

          const SizedBox(height: 24),
        ],
      ),
    );
  }
}
