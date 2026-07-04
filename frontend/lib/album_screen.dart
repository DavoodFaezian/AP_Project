import 'package:flutter/material.dart';
import 'package:test_app/custom_appbar.dart';
import 'package:test_app/custom_drawer.dart';
import 'package:test_app/custom_fab.dart';

class AlbumScreen extends StatefulWidget {
  const AlbumScreen({super.key});

  @override
  State<AlbumScreen> createState() => _AlbumScreenState();
}

class _AlbumScreenState extends State<AlbumScreen> {

  /// لیست آلبوم‌ها
  final List<String> albums = [];

  @override
  Widget build(BuildContext context) {

    return Scaffold(

      drawer: const CustomDrawer(),

      appBar: const CustomAppBar(
        title: "Albums",
      ),

      body: albums.isEmpty
          ? _buildEmptyAlbum()
          : _buildAlbumList(),

      floatingActionButton: CustomFAB(
        onPressed: () {
          // Create Album
        },
      ),

    );
  }

  //-------------------------------------
  // لیست آلبوم‌ها
  //-------------------------------------

  Widget _buildAlbumList() {

    return ListView.builder(

      itemCount: albums.length,

      itemBuilder: (context,index){

        return ListTile(

          leading: const Icon(Icons.photo_album),

          title: Text(albums[index]),

          subtitle: const Text("0 photos"),

          trailing: const Icon(Icons.arrow_forward_ios),

          onTap: (){

            // TODO:
            // Open Album

          },

        );

      },

    );

  }

  //-------------------------------------
  // زمانی که هیچ آلبومی وجود ندارد
  //-------------------------------------

  Widget _buildEmptyAlbum(){

    return Center(

      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Image.asset(
            'assets/images/Image folder-rafiki (2).png',
              width:  240,
              height: 240,
          ),
          const SizedBox(height: 20),

          const Text(
            "No albums yet",
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
            ),
          ),

          const SizedBox(height: 10),

          const Text(
            "Create your first album.",
            style: TextStyle(
              color: Colors.grey,
            ),
          ),
        ],
      ),

    );

  }

}