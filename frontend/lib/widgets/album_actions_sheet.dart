import 'package:flutter/material.dart';
import 'package:test_app/dialogs/delete_confirmation_dialog.dart';

class ImageActionsSheet extends StatelessWidget {

  final List<int> selectedImages;

  const ImageActionsSheet({
    super.key,
    required this.selectedImages
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 400,
      child: Column(
        children: [
          ListTile(
            leading: Icon(Icons.download),
            title: Text("Download")
          ),
          ListTile(
          leading: Icon(Icons.share),
          title: Text("Share")
          ),
          ListTile(
            leading: Icon(Icons.remove),
            title: Text("Remove from album")
          ),
          ListTile(
            leading: Icon(Icons.move_down),
            title: Text("move")
          ),
          ListTile(
            leading: Icon(Icons.delete),
            title: Text("Delete"),
            iconColor: Colors.red,
            titleTextStyle: TextStyle(
              color: Colors.red , 
              fontSize: 18),
              onTap: () {
                Navigator.pop(context);
                showDeleteDialog(
                  context,
                  selectedImages.length,
                );
              },
          ),
          
          
          

        ],
      ),
    );
  }
}

void showImageActionsSheet(
  BuildContext context,
  List<int> selectedImages,
) {
  showModalBottomSheet(
    context: context,
    builder: (_) => ImageActionsSheet(
      selectedImages: selectedImages,
    ),
  );
}