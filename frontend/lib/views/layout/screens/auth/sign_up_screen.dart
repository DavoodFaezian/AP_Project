import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../../../../repositories/user_repository.dart';
import '../../../../services/socket_service.dart';
import '../../../components/widgets/auth_header.dart';
import '../../../components/widgets/input_decoration.dart';
import '../../navigation/navigator_screen.dart';
import '../../../../services/session_manager.dart';
import 'log_in_screen.dart';


class SignUpPage extends StatefulWidget {
  const SignUpPage({super.key});

  @override
  State<SignUpPage> createState() => _SignUpPageState();
}

class _SignUpPageState extends State<SignUpPage> {

  final _formKey = GlobalKey<FormState>();
  final _userNameController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();
  final specialCharRegex = RegExp(r'[!@#$%^&*()]');

  bool _showPassword = false;
  bool _showConfirmPassword = false;
  
  bool _isLoading = false; 

  @override
  void initState() {
    super.initState();
    _checkAutoLogin();
  }

  Future<void> _checkAutoLogin() async {
    if (SessionManager.instance.isLoggedIn) {
      try {
        await UserRepository().loginBySessionId(SessionManager.instance.sessionId!);
        if (mounted) {
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (context) => const NavigatorPage()),
          );
        }
      } catch (e) {
        debugPrint("Auto login failed: $e");
        await SessionManager.instance.clearSession();
      }
    }
  }

  String? validateUserName(String? value){
    if(value == null || value.isEmpty){
      return "Username is required";
    }
    return null;
  }

  String? validatePassword(String? value){
    if (value == null || value.length < 8) {
      return "Password must be at least 8 characters";
    }

    if(value.contains(_userNameController.text) && _userNameController.text.isNotEmpty){
      return "Password must not contain username";
    }

    if (!specialCharRegex.hasMatch(value)) {
      return 'Password must contain a special character';
    }
                
    return null;
  }

  String? validateConfirmPassword(String? value){
    if (value != _passwordController.text || value == null || value.isEmpty) {
      return "Passwords do not match";
    }            
    return null;
  }

  Future<void> _signUp() async {
    if (_formKey.currentState!.validate()) {
      setState(() {
        _isLoading = true;
      });

      try {
        final requestMap = {
          "actionName": "User/signUp",
          "payload": {
            "userName": _userNameController.text.trim(),
            "repeatedPassword": _confirmPasswordController.text.trim(),
            "password": _passwordController.text,
          }
        };

        String jsonRequest = jsonEncode(requestMap) + "\n";

        String rawResponse = await SocketService.sendRequest(jsonRequest);
        Map<String, dynamic> responseMap = jsonDecode(rawResponse);

        if (mounted) {
          if (responseMap['status'] == '200') {
            String sessionId = responseMap["payload"]["id"];

            await SessionManager.instance.saveSessionId(sessionId);

            if (mounted) {
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text("Account created successfully!")),
              );

              Navigator.pushReplacement(
                context,
                MaterialPageRoute(
                  builder: (context) => const NavigatorPage(),
                ),
              );
            }
          } else {
            String errorMessage = responseMap['message'] ?? "Sign up failed!";
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(errorMessage), backgroundColor: Colors.red),
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
    _confirmPasswordController.dispose();
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
            Stack(
              children: [
                AuthHeader(
                  title: "Create\nAccount",
                ).animate().fadeIn(duration: 600.ms).slideY(begin: -0.2, end: 0),
              ],
            ),
      
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
                
                    const SizedBox(height: 16),
                
                    TextFormField(
                      controller: _confirmPasswordController,
                      obscureText: !_showConfirmPassword,
                      decoration: buildInputDecoration("Confirm Password").copyWith(
                        prefixIcon: const Icon(Icons.lock_reset),
                        suffixIcon: IconButton(
                          icon: Icon(
                            _showConfirmPassword
                                ? Icons.visibility
                                : Icons.visibility_off,
                          ),
                          onPressed: () {
                            setState(() {
                              _showConfirmPassword = !_showConfirmPassword;
                            });
                          },
                        ),
                      ),
                      validator: validateConfirmPassword,
                    ).animate().fadeIn(delay: 600.ms).slideX(begin: 0.1, end: 0),

                    const SizedBox(height: 32),
                
                    SizedBox(
                      width: double.infinity,
                      height: 56,
                      child: ElevatedButton(
                        onPressed: _isLoading ? null : _signUp,
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF5B21B6),
                          foregroundColor: Colors.white,
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                        ),
                        child: _isLoading
                            ? const CircularProgressIndicator(color: Colors.white)
                            : const Text(
                                "Sign Up",
                                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                              ),
                      ),
                    ).animate().fadeIn(delay: 800.ms).scale(),
                    
                    const SizedBox(height: 24),

                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Text('Already have an account? ', style: TextStyle(color: Colors.grey)),
                        GestureDetector(
                          onTap: _isLoading
                              ? null
                              : () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (_) => const LogInPage(),
                              ),
                            );
                          },
                          child: Text(
                            'Log In',
                            style: TextStyle(
                              color: theme.primaryColor,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ],
                    ).animate().fadeIn(delay: 1000.ms),

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
