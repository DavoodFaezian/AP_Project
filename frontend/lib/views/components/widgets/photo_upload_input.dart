import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';

import '../../../../services/session_manager.dart';
import '../../../../services/socket_service.dart';
import 'socket_image.dart';

class PhotoUploadInput extends StatefulWidget {
  const PhotoUploadInput({
    super.key,
    required this.onPhotoUploaded,
    this.initialPhotoName,
    this.isProfilePicture = false,
    this.ownerId,
    this.title = 'Photo',
    this.buttonText = 'Upload Photo',
    this.size = 110,
  });

  /// Called after a successful upload.
  /// The parent receives the server-generated photoName.
  final ValueChanged<String> onPhotoUploaded;

  /// Existing image name, useful for edit screens or profile settings.
  final String? initialPhotoName;

  /// Sent to your backend upload API.
  final bool isProfilePicture;

  /// Used by SocketImage if it needs the image owner's ID.
  final String? ownerId;

  final String title;
  final String buttonText;
  final double size;

  @override
  State<PhotoUploadInput> createState() => _PhotoUploadInputState();
}

class _PhotoUploadInputState extends State<PhotoUploadInput> {
  final ImagePicker _picker = ImagePicker();

  bool _isUploading = false;
  File? _localPreview;

  late String? _photoName;

  @override
  void initState() {
    super.initState();
    _photoName = widget.initialPhotoName;
  }

  @override
  void didUpdateWidget(covariant PhotoUploadInput oldWidget) {
    super.didUpdateWidget(oldWidget);

    // Allows the parent to update initialPhotoName after fetching data.
    if (oldWidget.initialPhotoName != widget.initialPhotoName) {
      _photoName = widget.initialPhotoName;
    }
  }

  void _showSnackBar(String message, {bool isError = false}) {
    if (!mounted) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red : null,
      ),
    );
  }

  Future<void> _pickAndUploadPhoto() async {
    try {
      final XFile? selectedImage = await _picker.pickImage(
        source: ImageSource.gallery,
        imageQuality: 80,
      );

      if (selectedImage == null) return;

      final localFile = File(selectedImage.path);

      setState(() {
        _isUploading = true;
        _localPreview = localFile;
      });

      final imageBytes = await selectedImage.readAsBytes();
      final base64Photo = base64Encode(imageBytes);

      final requestMap = {
        'actionName': 'Photo/uploadPhoto',
        'payload': {
          'sessionId': SessionManager.instance.sessionId,
          'photoData': base64Photo,
          'isProfilePicture': widget.isProfilePicture,
        },
      };

      final rawResponse = await SocketService.sendRequest(
        '${jsonEncode(requestMap)}\n',
      );

      final responseMap = jsonDecode(rawResponse) as Map<String, dynamic>;

      if (responseMap['status'] != '200') {
        throw Exception(responseMap['message'] ?? 'Failed to upload photo.');
      }

      final payload = responseMap['payload'];

      /*
       * Prefer `photoName`, because that is the name expected by SocketImage.
       *
       * If your server currently returns:
       * { "payload": { "id": "..." } }
       * this fallback still works. Ideally, change Java to return `photoName`.
       */
      final photoName = payload is Map<String, dynamic>
          ? (payload['photoName'] ?? payload['id'])?.toString()
          : payload?.toString();

      if (photoName == null || photoName.isEmpty) {
        throw const FormatException(
          'Upload succeeded, but the server did not return photoName.',
        );
      }

      if (!mounted) return;

      setState(() {
        _photoName = photoName;
        _localPreview = null;
      });

      // Send the value outside this widget.
      widget.onPhotoUploaded(photoName);

      _showSnackBar('Photo uploaded successfully.');
    } catch (e) {
      if (mounted) {
        // Keep local image visible after an error so the user can retry.
        _showSnackBar('Could not upload photo: $e', isError: true);
      }
    } finally {
      if (mounted) {
        setState(() {
          _isUploading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final hasUploadedPhoto = _photoName != null && _photoName!.isNotEmpty;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          widget.title,
          style: const TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 12),

        Center(
          child: ClipOval(
            child: SizedBox(
              width: widget.size,
              height: widget.size,
              child: _localPreview != null
                  ? Image.file(
                _localPreview!,
                fit: BoxFit.cover,
              )
                  : hasUploadedPhoto
                  ? SocketImage(
                photoName: _photoName!,
                sessionId: SessionManager.instance.sessionId!,
                ownerId: widget.ownerId,
                loadingPlaceholder: const Center(
                  child: CircularProgressIndicator(strokeWidth: 2),
                ),
                errorPlaceholder: Container(
                  color: Colors.grey.shade200,
                  child: const Icon(
                    Icons.broken_image,
                    color: Colors.grey,
                  ),
                ),
                builder: (context, imageProvider) {
                  return Image(
                    image: imageProvider,
                    fit: BoxFit.cover,
                  );
                },
              )
                  : Container(
                color: Colors.grey.shade200,
                child: const Icon(
                  Icons.image_outlined,
                  size: 48,
                  color: Colors.grey,
                ),
              ),
            ),
          ),
        ),
        const SizedBox(height: 12),

        SizedBox(
          width: double.infinity,
          height: 50,
          child: ElevatedButton.icon(
            onPressed: _isUploading ? null : _pickAndUploadPhoto,
            icon: _isUploading
                ? const SizedBox(
              width: 20,
              height: 20,
              child: CircularProgressIndicator(
                strokeWidth: 2,
                color: Colors.white,
              ),
            )
                : const Icon(Icons.upload),
            label: Text(
              _isUploading ? 'Uploading...' : widget.buttonText,
            ),
          ),
        ),

        if (hasUploadedPhoto) ...[
          const SizedBox(height: 8),
          Text(
            'Photo name: $_photoName',
            textAlign: TextAlign.center,
            style: TextStyle(
              color: Colors.grey.shade700,
              fontSize: 12,
            ),
          ),
        ],
      ],
    );
  }
}
