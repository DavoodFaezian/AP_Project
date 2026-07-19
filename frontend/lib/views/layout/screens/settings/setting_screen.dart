import 'package:flutter/material.dart';
import '../../../components/widgets/custom_appbar.dart';
import '../../../components/widgets/custom_drawer.dart';


// Using a Dart Record to handle the result of our async operations
typedef UpdateResult = ({bool success, String message});

class SettingsScreen extends StatefulWidget {
  // Utilizing named parameters in the standard const constructor
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  final _usernameController = TextEditingController();
  final _currentPasswordController = TextEditingController();
  final _newPasswordController = TextEditingController();

  @override
  void dispose() {
    _usernameController.dispose();
    _currentPasswordController.dispose();
    _newPasswordController.dispose();
    super.dispose();
  }

  // Async method simulating a network request using named parameters
  Future<UpdateResult> _updateData({
    required String field,
    required String value,
  }) async {
    await Future.delayed(const Duration(seconds: 1)); // Simulate API call
    if (value.isEmpty) {
      return (success: false, message: '$field cannot be empty');
    }
    return (success: true, message: '$field updated successfully');
  }

  void _showSnackBar(String message) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message)),
    );
  }

  // Async handler for updating the username
  Future<void> _handleUpdateUsername() async {
    final result = await _updateData(
      field: 'Username',
      value: _usernameController.text,
    );
    _showSnackBar(result.message);
  }

  // Async handler for updating the password
  Future<void> _handleUpdatePassword() async {
    final result = await _updateData(
      field: 'Password',
      value: _newPasswordController.text,
    );
    _showSnackBar(result.message);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      drawer: CustomDrawer(),
        appBar: CustomAppBar(
          title: "Settings",

        ),
      body: ListView(
        padding: const EdgeInsets.all(16.0),
        children: [
          const Text(
            'Change Username',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          TextField(
            controller: _usernameController,
            decoration: const InputDecoration(labelText: 'New Username'),
          ),
          const SizedBox(height: 8),
          ElevatedButton(
            // Passing the async method as a tear-off
            onPressed: _handleUpdateUsername,
            child: const Text('Save Username'),
          ),

          const Divider(height: 48),

          const Text(
            'Change Password',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          TextField(
            controller: _currentPasswordController,
            obscureText: true,
            decoration: const InputDecoration(labelText: 'Current Password'),
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _newPasswordController,
            obscureText: true,
            decoration: const InputDecoration(labelText: 'New Password'),
          ),
          const SizedBox(height: 8),
          ElevatedButton(
            // Passing the async method as a tear-off
            onPressed: _handleUpdatePassword,
            child: const Text('Save Password'),
          ),
        ],
      ),

    );
  }
}
