import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:local_auth/local_auth.dart';
import 'package:test_app/views/layout/screens/auth/sign_up_screen.dart';
import '../../../../services/session_manager.dart';
import '../../../components/widgets/auth_header.dart';
import '../../../components/widgets/input_decoration.dart';
import '../../navigation/navigator_screen.dart';
import '../../../../services/socket_service.dart';

class LogInPage extends StatefulWidget {
  const LogInPage({super.key});

  @override
  State<LogInPage> createState() => _LogInPageState();
}

class _LogInPageState extends State<LogInPage> {

  final _formKey = GlobalKey<FormState>();
  final _userNameController = TextEditingController();
  final _passwordController = TextEditingController();
  final LocalAuthentication _localAuth = LocalAuthentication();

  bool _showPassword = false;
  bool _isLoading = false; 

  String? validateUserName(String? value){
    if(value == null || value.isEmpty){
      return "Username is required";
    }
    return null;
  }

  String? validatePassword(String? value){
    if (value == null || value.isEmpty) {
      return "Password is required";
    }
    return null;
  }

  Future<void> _logInWithBiometrics() async {
    setState(() {
      _isLoading = true;
    });

    try {
      final bool canAuthenticateWithBiometrics = await _localAuth.canCheckBiometrics;
      final bool isDeviceSupported = await _localAuth.isDeviceSupported();

      if (!canAuthenticateWithBiometrics || !isDeviceSupported) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text("Biometric authentication is not available on this device."),
              backgroundColor: Colors.orange,
            ),
          );
        }
        return;
      }

      final bool isAuthenticated = await _localAuth.authenticate(
        localizedReason: 'Please put your finger to open the application',
        biometricOnly: true,
        persistAcrossBackgrounding: true,
      );

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

  Future<void> _logIn() async {
    if (_formKey.currentState!.validate()) {
      setState(() {
        _isLoading = true;
      });

      try {
        final requestMap = {
          "actionName": "User/logIn",
          "payload": {
            "userName": _userNameController.text.trim(),
            "password": _passwordController.text,
          }
        };

        String jsonRequest = jsonEncode(requestMap) + "\n";

        String rawResponse = await SocketService.sendRequest(jsonRequest);
        Map<String, dynamic> responseMap = jsonDecode(rawResponse);

        if (mounted) {
          if (responseMap['status'] == "200") {
            String sessionId = responseMap['payload']['id'];
            await SessionManager.instance.saveSessionId(sessionId);

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

  @override
  void dispose() {
    _userNameController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      backgroundColor: Colors.white,
      body: SingleChildScrollView(
        child: Column(
          children: [
            AuthHeader(
              title: "Welcome\nBack",
            ).animate().fadeIn(duration: 600.ms).slideY(begin: -0.2, end: 0),
      
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 20),
              child: Form(
                key: _formKey,
                child: Column(
                  children: [
                    Image.asset(
                      'assets/images/Photos-pana (1).png',
                      width: 200,
                      height: 200,
                    ).animate().fadeIn(delay: 200.ms).scale(),
                
                    const SizedBox(height: 24),
                
                    TextFormField(
                      controller: _userNameController,
                      decoration: buildInputDecoration("Username").copyWith(
                        prefixIcon: const Icon(Icons.person_outline),
                      ),
                      validator: validateUserName,
                    ).animate().fadeIn(delay: 400.ms).slideX(begin: 0.1, end: 0),
                
                    const SizedBox(height: 16),
                
                    TextFormField(
                      controller: _passwordController,
                      obscureText: !_showPassword,
                      decoration: buildInputDecoration("Password").copyWith(
                        prefixIcon: const Icon(Icons.lock_outline),
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
                    ).animate().fadeIn(delay: 500.ms).slideX(begin: 0.1, end: 0),

                    const SizedBox(height: 32),
                
                    Row(
                      children: [
                        Expanded(
                          child: SizedBox(
                            height: 56,
                            child: ElevatedButton(
                              onPressed: _isLoading ? null : _logIn,
                              style: ElevatedButton.styleFrom(
                                backgroundColor: const Color(0xFF5B21B6),
                                foregroundColor: Colors.white,
                                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                              ),
                              child: _isLoading
                                  ? const CircularProgressIndicator(color: Colors.white)
                                  : const Text(
                                      "Log In",
                                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                                    ),
                            ),
                          ),
                        ),
                        const SizedBox(width: 12),
                        Container(
                          height: 56,
                          width: 56,
                          decoration: BoxDecoration(
                            color: const Color(0xFFF3E8FF),
                            borderRadius: BorderRadius.circular(16),
                          ),
                          child: IconButton(
                            onPressed: _isLoading ? null : _logInWithBiometrics,
                            icon: const Icon(Icons.fingerprint, color: Color(0xFF5B21B6), size: 28),
                          ),
                        ),
                      ],
                    ).animate().fadeIn(delay: 700.ms).scale(),
                    
                    const SizedBox(height: 24),

                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Text("Don't have an account? ", style: TextStyle(color: Colors.grey)),
                        GestureDetector(
                          onTap: _isLoading
                              ? null
                              : () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (_) => const SignUpPage(),
                              ),
                            );
                          },
                          child: Text(
                            'Sign Up',
                            style: TextStyle(
                              color: theme.primaryColor,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ],
                    ).animate().fadeIn(delay: 900.ms),

                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
