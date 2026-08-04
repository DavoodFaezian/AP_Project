import 'package:flutter/material.dart';
import 'package:test_app/repositories/album_repository.dart';
import 'package:test_app/repositories/photo_repository.dart';
import 'package:test_app/repositories/post_repository.dart';
import 'package:test_app/viewmodels/post_list_view_model.dart';
import 'package:test_app/views/components/widgets/custom_appbar.dart';
import 'package:test_app/views/components/widgets/custom_fab.dart';
import 'package:test_app/views/features/post/post_list.dart';
import 'package:test_app/views/features/post/post_form_popup.dart';

class MeScreen extends StatefulWidget {
  const MeScreen({super.key});

  @override
  State<MeScreen> createState() => _MeScreenState();
}

class _MeScreenState extends State<MeScreen> {
  final PostRepository _postRepository = PostRepository();
  final PhotoRepository _photoRepository = PhotoRepository();
  final AlbumRepository _albumRepository = SocketAlbumRepository();
  
  late final PostListViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = PostListViewModel(postRepository: _postRepository, isMe: true);
    _viewModel.loadPosts();
  }

  @override
  void dispose() {
    _viewModel.dispose();
    super.dispose();
  }

  void _openAddPost() async {
    final result = await showDialog<bool>(
      context: context,
      builder: (context) => PostFormPopup(
        photoRepository: _photoRepository,
        albumRepository: _albumRepository,
        postRepository: _postRepository,
      ),
    );

    if (result == true) {
      _viewModel.loadPosts();
    }
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _viewModel,
      builder: (context, _) {
        return Scaffold(
          appBar: const CustomAppBar(
            title: "My Posts",
          ),
          body: PostList(
            posts: _viewModel.posts,
            isLoading: _viewModel.isLoading,
            photoRepository: _photoRepository,
            albumRepository: _albumRepository,
            postRepository: _postRepository,
            showActions: true,
            onRefresh: () => _viewModel.loadPosts(),
          ),
          floatingActionButton: CustomFAB(
            onPressed: _openAddPost,
          ),
        );
      },
    );
  }
}
