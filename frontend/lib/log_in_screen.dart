import 'package:flutter/material.dart';
import 'package:test_app/auth_header.dart';
import 'package:test_app/input_decoration.dart';
import 'package:test_app/navigator_screen.dart';

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

  void _logIn() {

    if (_formKey.currentState!.validate()) {

      // TODO:
      // Check username and password with Backend

      Navigator.pushReplacement(

        context,

        MaterialPageRoute(

          builder: (context) => const NavigatorPage(),

        ),

      );

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

                    const SizedBox(height: 20),
                
                    SizedBox(
                      width: double.infinity,
                      height: 60,
                      child: ElevatedButton(
                        onPressed: _logIn,
                        style: TextButton.styleFrom(
                          backgroundColor: Color(0xFF1257FA)
                        ),
                        child: Text(
                        "Log In" ,
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