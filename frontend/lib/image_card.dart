import 'package:flutter/material.dart';

class ImageCard extends StatelessWidget {

  final int index;

  final bool isSelected;

  final bool isSelectionMode;

  final VoidCallback onTap;

  final VoidCallback onLongPress;

  const ImageCard({
    super.key,
    required this.index,
    required this.isSelected,
    required this.isSelectionMode,
    required this.onTap,
    required this.onLongPress,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(

      onTap: onTap,

      onLongPress: onLongPress,

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