import 'package:flutter/material.dart';
import 'package:test_app/views/features/album/album_list_screen.dart';

import '../../features/search/search_screen.dart';
import '../screens/home/home_screen.dart';

class NavigatorPage extends StatefulWidget {
  const NavigatorPage({super.key});

  @override
  State<NavigatorPage> createState() => _NavigatorPageState();
}

class _NavigatorPageState extends State<NavigatorPage> {

  Color drawerIconColor = Colors.grey;

  int _selectedIndex = 0;

  final List<Widget> _pages = [
    HomeScreen(),
    AlbumListPage(),
    SearchScreen(),
  ];

  @override
  Widget build(BuildContext context) {

    return Scaffold(

      body: _pages[_selectedIndex],

      bottomNavigationBar: BottomNavigationBar(

        currentIndex: _selectedIndex,


        onTap: (index) {
          setState(() {
            _selectedIndex = index;
          });
        },

        selectedItemColor: Colors.deepPurple,

        unselectedItemColor: Colors.grey,

        items: const [

          BottomNavigationBarItem(
            icon: Icon(Icons.home_rounded),
            label: "Home",
          ),

          BottomNavigationBarItem(
            icon: Icon(Icons.photo_album_rounded),
            label: "Albums",
          ),

          BottomNavigationBarItem(
            icon: Icon(Icons.search_rounded),
            label: "Search",
          ),
        ],
      ),
    );
  }
}