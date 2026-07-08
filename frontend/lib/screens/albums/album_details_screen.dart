import 'package:flutter/material.dart';

import 'package:test_app/widgets/custom_appbar.dart';
import 'package:test_app/widgets/custom_drawer.dart';
import 'package:test_app/widgets/custom_fab.dart';
import 'package:test_app/widgets/empty_screen.dart';
import 'package:test_app/widgets/image_grid.dart';
import 'package:test_app/widgets/album_actions_sheet.dart';
import 'package:test_app/screens/home/image_detail_screen.dart';

class AlbumDetailScreen extends StatefulWidget {

  final String albumName;

  const AlbumDetailScreen({
    super.key,
    required this.albumName,
  });

  @override
  State<AlbumDetailScreen> createState() =>
      _AlbumDetailScreenState();
}

class _AlbumDetailScreenState extends State<AlbumDetailScreen> {

  /// عکس‌های داخل آلبوم
  final List<int> photos = [1,2,3,4,5,6,7,8,9,10];

  /// عکس‌های انتخاب شده
  final List<int> selectedImages = [];

  /// آیا Selection Mode فعال است؟
  bool isSelectionMode = false;

  @override
  Widget build(BuildContext context) {

    return Scaffold(

      drawer: const CustomDrawer(),

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
                      selectedImages,
                    );

                  },

                  icon: const Icon(Icons.more_vert),

                ),

              ],

            )

          : CustomAppBar(

              title: widget.albumName,

              leading: const BackButton(),

              actions: [

                IconButton(

                  onPressed: () {},

                  icon: const Icon(Icons.sort),

                ),

                IconButton(

                  onPressed: () {},

                  icon: const Icon(Icons.filter_list),

                ),

              ],

            ),

      body: Padding(

        padding: const EdgeInsets.all(8),

        child: photos.isEmpty

            ? const EmptyState(

                imagePath:
                    "assets/images/Image post-cuate.png",

                title: "No photos",

                subtitle:
                    "Add photos to this album.",

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
          // Add photos to album

        },

      ),

    );

  }

}