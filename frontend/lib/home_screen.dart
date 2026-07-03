import 'package:flutter/material.dart';
import 'package:test_app/image_actions_sheet.dart';
import 'package:test_app/custom_appbar.dart';
import 'package:test_app/custom_drawer.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {

  /// آیا کاربر وارد حالت انتخاب چندتایی شده؟
  bool isSelectionMode = false;

  /// لیست عکس‌های انتخاب شده
  final List<int> selectedImages = [];

  @override
  Widget build(BuildContext context) {
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
        child: GridView.builder(
          itemCount: 20, // TODO: تعداد عکس‌ها
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2,
            crossAxisSpacing: 10,
            mainAxisSpacing: 10,
            childAspectRatio: 0.9,
          ),
          itemBuilder: (context, index) {
            return _buildImageCard(index);
          },
        ),
      ),

      floatingActionButton: FloatingActionButton(
        onPressed: () {
          // TODO: Navigate to Upload Screen
        },
        shape: CircleBorder(),
        backgroundColor: Color(0xFF1257FA),
        child: const Icon(
          Icons.add,
          color: Colors.white,
          ),
      ),
    );
  }

  Widget _buildImageCard(int index) {

    final isSelected = selectedImages.contains(index);

    return GestureDetector(

      onTap: () {

        if (isSelectionMode) {

          setState(() {

            if (isSelected) {
              selectedImages.remove(index);
            } else {
              selectedImages.add(index);
            }

            if (selectedImages.isEmpty) {
              isSelectionMode = false;
            }

          });

          return;
        }

        // TODO:
        // Navigate to Image Detail Screen

      },

      onLongPress: () {

        setState(() {

          isSelectionMode = true;
          selectedImages.add(index);

        });

      },

      child: Stack(

        children: [

          Card(
            elevation: 3,
            clipBehavior: Clip.antiAlias,
            child: Column(

              children: [

                Expanded(
                  child: Container(
                    color: Colors.grey.shade300,

                    child: const Center(
                      child: Icon(
                        Icons.image,
                        size: 60,
                      ),
                    ),
                  ),
                ),

              ],
            ),
          ),

          if (isSelectionMode)
            Positioned(
              top: 8,
              right: 8,
              child: CircleAvatar(
                radius: 12,
                backgroundColor:
                    isSelected ? Colors.blue : Colors.white,
                child: Icon(
                  Icons.check,
                  size: 16,
                  color:
                      isSelected ? Colors.white : Colors.grey,
                ),
              ),
            ),
        ],
      ),
    );
  }
}