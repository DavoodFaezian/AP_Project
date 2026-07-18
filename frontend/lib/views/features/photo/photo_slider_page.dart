import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class PhotoSliderPage<T> extends StatefulWidget {
  const PhotoSliderPage({
    super.key,
    required this.items,
    required this.initialItemId,
    required this.idBuilder,
    required this.imageProviderBuilder,
    this.titleBuilder,
    this.onEyePressed,
  });

  final List<T> items;
  final String initialItemId;
  final String Function(T item) idBuilder;
  final ImageProvider Function(T item) imageProviderBuilder;
  final String Function(T item)? titleBuilder;
  final VoidCallback? onEyePressed;

  @override
  State<PhotoSliderPage<T>> createState() => _PhotoSliderPageState<T>();
}

class _PhotoSliderPageState<T> extends State<PhotoSliderPage<T>> {
  static const double _largeScreenBreakpoint = 700;
  static const Duration _pageAnimationDuration = Duration(milliseconds: 300);
  static const Curve _pageAnimationCurve = Curves.easeInOut;

  late final PageController _pageController;
  late final FocusNode _focusNode;
  late int _currentIndex;

  @override
  void initState() {
    super.initState();
    _currentIndex = _findInitialIndex();
    _pageController = PageController(initialPage: _currentIndex);
    _focusNode = FocusNode();
  }

  int _findInitialIndex() {
    final index = widget.items.indexWhere(
          (item) => widget.idBuilder(item) == widget.initialItemId,
    );

    return index >= 0 ? index : 0;
  }

  bool get _hasPrevious => _currentIndex > 0;
  bool get _hasNext => _currentIndex < widget.items.length - 1;

  Future<void> _goToPage(int index) async {
    if (!_pageController.hasClients) return;
    if (index < 0 || index >= widget.items.length) return;

    await _pageController.animateToPage(
      index,
      duration: _pageAnimationDuration,
      curve: _pageAnimationCurve,
    );
  }

  Future<void> _showPrevious() => _goToPage(_currentIndex - 1);
  Future<void> _showNext() => _goToPage(_currentIndex + 1);

  KeyEventResult _handleKeyEvent(FocusNode node, KeyEvent event) {
    if (event is! KeyDownEvent) return KeyEventResult.ignored;

    if (event.logicalKey == LogicalKeyboardKey.arrowLeft) {
      if (_hasPrevious) {
        _showPrevious();
        return KeyEventResult.handled;
      }
    }

    if (event.logicalKey == LogicalKeyboardKey.arrowRight) {
      if (_hasNext) {
        _showNext();
        return KeyEventResult.handled;
      }
    }

    if (event.logicalKey == LogicalKeyboardKey.escape) {
      Navigator.of(context).pop();
      return KeyEventResult.handled;
    }

    return KeyEventResult.ignored;
  }

  @override
  void dispose() {
    _pageController.dispose();
    _focusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final hasItems = widget.items.isNotEmpty;
    final currentItem = hasItems ? widget.items[_currentIndex] : null;
    final title = currentItem != null && widget.titleBuilder != null
        ? widget.titleBuilder!(currentItem)
        : null;
    final isLargeScreen = MediaQuery.of(context).size.width >= _largeScreenBreakpoint;

    return Scaffold(
      backgroundColor: Colors.black,
      body: SafeArea(
        child: Focus(
          autofocus: true,
          focusNode: _focusNode,
          onKeyEvent: _handleKeyEvent,
          child: Stack(
            children: [
              if (hasItems)
                PageView.builder(
                  controller: _pageController,
                  itemCount: widget.items.length,
                  onPageChanged: (index) {
                    setState(() {
                      _currentIndex = index;
                    });
                  },
                  itemBuilder: (context, index) {
                    final item = widget.items[index];

                    return InteractiveViewer(
                      minScale: 1,
                      maxScale: 4,
                      child: Center(
                        child: Image(
                          image: widget.imageProviderBuilder(item),
                          fit: BoxFit.contain,
                        ),
                      ),
                    );
                  },
                )
              else
                const Center(
                  child: Text(
                    'No images found',
                    style: TextStyle(color: Colors.white),
                  ),
                ),
              Positioned(
                top: 8,
                right: 8,
                child: IconButton(
                  onPressed: widget.onEyePressed ?? () {},
                  icon: const Icon(Icons.remove_red_eye),
                  color: Colors.white,
                ),
              ),
              Positioned(
                top: 8,
                left: 8,
                child: IconButton(
                  onPressed: () => Navigator.of(context).pop(),
                  icon: const Icon(Icons.close),
                  color: Colors.white,
                ),
              ),
              if (isLargeScreen && widget.items.length > 1)
                Positioned(
                  left: 16,
                  top: 0,
                  bottom: 0,
                  child: Center(
                    child: AnimatedOpacity(
                      duration: const Duration(milliseconds: 200),
                      opacity: _hasPrevious ? 1 : 0.35,
                      child: _NavigationButton(
                        icon: Icons.chevron_left,
                        onPressed: _hasPrevious ? _showPrevious : null,
                      ),
                    ),
                  ),
                ),
              if (isLargeScreen && widget.items.length > 1)
                Positioned(
                  right: 16,
                  top: 0,
                  bottom: 0,
                  child: Center(
                    child: AnimatedOpacity(
                      duration: const Duration(milliseconds: 200),
                      opacity: _hasNext ? 1 : 0.35,
                      child: _NavigationButton(
                        icon: Icons.chevron_right,
                        onPressed: _hasNext ? _showNext : null,
                      ),
                    ),
                  ),
                ),
              if (title != null)
                Positioned(
                  left: 16,
                  right: 16,
                  bottom: 48,
                  child: Text(
                    title,
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              if (widget.items.length > 1)
                Positioned(
                  left: 0,
                  right: 0,
                  bottom: 16,
                  child: Text(
                    '${_currentIndex + 1} / ${widget.items.length}',
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      color: Colors.white70,
                      fontSize: 14,
                    ),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }
}

class _NavigationButton extends StatelessWidget {
  const _NavigationButton({
    required this.icon,
    required this.onPressed,
  });

  final IconData icon;
  final VoidCallback? onPressed;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.black45,
      shape: const CircleBorder(),
      child: IconButton(
        onPressed: onPressed,
        icon: Icon(icon, color: Colors.white),
        iconSize: 32,
        splashRadius: 28,
      ),
    );
  }
}
