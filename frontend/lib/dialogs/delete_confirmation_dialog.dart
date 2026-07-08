import 'package:flutter/material.dart';

void showDeleteDialog(
  BuildContext context,
  int selectedCount,
) {
  showDialog(
    context: context,
    builder: (context) {
      return AlertDialog(
        title: const Text("Delete Photos"),

        content: Text(
          selectedCount == 1
              ? "Are you sure you want to permanently delete this photo?"
              : "Are you sure you want to permanently delete $selectedCount photos?",
        ),

        actions: [

          TextButton(
            onPressed: () {
              Navigator.pop(context);
            },
            child: const Text("Cancel"),
          ),

          FilledButton(
            onPressed: () {

              // TODO:
              // delete photos

              Navigator.pop(context);
            },
            child: const Text("Delete"),
          ),

        ],
      );
    },
  );
}