import 'dart:convert';
import 'package:flutter/material.dart';

import '../../../components/widgets/custom_appbar.dart';
import '../../../components/widgets/custom_drawer.dart';
import '../../../../services/socket_service.dart';
import '../../../../services/session_manager.dart';
import '../../../layout/screens/auth/log_in_screen.dart';

typedef UpdateResult = ({bool success, String message});

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  final _usernameController = TextEditingController();
  final _currentPasswordController = TextEditingController();
  final _newPasswordController = TextEditingController();
  final _confirmPasswordController = TextEditingController(); // 👈 کنترلر جدید برای تکرار رمز عبور

  bool _isUpdatingUsername = false;
  bool _isUpdatingPassword = false;

  @override
  void dispose() {
    _usernameController.dispose();
    _currentPasswordController.dispose();
    _newPasswordController.dispose();
    _confirmPasswordController.dispose(); // 👈 آزاد کردن کنترلر جدید
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

  Future<void> _handleUpdateUsername() async {
    final newUsername = _usernameController.text.trim();
    if (newUsername.isEmpty) {
      _showSnackBar('Username cannot be empty', isError: true);
      return;
    }

    setState(() => _isUpdatingUsername = true);

    try {
      final requestMap = {
        "action": "User/changeUserName",
        "sessionId": SessionManager.instance.sessionId,
        "newUserName": newUsername,
      };

      String jsonRequest = jsonEncode(requestMap) + "\n";
      String rawResponse = await SocketService.sendRequest(jsonRequest);
      Map<String, dynamic> responseMap = jsonDecode(rawResponse);

      if (responseMap['statusCode'] == 200 || responseMap['status'] == 'SUCCESS') {
        _showSnackBar('Username updated successfully');
        _usernameController.clear();
      } else {
        _showSnackBar(responseMap['message'] ?? 'Failed to update username', isError: true);
      }
    } catch (e) {
      _showSnackBar('Connection error: $e', isError: true);
    } finally {
      if (mounted) setState(() => _isUpdatingUsername = false);
    }
  }

  // 👈 متد اصلاح‌شده تغییر رمز عبور به همراه تکرار آن
  Future<void> _handleUpdatePassword() async {
    final currentPassword = _currentPasswordController.text;
    final newPassword = _newPasswordController.text;
    final confirmPassword = _confirmPasswordController.text;

    // ۱. بررسی خالی نبودن فیلدها
    if (currentPassword.isEmpty || newPassword.isEmpty || confirmPassword.isEmpty) {
      _showSnackBar('Please fill in all password fields', isError: true);
      return;
    }

    // ۲. اعتبارسنجی یکسان بودن رمز جدید و تکرار آن
    if (newPassword != confirmPassword) {
      _showSnackBar('New password and confirmation do not match', isError: true);
      return;
    }

    setState(() => _isUpdatingPassword = true);

    try {
      final requestMap = {
        "action": "User/changePassword",
        "sessionId": SessionManager.instance.sessionId,
        "oldPassword": currentPassword,
        "newPassword": newPassword,
        "confirmNewPassword": confirmPassword, // 👈 ارسال تکرار رمز عبور به بک‌اند
      };

      String jsonRequest = jsonEncode(requestMap) + "\n";
      String rawResponse = await SocketService.sendRequest(jsonRequest);
      Map<String, dynamic> responseMap = jsonDecode(rawResponse);

      if (responseMap['statusCode'] == 200 || responseMap['status'] == 'SUCCESS') {
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
      if (mounted) setState(() => _isUpdatingPassword = false);
    }
  }

  Future<void> _handleLogout() async {
    try {
      final requestMap = {
        "action": "User/logOut",
        "sessionId": SessionManager.instance.sessionId,
      };
      String jsonRequest = jsonEncode(requestMap) + "\n";
      await SocketService.sendRequest(jsonRequest);
    } catch (_) {}

    SessionManager.instance.clearSession();

    if (!mounted) return;

    Navigator.pushAndRemoveUntil(
      context,
      MaterialPageRoute(builder: (context) => const LogInPage()),
      (route) => false,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      drawer: const CustomDrawer(),
      appBar: const CustomAppBar(
        title: "Settings",
      ),
      body: ListView(
        padding: const EdgeInsets.all(16.0),
        children: [
          const Text(
            'Change Username',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
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

          const Text(
            'Change Password',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
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
          // 👈 فیلد جدید برای تکرار رمز عبور جدید
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

          const Text(
            'Account Actions',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.red),
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
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
            ),
          ),
        ],
      ),
    );
  }
}