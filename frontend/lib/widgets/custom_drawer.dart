import 'package:flutter/material.dart';
import 'package:test_app/logout_confirmation_dialog.dart';
import 'package:test_app/setting_screen.dart';

class CustomDrawer extends StatelessWidget {
  const CustomDrawer({super.key});

  @override
  Widget build(BuildContext context) {
    
    return Drawer(

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
                  ),

                  title: Text("Profile"),

                  onTap: () {

                  },
                ),

                ListTile(
                  leading: Icon(
                    Icons.settings,
                    ),

                  title: Text("Settings"),

                  onTap: () {

                    // بستن Drawer
                    Navigator.pop(context);

                    // رفتن به صفحه Settings
                    Navigator.push(

                      context,

                      MaterialPageRoute(

                        builder: (context) => const SettingsScreen(),

                      ),

                    );

                  },
                ),

                ListTile(
                  leading: Icon(
                    Icons.info,
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
                    color: Colors.red,
                    ),

                  title: Text("Log out"),

                  onTap: () {

                    // بستن Drawer
                    Navigator.pop(context);

                    // نمایش دیالوگ خروج
                    showLogoutDialog(context);

                  },
                )
              ],
            )
          ],

        )
      );
  }
}