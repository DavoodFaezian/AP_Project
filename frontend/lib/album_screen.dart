import 'package:flutter/material.dart';
import 'package:test_app/custom_appbar.dart';
import 'package:test_app/custom_drawer.dart';
import 'package:test_app/custom_fab.dart';
import 'package:test_app/empty_screen.dart';
import 'package:test_app/album_details_screen.dart';

class AlbumScreen extends StatefulWidget {
  const AlbumScreen({super.key});

  @override
  State<AlbumScreen> createState() => _AlbumScreenState();
}

class _AlbumScreenState extends State<AlbumScreen> {

  /// لیست آلبوم‌ها
  final List<String> albums = ["1" , "2" , "3" , "4" , "5"];

  @override
  Widget build(BuildContext context) {

    return Scaffold(

      drawer: const CustomDrawer(),

      appBar: const CustomAppBar(
        title: "Albums",
      ),

      body: albums.isEmpty
        ? const EmptyState(
            imagePath: "assets/images/Image folder-rafiki (2).png",
            title: "No albums yet",
            subtitle: "Create your first album.",
          )
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

          onTap: () {

          Navigator.push(

            context,

            MaterialPageRoute(

              builder: (context) => AlbumDetailScreen(

                albumName: albums[index],

              ),

            ),

          );

        },

        );

      },

    );

  }

  //-------------------------------------
  // زمانی که هیچ آلبومی وجود ندارد
  //-------------------------------------



}