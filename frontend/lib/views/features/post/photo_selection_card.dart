import 'package:flutter/material.dart';
import 'package:test_app/models/photo.dart';
import 'package:test_app/services/session_manager.dart';
import 'package:test_app/views/components/widgets/socket_image.dart';

class PhotoSelectionCard extends StatelessWidget {
  final Photo photo;
  final bool isSelected;
  final VoidCallback onTap;
  final Color selectionColor;

  const PhotoSelectionCard({
    super.key,
    required this.photo,
    required this.isSelected,
    required this.onTap,
    required this.selectionColor,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 150),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
          border: isSelected
              ? Border.all(color: selectionColor, width: 2.5)
              : Border.all(color: Colors.grey.shade200, width: 1),
          boxShadow: [
            if (isSelected)
              BoxShadow(
                color: selectionColor.withOpacity(0.2),
                blurRadius: 8,
                offset: const Offset(0, 4),
              ),
          ],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Expanded(
              child: Stack(
                fit: StackFit.expand,
                children: [
                  ClipRRect(
                    borderRadius: const BorderRadius.vertical(top: Radius.circular(14)),
                    child: SocketImage(
                      photoName: photo.photoName,
                      sessionId: SessionManager.instance.sessionId!,
                      ownerId: photo.ownerId,
                      loadingPlaceholder: const Center(child: CircularProgressIndicator(strokeWidth: 2)),
                      errorPlaceholder: Container(color: Colors.grey.shade100),
                      builder: (context, provider) => Image(image: provider, fit: BoxFit.cover),
                    ),
                  ),
                  Positioned(
                    top: 8,
                    left: 8,
                    child: Container(
                      width: 22,
                      height: 22,
                      decoration: BoxDecoration(
                        color: isSelected ? selectionColor : Colors.white.withOpacity(0.8),
                        shape: BoxShape.circle,
                        border: Border.all(
                          color: isSelected ? selectionColor : Colors.grey.shade400,
                          width: 1.5,
                        ),
                      ),
                      child: isSelected
                          ? const Icon(Icons.check, size: 14, color: Colors.white)
                          : null,
                    ),
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(10, 8, 10, 8),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    photo.title.isNotEmpty ? photo.title : photo.photoName,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
                  ),
                  const SizedBox(height: 4),
                  if (photo.tags.isNotEmpty)
                    Text(
                      photo.tags.map((t) => '#$t').join(' '),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(color: Colors.blue, fontSize: 11),
                    ),
                  const SizedBox(height: 4),
                  Text(
                    photo.lastModified?.toString().split(' ')[0] ?? 
                    photo.createdAt.toString().split(' ')[0],
                    style: TextStyle(color: Colors.grey.shade600, fontSize: 10),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
