import 'dart:convert'; // 👈 برای jsonEncode و jsonDecode
import 'package:flutter/material.dart';
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

  // 👈 ۲. اصلاح متد _logIn و اتصال به بک‌اند جاوا
  Future<void> _logIn() async {
    if (_formKey.currentState!.validate()) {
      setState(() {
        _isLoading = true; // فعال‌سازی حالت لودینگ
      });

      try {
        // الف) ساخت Map درخواست با اکشن LOG_IN
        final requestMap = {
          "action": "User/logIn",
          "userName": _userNameController.text.trim(),
          "password": _passwordController.text,
        };

        // ب) تبدیل به JSON و اضافه کردن n\ برای readLine جاوا
        String jsonRequest = jsonEncode(requestMap) + "\n";

        // ج) ارسال درخواست به بک‌اند جاوا از طریق SocketService
        String rawResponse = await SocketService.sendRequest(jsonRequest);
        Map<String, dynamic> responseMap = jsonDecode(rawResponse);

        // د) بررسی پاسخ سرور
        if (mounted) {
          if (responseMap['statusCode'] == 200 || responseMap['status'] == 'SUCCESS') {

            String sessionId = responseMap['sessionId'];
            SessionManager.instance.setSession(
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
                
                    // 👈 ۳. تغییر دکمه Log In برای نمایش Spinner در زمان ارسال
                    SizedBox(
                      width: double.infinity,
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