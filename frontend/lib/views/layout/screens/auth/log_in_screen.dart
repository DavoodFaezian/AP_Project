import 'dart:convert';
import 'package:flutter/material.dart';

import '../../../../services/session_manager.dart';
import '../../../../services/socket_service.dart';
import '../../../components/widgets/auth_header.dart';
import '../../../components/widgets/input_decoration.dart';
import '../../../../repositories/user_repository.dart';
import '../../navigation/navigator_screen.dart';

class LogInPage extends StatefulWidget {
  const LogInPage({super.key});

  @override
  State<LogInPage> createState() => _LogInPageState();
}

class _LogInPageState extends State<LogInPage> {
  final _formKey = GlobalKey<FormState>();
  final _userNameController = TextEditingController();
  final _passwordController = TextEditingController();

  // نمونه‌سازی مستقیم از ریپازیتوری بدون نیاز به provider
  final UserRepository _userRepository = UserRepository();

  bool _showPassword = false;
  bool _isLoading = false;

  String? validateUserName(String? value) {
    if (value == null || value.trim().isEmpty) {
      return "Username is required";
    }
    return null;
  }

  String? validatePassword(String? value) {
    if (value == null || value.isEmpty) {
      return "Password is required";
    }
    return null;
  }

  /// ورود معمولی با نام کاربری و رمز عبور
  Future<void> _logIn() async {
    if (_formKey.currentState!.validate()) {
      setState(() {
        _isLoading = true;
      });

      try {
        final requestMap = {
          "action": "User/logIn",
          "userName": _userNameController.text.trim(),
          "password": _passwordController.text,
        };

        String jsonRequest = jsonEncode(requestMap) + "\n";
        String rawResponse = await SocketService.sendRequest(jsonRequest);
        Map<String, dynamic> responseMap = jsonDecode(rawResponse);

        if (mounted) {
          if (responseMap['statusCode'] == 200 || responseMap['status'] == 'SUCCESS') {
            String sessionId = responseMap['sessionId'];
            SessionManager.instance.saveSessionId(sessionId);

            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text("Welcome back!")),
            );

            Navigator.pushReplacement(
              context,
              MaterialPageRoute(
                builder: (context) => const NavigatorPage(),
              ),
            );
          } else {
            String errorMessage = responseMap['message'] ?? "Log in failed!";
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(errorMessage),
                backgroundColor: Colors.red,
              ),
            );
          }
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text("Could not connect to server: $e"),
              backgroundColor: Colors.red,
            ),
          );
        }
      } finally {
        if (mounted) {
          setState(() {
            _isLoading = false;
          });
        }
      }
    }
  }

  /// ورود با اثر انگشت (بدون نیاز به Provider)
  Future<void> _logInWithBiometrics() async {
    setState(() {
      _isLoading = true;
    });

    try {
      final isAuthenticated = await _userRepository.authenticateWithBiometrics();

      if (isAuthenticated) {
        final savedSessionId = SessionManager.instance.sessionId;

        if (savedSessionId != null && savedSessionId.isNotEmpty) {
          if (mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text("Welcome back!")),
            );
            Navigator.pushReplacement(
              context,
              MaterialPageRoute(
                builder: (context) => const NavigatorPage(),
              ),
            );
          }
        } else {
          if (mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text("No active session found. Please log in with password first."),
                backgroundColor: Colors.orange,
              ),
            );
          }
        }
      } else {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text("Biometric authentication failed."),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text("Error: $e"),
            backgroundColor: Colors.red,
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  @override
  void dispose() {
    _userNameController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          const AuthHeader(
            title: "Log In...",
          ),
          Padding(
            padding: const EdgeInsets.all(16),
            child: Form(
              key: _formKey,
              child: SingleChildScrollView(
                child: Column(
                  children: [
                    const SizedBox(height: 100),
                    Image.asset(
                      'assets/images/Photos-pana (1).png',
                      width: 300,
                      height: 300,
                    ),
                    TextFormField(
                      controller: _userNameController,
                      decoration: buildInputDecoration("Username"),
                      validator: validateUserName,
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      controller: _passwordController,
                      obscureText: !_showPassword,
                      decoration: buildInputDecoration(
                        "Password",
                        suffixIcon: IconButton(
                          icon: Icon(
                            _showPassword
                                ? Icons.visibility
                                : Icons.visibility_off,
                          ),
                          onPressed: () {
                            setState(() {
                              _showPassword = !_showPassword;
                            });
                          },
                        ),
                      ),
                      validator: validatePassword,
                    ),
                    const SizedBox(height: 24),
                    
                    // دکمه Log In و آیکون اثر انگشت
                    Row(
                      children: [
                        Expanded(
                          child: SizedBox(
                            height: 60,
                            child: ElevatedButton(
                              onPressed: _isLoading ? null : _logIn,
                              style: ElevatedButton.styleFrom(
                                backgroundColor: const Color(0xFF1257FA),
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(12),
                                ),
                              ),
                              child: _isLoading
                                  ? const CircularProgressIndicator(
                                      color: Colors.white,
                                    )
                                  : const Text(
                                      "Log In",
                                      style: TextStyle(
                                        color: Colors.white,
                                        fontSize: 18,
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ),
                            ),
                          ),
                        ),
                        const SizedBox(width: 12),
                        
                        InkWell(
                          onTap: _isLoading ? null : _logInWithBiometrics,
                          borderRadius: BorderRadius.circular(12),
                          child: Container(
                            width: 60,
                            height: 60,
                            decoration: BoxDecoration(
                              color: const Color(0xFF1257FA).withOpacity(0.1),
                              borderRadius: BorderRadius.circular(12),
                              border: Border.all(
                                color: const Color(0xFF1257FA),
                                width: 1.5,
                              ),
                            ),
                            child: const Icon(
                              Icons.fingerprint,
                              color: Color(0xFF1257FA),
                              size: 32,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}