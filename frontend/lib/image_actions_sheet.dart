import 'package:flutter/material.dart';

class ImageActionsSheet extends StatelessWidget {
  const ImageActionsSheet({super.key});

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
            leading: Icon(Icons.add),
            title: Text("Add to album"),
          ),
          ListTile(
            leading: Icon(Icons.delete),
            title: Text("Delete"),
            iconColor: Colors.red,
            titleTextStyle: TextStyle(
              color: Colors.red , 
              fontSize: 18), 
          ),
          
          
          

        ],
      ),
    );
  }
}

void showImageActionsSheet(BuildContext context) {
  showModalBottomSheet(
    context: context,
    builder: (_) => const ImageActionsSheet(),
  );
}