import 'package:flutter/material.dart';
import 'package:test_app/image_card.dart';

class PhotoGrid extends StatelessWidget {

  /// لیست عکس‌ها
  final List<int> photos;

  /// عکس‌های انتخاب شده
  final List<int> selectedImages;

  /// آیا Selection Mode فعال است؟
  final bool isSelectionMode;

  /// زمانی که روی عکس کلیک می‌شود
  final Function(int index) onTap;

  /// زمانی که روی عکس Long Press می‌شود
  final Function(int index) onLongPress;

  const PhotoGrid({
    super.key,
    required this.photos,
    required this.selectedImages,
    required this.isSelectionMode,
    required this.onTap,
    required this.onLongPress,
  });

  @override
  Widget build(BuildContext context) {

    return GridView.builder(

      itemCount: photos.length,

      gridDelegate:
          const SliverGridDelegateWithFixedCrossAxisCount(

        crossAxisCount: 2,
        crossAxisSpacing: 2,
        mainAxisSpacing: 2,
        childAspectRatio: 0.9,

      ),

      itemBuilder: (context, index) {

        return ImageCard(

          index: index,

          isSelected: selectedImages.contains(index),

          isSelectionMode: isSelectionMode,

          onTap: () => onTap(index),

          onLongPress: () => onLongPress(index),

        );

      },

    );

  }

}