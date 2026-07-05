import 'package:flutter/material.dart';
import 'package:test_app/image_actions_sheet.dart';
import 'package:test_app/custom_appbar.dart';
import 'package:test_app/custom_drawer.dart';
import 'package:test_app/custom_fab.dart';
import 'package:test_app/image_grid.dart';
import 'package:test_app/empty_screen.dart';

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

        actions: [
          IconButton(
            onPressed: () {
              showImageActionsSheet(
                context,
                selectedImages
              );
            },
            icon: const Icon(Icons.more_vert),
          ),
        ],
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
        child: photos.isEmpty
            ? EmptyState(
              imagePath: 'assets/images/Image post-cuate.png',
              title: "No photos yet",
              subtitle: "Upload your first photo",
            )
            : PhotoGrid(
                photos: photos,
                selectedImages: selectedImages,
                isSelectionMode: isSelectionMode,

                onTap: (index) {
                  setState(() {

                    if (isSelectionMode) {

                      if (selectedImages.contains(index)) {
                        selectedImages.remove(index);
                      } else {
                        selectedImages.add(index);
                      }

                      if (selectedImages.isEmpty) {
                        isSelectionMode = false;
                      }

                    } else {

                      // TODO:
                      // Navigate to Image Detail

                    }

                  });
                },

                onLongPress: (index) {
                  setState(() {

                    isSelectionMode = true;

                    if (!selectedImages.contains(index)) {
                      selectedImages.add(index);
                    }

                  });
                },
              ),
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