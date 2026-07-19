import 'package:flutter/material.dart';

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
        return "Password must not conatin username";
       }

    if (!specialCharRegex.hasMatch(value)) {
        return 'Password must contain a special character';
     }
                
     return null;
  }

  void _signUp() {

    if (_formKey.currentState!.validate()) {

      // TODO:
      // Send data to Backend

      Navigator.pushReplacement(

        context,

        MaterialPageRoute(

          builder: (context) => const NavigatorPage(),

        ),

      );

    }

  }

  String? validateConfirmPassword(String? value){
    if (value != _passwordController.text || value == null || value.isEmpty) {
      return "Passwords do not match";
    }            
    return null;
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

                    SizedBox(height: 100),
                
                    Image.asset(
                      'assets/images/Photos-pana (1).png',
                      width:  300,
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
                
                      validator: validatePassword
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
                
                
                      validator: validateConfirmPassword
                      
                    ),

                    const SizedBox(height: 20),
                
                    SizedBox(
                      width: double.infinity,
                      height: 60,
                      child: ElevatedButton(
                        onPressed: _signUp,
                        style: TextButton.styleFrom(
                          backgroundColor: Color(0xFF1257FA)
                        ),
                        child: Text(
                        "Sign Up" ,
                        style: TextStyle(color: Colors.white))
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ]
      ),
    );
  }
}