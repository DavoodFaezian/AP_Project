import 'package:flutter/material.dart';
import 'package:test_app/widgets/wave_clipper.dart';

class AuthHeader extends StatelessWidget {

  final String title;

  const AuthHeader({
    super.key,
    required this.title,
  });

  @override
  Widget build(BuildContext context) {

    return Stack(
      children: [

        ClipPath(
          clipper: WaveClipper(),

          child: Container(
            height: 180,

            decoration: const BoxDecoration(
              gradient: LinearGradient(
                begin: Alignment.centerLeft,
                end: Alignment.centerRight,

                colors: [
                  Color(0xFF5B21B6),
                  Color(0xFFA855F7),
                ],
              ),
            ),
          ),
        ),

        Positioned(
          top: 70,
          left: 70,

          child: Text(
            title,

            style: const TextStyle(
              color: Colors.white,
              fontSize: 30,
            ),
          ),
        ),
      ],
    );
  }
}