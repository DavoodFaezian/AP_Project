import 'package:flutter/material.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {

  Color drawerIconColor = Colors.grey;

  int _selectedIndex = 0;

  final List<Widget> _pages = [
    Center(child: Text("home")),   
    Center(child: Text("album")),
    Center(child: Text("search")),
  ];

  @override
  Widget build(BuildContext context) {

    

    return Scaffold(

      appBar: AppBar(
          title: Text("Photo Gallery")
      ),


      drawer: Drawer(

        child: ListView(

          padding: EdgeInsets.zero,

          children: [

            Container(

              decoration: BoxDecoration(
                gradient: LinearGradient(
                  colors: [
                    Color(0xFF5B21B6),
                    Color(0xFFA855F7),
                  ]
                ),
              ),
              
              child: Column(

                mainAxisAlignment: MainAxisAlignment.center,

                children: [
                  
                  SizedBox(height: 50,),

                  Stack(

                    children: [

                      CircleAvatar(
                        radius: 50,
                        child: Icon(
                          Icons.person,
                          size: 60,
                        ),
                      ),

                      Positioned(
                        bottom: -11,
                        right: -11,
                        child: IconButton(
                          onPressed: () {}, 
                          icon: Icon(
                            Icons.photo_camera,
                            color: Colors.lightBlue,
                            size: 35
                            )
                          )
                        ),
                    ],
                  ),

                  SizedBox(height: 25),
                  
                  Text(
                    "Username",
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 20,
                    )
                  ),

                  SizedBox(height: 25)

              ],),
            ),

            Column(

              children: [
                
                ListTile(
                  leading: Icon(
                    Icons.person,
                    color: drawerIconColor,
                  ),

                  title: Text("Profile"),

                  onTap: () {

                  },
                ),

                ListTile(
                  leading: Icon(
                    Icons.settings,
                    color: drawerIconColor
                    ),

                  title: Text("Settings"),

                  onTap: () {

                  }
                ),

                ListTile(
                  leading: Icon(
                    Icons.info,
                    color: drawerIconColor,
                    ),

                  title: Text("About"),

                  onTap: () {},
                ),

                Divider(
                  thickness: 1,
                  color: Colors.grey,
                ),

                ListTile(
                  leading: Icon(
                    Icons.logout,
                    color: drawerIconColor,
                    ),

                  title: Text("Log out"),

                  onTap:() {
                    
                  },
                )
              ],
            )
          ],

        )
      ),

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