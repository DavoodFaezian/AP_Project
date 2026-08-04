import 'dart:convert'; // 👈 برای jsonEncode و jsonDecode
import 'package:flutter/material.dart';
import 'package:local_auth/local_auth.dart'; // 👈 اضافه شدن پکیج اثر انگشت
import 'package:test_app/views/layout/screens/auth/sign_up_screen.dart';
import '../../../../services/session_manager.dart';
import '../../../components/widgets/auth_header.dart';
import '../../../components/widgets/input_decoration.dart';
import '../../navigation/navigator_screen.dart';
import '../../../../services/socket_service.dart'; // 👈 اضافه کردن سرویس سوکت

class LogInPage extends StatefulWidget {
  const LogInPage({super.key});

  @override
  State<LogInPage> createState() => _LogInPageState();
}

class _LogInPageState extends State<LogInPage> {

  final _formKey = GlobalKey<FormState>();
  final _userNameController = TextEditingController();
  final _passwordController = TextEditingController();
  final LocalAuthentication _localAuth = LocalAuthentication(); // 👈 تعریف نمونه اثر انگشت

  bool _showPassword = false;
  
  // 👈 ۱. متغیر مدیریت وضعیت بارگذاری
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

  // 👈 متد ورود با اثر انگشت
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

  // 👈 ۲. اصلاح متد _logIn و اتصال به بک‌اند جاوا
  Future<void> _logIn() async {
    if (_formKey.currentState!.validate()) {
      setState(() {
        _isLoading = true; // فعال‌سازی حالت لودینگ
      });

      try {
        // الف) ساخت Map درخواست با اکشن LOG_IN
        final requestMap = {
          "actionName": "User/logIn",
          "payload": {
            "userName": _userNameController.text.trim(),
            "password": _passwordController.text,
          }
        };

        // ب) تبدیل به JSON و اضافه کردن n\ برای readLine جاوا
        String jsonRequest = jsonEncode(requestMap) + "\n";

        // ج) ارسال درخواست به بک‌اند جاوا از طریق SocketService
        String rawResponse = await SocketService.sendRequest(jsonRequest);
        Map<String, dynamic> responseMap = jsonDecode(rawResponse);

        // د) بررسی پاسخ سرور
        if (mounted) {
          if (responseMap['status'] == "200") {

            String sessionId = responseMap['payload']['id'];
            SessionManager.instance.saveSessionId(
              sessionId
            );

            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text("Welcome back!")),
            );

            // هدایت به صفحه اصلی
            Navigator.pushReplacement(
              context,
              MaterialPageRoute(
                builder: (context) => const NavigatorPage(),
              ),
            );
          } else {
            // نمایش پیام خطا (مثلاً "Invalid username or password")
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
        // هـ) مدیریت خطای عدم اتصال به سرور
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
            _isLoading = false; // غیرفعال‌سازی لودینگ
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

    return Scaffold(
      body: Stack(
        children: [
          AuthHeader(
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

                    const SizedBox(height: 20),
                
                    // 👈 ۳. ترکیب دکمه Log In و دکمه اثر انگشت در یک Row
                    Row(
                      children: [
                        Expanded(
                          child: SizedBox(
                            height: 60,
                            child: ElevatedButton(
                              onPressed: _isLoading ? null : _logIn,
                              style: TextButton.styleFrom(
                                backgroundColor: const Color(0xFF1257FA),
                              ),
                              child: _isLoading
                                  ? const CircularProgressIndicator(color: Colors.white)
                                  : const Text(
                                      "Log In",
                                      style: TextStyle(color: Colors.white, fontSize: 18),
                                    ),
                            ),
                          ),
                        ),
                        const SizedBox(width: 12),
                        InkWell(
                          onTap: _isLoading ? null : _logInWithBiometrics,
                          borderRadius: BorderRadius.circular(8),
                          child: Container(
                            width: 60,
                            height: 60,
                            decoration: BoxDecoration(
                              color: const Color(0xFF1257FA).withOpacity(0.1),
                              borderRadius: BorderRadius.circular(8),
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
                    const SizedBox(height: 16),

                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Text("Don't have an account?"),
                        const SizedBox(width: 5),
                        TextButton(
                          onPressed: _isLoading
                              ? null
                              : () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (_) => const SignUpPage(),
                              ),
                            );
                          },
                          child: const Text('Sign Up'),
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