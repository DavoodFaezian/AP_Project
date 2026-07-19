import 'package:flutter/material.dart';
import 'package:test_app/repositories/album_repository.dart';

import '../../../models/album.dart';

class AlbumMultiSelectField extends FormField<Set<String>> {
  AlbumMultiSelectField({
    super.key,
    required AlbumRepository albumRepository,
    required String ownerId,
    Set<String>? initialValue,
    ValueChanged<Set<String>>? onChanged,
    String title = 'Albums',
    String hintText = 'Select albums',
    FormFieldValidator<Set<String>>? validator,
    bool enabled = true,
  }) : super(
    initialValue: initialValue ?? <String>{},
    validator: validator,
      builder: (field) {
        final selectedIds = field.value ?? <String>{};

        return FutureBuilder<List<Album>>(
          future: albumRepository.getAlbumsByOwner(ownerId),
          builder: (context, snapshot) {
            final albums = snapshot.data ?? <Album>[];

            final selectedAlbums = albums
                .where((album) => selectedIds.contains(album.id))
                .map((album) => album.albumName)
                .toList();

            final displayText = selectedAlbums.isEmpty
                ? hintText
                : selectedAlbums.join(', ');

            Future<void> openSelector() async {
              if (!enabled || snapshot.connectionState == ConnectionState.waiting) {
                return;
              }

              if (snapshot.hasError) {
                return;
              }

              final result = await showDialog<Set<String>>(
                context: field.context,
                builder: (_) => _AlbumSelectionDialog(
                  title: title,
                  albums: albums,
                  initialSelectedIds: selectedIds,
                ),
              );

              if (result == null) {
                return;
              }

              field.didChange(result);
              onChanged?.call(result);
            }

            return InkWell(
              onTap: openSelector,
              borderRadius: BorderRadius.circular(12),
              child: InputDecorator(
                decoration: InputDecoration(
                  labelText: title,
                  hintText: hintText,
                  border: const OutlineInputBorder(),
                  errorText: field.errorText ?? (snapshot.hasError ? 'Failed to load albums' : null),
                  enabled: enabled,
                  suffixIcon: const Icon(Icons.arrow_drop_down),
                ),
                isEmpty: selectedAlbums.isEmpty,
                child: Text(
                  snapshot.connectionState == ConnectionState.waiting
                      ? hintText
                      : displayText,
                  style: selectedAlbums.isEmpty
                      ? TextStyle(
                    color: Theme.of(field.context).hintColor,
                  )
                      : null,
                ),
              ),
            );
          },
        );
      }

  );
}

class _AlbumSelectionDialog extends StatefulWidget {
  const _AlbumSelectionDialog({
    required this.title,
    required this.albums,
    required this.initialSelectedIds,
  });

  final String title;
  final List<Album> albums;
  final Set<String> initialSelectedIds;

  @override
  State<_AlbumSelectionDialog> createState() => _AlbumSelectionDialogState();
}

class _AlbumSelectionDialogState extends State<_AlbumSelectionDialog> {
  late final Set<String> _selectedIds = {...widget.initialSelectedIds};

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(widget.title),
      content: SizedBox(
        width: double.maxFinite,
        child: widget.albums.isEmpty
            ? const Center(child: Text('No albums available'))
            : SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: widget.albums.map((album) {
              final checked = _selectedIds.contains(album.id);

              return CheckboxListTile(
                value: checked,
                title: Text(album.albumName),
                contentPadding: EdgeInsets.zero,
                controlAffinity: ListTileControlAffinity.leading,
                onChanged: (value) {
                  setState(() {
                    if (value == true) {
                      _selectedIds.add(album.id);
                    } else {
                      _selectedIds.remove(album.id);
                    }
                  });
                },
              );
            }).toList(),
          ),
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.of(context).pop(),
          child: const Text('Cancel'),
        ),
        TextButton(
          onPressed: () => Navigator.of(context).pop(<String>{}),
          child: const Text('Clear'),
        ),
        FilledButton(
          onPressed: () => Navigator.of(context).pop(_selectedIds),
          child: const Text('OK'),
        ),
      ],
    );
  }
}
