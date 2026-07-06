import 'package:flutter/material.dart';
import 'package:test_app/log_in_screen.dart';

void showLogoutDialog(BuildContext context) {

  showDialog(

    context: context,

    builder: (context) {

      return AlertDialog(

        title: const Text("Log out"),

        content: const Text(
          "Are you sure you want to log out?",
        ),

        actions: [

          TextButton(

            onPressed: () {

              Navigator.pop(context);

            },

            child: const Text("Cancel"),

          ),

          TextButton(

            onPressed: () {

              Navigator.pushAndRemoveUntil(

                context,

                MaterialPageRoute(

                  builder: (_) => const LogInPage(),

                ),

                (route) => false,

              );

            },

            style: TextButton.styleFrom(
              foregroundColor: Colors.red,
            ),

            child: const Text("Log out"),

          ),

        ],

      );

    },

  );

}