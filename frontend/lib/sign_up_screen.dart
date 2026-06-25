import 'package:flutter/material.dart';

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

  void _signUp() {

    if (_formKey.currentState!.validate()) {

      print("Name: ${_userNameController.text}");
      print("Password: ${_passwordController.text}");

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text("Sign Up Successful"),
        ),
      );
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

      body: Padding(

        padding: const EdgeInsets.all(16),

        child: Form(

          key: _formKey,

          child: SingleChildScrollView(
            child: Column(
            
              children: [
            
                Image.asset(
                  'assets/images/Photos-pana (1).png',
                  width:  300,
                  height: 300,
                  ),
            
                TextFormField(
                  controller: _userNameController,
                  decoration: InputDecoration(
                    labelText: "Username",

                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(30),
                    ),

                    enabledBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(30),
                    ),

                    focusedBorder: OutlineInputBorder(
                      borderSide: BorderSide(color: Colors.deepPurple),
                      borderRadius: BorderRadius.circular(30),
                    ),

                    focusedErrorBorder: OutlineInputBorder(
                      borderSide: BorderSide(color: Colors.red),
                      borderRadius: BorderRadius.circular(30),
                    ),

                    errorBorder: OutlineInputBorder(
                      borderSide: BorderSide(color: Colors.red),
                      borderRadius: BorderRadius.circular(30),
                    ),
                  ),
            
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return "Username is required";
                    }
            
                    return null;
                  },
                ),
            
                const SizedBox(height: 16),
            
                TextFormField(
                  controller: _passwordController,
            
                  obscureText: !_showPassword,
            
                  decoration: InputDecoration(
                    labelText: "Password",

                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(30),
                      ),
            
                      enabledBorder: OutlineInputBorder(
                        borderSide: BorderSide(),
                      borderRadius: BorderRadius.circular(30),
                      ),
            
                      focusedBorder: OutlineInputBorder(
                        borderSide: BorderSide(color: Colors.deepPurple),
                        borderRadius: BorderRadius.circular(30),
                      ),

                      focusedErrorBorder: OutlineInputBorder(
                        borderSide: BorderSide(
                          color: Colors.red,
                        ),
                        borderRadius: BorderRadius.circular(30),
                      ),

                      errorBorder: OutlineInputBorder(
                        borderSide: BorderSide(
                          color: Colors.red,
                        ),
                        borderRadius: BorderRadius.circular(30),
                      ),
            
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
            
                  validator: (value) {
            
                    if (value == null || value.length < 8) {
                      return "Password must be at least 8 characters";
                    }

                    if(value.contains(_userNameController.text)){
                      return "Password must not conatin username";
                    }

                    if (!specialCharRegex.hasMatch(value)) {
                      return 'Password must contain a special character';
                    }
            
                    return null;
                  },
                ),
            
                const SizedBox(height: 16),
            
                TextFormField(
                  controller: _confirmPasswordController,
            
                  obscureText: !_showConfirmPassword,
            
                  decoration: InputDecoration(
                      labelText: "Confirm Password",
            
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(30),
                      ),
            
                      enabledBorder: OutlineInputBorder(
                        borderSide: BorderSide(),
                        borderRadius: BorderRadius.circular(30),
                      ),
            
                      focusedBorder: OutlineInputBorder(
                        borderSide: BorderSide(color: Colors.deepPurple),
                        borderRadius: BorderRadius.circular(30),
                      ),

                      focusedErrorBorder: OutlineInputBorder(
                        borderSide: BorderSide(
                          color: Colors.red,
                        ),
                        borderRadius: BorderRadius.circular(30),
                      ),

                      errorBorder: OutlineInputBorder(
                        borderSide: BorderSide(
                          color: Colors.red,
                        ),
                        borderRadius: BorderRadius.circular(30),
                      ),
            
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
            
            
                  validator: (value) {
            
                    if (value != _passwordController.text || value == null || value.isEmpty) {
                      return "Passwords do not match";
                    }
            
                    return null;
                  },
            
                  
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
    );
  }
}