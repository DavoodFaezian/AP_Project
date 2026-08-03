import 'dart:convert';
import 'dart:typed_data';

import 'package:flutter/material.dart';

import '../../../../services/socket_service.dart';

class SocketImage extends StatefulWidget {
  final String photoName;
  final String sessionId;
  final String? ownerId;

  /// The parent controls how the image is rendered and styled.
  final Widget Function(
      BuildContext context,
      ImageProvider imageProvider,
      ) builder;

  /// Optional parent-provided widgets for loading/error states.
  final Widget? loadingPlaceholder;
  final Widget? errorPlaceholder;

  const SocketImage({
    super.key,
    required this.photoName,
    required this.sessionId,
    this.ownerId,
    required this.builder,
    this.loadingPlaceholder,
    this.errorPlaceholder,
  });

  @override
  State<SocketImage> createState() => _SocketImageState();
}

class _SocketImageState extends State<SocketImage> {
  Uint8List? _imageBytes;
  bool _isLoading = false;
  String? _error;
  String? _loadedPhotoName;

  @override
  void initState() {
    super.initState();
    _fetchImageBase64();
  }

  @override
  void didUpdateWidget(covariant SocketImage oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (oldWidget.photoName != widget.photoName ||
        oldWidget.ownerId != widget.ownerId ||
        oldWidget.sessionId != widget.sessionId) {
      _fetchImageBase64();
    }
  }

  Future<void> _fetchImageBase64() async {
    if (widget.photoName.isEmpty) {
      return;
    }

    final requestedPhotoName = widget.photoName;

    setState(() {
      _isLoading = true;
      _error = null;
      _imageBytes = null;
      _loadedPhotoName = requestedPhotoName;
    });

    try {
      final requestMap = {
        "actionName": "Photo/getPhotoBytes",
        "payload": {
          "sessionId": widget.sessionId,
          "photoId": widget.photoName,
          "ownerId": widget.ownerId,
        },
      };

      final jsonRequest = '${jsonEncode(requestMap)}\n';
      final rawResponse = await SocketService.sendRequest(jsonRequest);
      final responseMap = jsonDecode(rawResponse) as Map<String, dynamic>;

      if (responseMap['status'] != '200') {
        throw Exception(
          responseMap['message'] ?? 'Failed to load image.',
        );
      }

      final payload = responseMap['payload'] as Map<String, dynamic>;

      // Change 'base64' if the backend uses another key:
      // for example: payload['photoBase64'] or payload['imageBase64'].
      String base64Image = payload['photoData']?.toString() ?? '';

      if (base64Image.isEmpty) {
        throw Exception('The server returned an empty Base64 image.');
      }

      // Supports both plain Base64 and a data-URI such as:
      // data:image/png;base64,iVBORw0KGgo...
      if (base64Image.contains(',')) {
        base64Image = base64Image.split(',').last;
      }

      final Uint8List imageBytes = base64Decode(base64Image);

      if (!mounted || _loadedPhotoName != requestedPhotoName) {
        return;
      }

      setState(() {
        _imageBytes = imageBytes;
        _isLoading = false;
      });
    } catch (e) {
      if (!mounted || _loadedPhotoName != requestedPhotoName) {
        return;
      }

      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return widget.loadingPlaceholder ?? const SizedBox.shrink();
    }

    if (_error != null || _imageBytes == null) {
      return widget.errorPlaceholder ?? const SizedBox.shrink();
    }

    return widget.builder(
      context,
      MemoryImage(_imageBytes!),
    );
  }
}
