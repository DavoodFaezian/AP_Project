import 'package:flutter/material.dart';
import 'package:test_app/navigator_screen.dart';
import 'package:test_app/log_in_screen.dart';
import 'package:test_app/sign_up_screen.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:test_app/home_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        textTheme: GoogleFonts.poppinsTextTheme(),
      ),
      home: NavigatorPage()
    );
  }
}