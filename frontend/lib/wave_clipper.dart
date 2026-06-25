import 'package:flutter/material.dart';
class WaveClipper extends CustomClipper<Path> {

  @override
  Path getClip(Size size) {

    Path path = Path();

    path.lineTo(0, size.height - 70);

    path.quadraticBezierTo(
      size.width * 0.25,
      size.height + 20,

      size.width * 0.5,
      size.height - 30,
    );

    path.quadraticBezierTo(
      size.width * 0.75,
      size.height - 90,

      size.width,
      size.height - 20,
    );

    path.lineTo(size.width, 0);

    path.close();

    return path;
  }

  @override
  bool shouldReclip(CustomClipper<Path> oldClipper) {
    return false;
  }
}