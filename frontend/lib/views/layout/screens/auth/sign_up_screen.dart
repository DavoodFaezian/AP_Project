import 'dart:convert';
import 'package:flutter/material.dart';
import '../../../../services/socket_service.dart';
import '../../../components/widgets/auth_header.dart';
import '../../../components/widgets/input_decoration.dart';
import '../../navigation/navigator_screen.dart';


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
  
  // 👈 ۱. متغیر برای حالت بارگذاری
  bool _isLoading = false; 

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

  // 👈 ۲. اصلاح اصلی: اتصال متد _signUp به بک‌اند سوکت
  Future<void> _signUp() async {
    if (_formKey.currentState!.validate()) {
      setState(() {
        _isLoading = true; // فعال‌سازی حالت لودینگ
      });

      try {
        // الف) ساخت شیء Request مطابق با نیازمندی‌های بک‌اند جاوا
        final requestMap = {
          "action": "SIGN_UP",
          "username": _userNameController.text.trim(),
          "password": _passwordController.text,
        };

        // ب) تبدیل به JSON و اضافه کردن \n الزامی برای readLine بک‌اند
        String jsonRequest = jsonEncode(requestMap) + "\n";

        // ج) ارسال از طریق سوکت و دریافت Response (با فرض وجود SocketService)
        // اگر SocketService داری متد ارسال آن را صدا بزن:
        String rawResponse = await  SocketService.sendRequest(jsonRequest);
        Map<String, dynamic> responseMap = jsonDecode(rawResponse);

        // د) بررسی وضعیت پاسخ بک‌اند
        if (mounted) {
          // اگر کد پاسخ موفقیت‌آمیز بود (مثلاً 200 یا status == SUCCESS)
          if (responseMap['statusCode'] == '200' || responseMap['status'] == 'SUCCESS') {
            
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text("Account created successfully!")),
            );

            // هدایت به صفحه اصلی
            Navigator.pushReplacement(
              context,
              MaterialPageRoute(
                builder: (context) => const NavigatorPage(),
              ),
            );
          } else {
            // اگر بک‌اند خطایی برگرداند (مثلاً "User already exists")
            String errorMessage = responseMap['message'] ?? "Sign up failed!";
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(errorMessage), backgroundColor: Colors.red),
            );
          }
        }
      } catch (e) {
        // هـ) مدیریت خطاهای عدم اتصال به شبکه یا قطع بودن سوکت
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
            _isLoading = false; // غیرفعال‌سازی لودینگ در هر شرایطی
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
    return Scaffold(
      body: Stack(
        children: [
          AuthHeader(
            title: "Sign Up...",
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
                
                    const SizedBox(height: 16),
                
                    TextFormField(
                      controller: _confirmPasswordController,
                      obscureText: !_showConfirmPassword,
                      decoration: buildInputDecoration(
                        "Confirm Password",
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
                    ),

                    const SizedBox(height: 20),
                
                    // 👈 ۳. تغییر دکمه Sign Up برای پشتیبانی از حالت Loading
                    SizedBox(
                      width: double.infinity,
                      height: 60,
                      child: ElevatedButton(
                        onPressed: _isLoading ? null : _signUp, // در زمان لودینگ کلیک نمی‌شود
                        style: TextButton.styleFrom(
                          backgroundColor: const Color(0xFF1257FA),
                        ),
                        child: _isLoading
                            ? const CircularProgressIndicator(color: Colors.white)
                            : const Text(
                                "Sign Up",
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