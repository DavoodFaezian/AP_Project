import 'package:flutter/material.dart';

import '../../../components/widgets/custom_appbar.dart';
import '../../../components/widgets/custom_drawer.dart';
import '../../../components/widgets/custom_fab.dart';
import '../../../components/widgets/empty_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {

  /// آیا کاربر وارد حالت انتخاب چندتایی شده؟
  bool isSelectionMode = false;

  final List<int> photos = [];

  /// لیست عکس‌های انتخاب شده
  final List<int> selectedImages = [];

  @override
  Widget build(BuildContext context) {
    print(photos.length);
    return Scaffold(
      drawer: CustomDrawer(),
      appBar: isSelectionMode
    ? CustomAppBar(
        title: "${selectedImages.length} Selected",

        leading: IconButton(
          icon: const Icon(Icons.close),
          onPressed: () {
            setState(() {
              isSelectionMode = false;
              selectedImages.clear();
            });
          },
        ),


      )
    : CustomAppBar(
        title: "Home",
        actions: [
        
          IconButton(
            onPressed: () {
              // TODO: Filter
            },
            icon: const Icon(Icons.filter_list),
          ),

          IconButton(
            onPressed: () {
              // TODO: Sort
            },
            icon: const Icon(Icons.sort),
          ),
        ],
      ),

      body: Padding(
        padding: const EdgeInsets.all(8),
        child:  EmptyState(
              imagePath: 'assets/images/Image post-cuate.png',
              title: "No photos yet",
              subtitle: "Upload your first photo",
            )


      ),

      floatingActionButton: CustomFAB(
        onPressed: () {
          // TODO:
          // Navigate to Upload Screen
        },
      ),
    );
  }

}