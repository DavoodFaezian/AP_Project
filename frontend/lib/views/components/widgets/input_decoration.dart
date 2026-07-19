import 'package:flutter/material.dart';
InputDecoration buildInputDecoration(
  String label,
  {Widget? suffixIcon}
  ) {
  return InputDecoration(
    labelText: label,

    border: OutlineInputBorder(
      borderRadius: BorderRadius.circular(30),
    ),

    enabledBorder: OutlineInputBorder(
      borderRadius: BorderRadius.circular(30),
    ),

    focusedBorder: OutlineInputBorder(
      borderSide: const BorderSide(
        color: Colors.deepPurple,
      ),
      borderRadius: BorderRadius.circular(30),
    ),

    focusedErrorBorder: OutlineInputBorder(
      borderSide: const BorderSide(
        color: Colors.red,
      ),
      borderRadius: BorderRadius.circular(30),
    ),

    errorBorder: OutlineInputBorder(
      borderSide: const BorderSide(
        color: Colors.red,
      ),
      borderRadius: BorderRadius.circular(30),
    ),

    suffixIcon: suffixIcon,
  );
}