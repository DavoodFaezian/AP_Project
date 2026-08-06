import 'package:flutter/material.dart';
import '../../../../repositories/photo_repository.dart';
import '../../../../repositories/post_repository.dart';
import '../../../../viewmodels/post_list_view_model.dart';
import '../../../../repositories/album_repository.dart';
import '../../../features/photo/photo_form_page.dart';
import '../../../features/post/post_list.dart';
import '../../../components/widgets/custom_appbar.dart';
import '../../../components/widgets/custom_drawer.dart';
import '../../../components/widgets/custom_fab.dart';
import '../../../components/widgets/empty_screen.dart';
import 'me_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final PhotoRepository _photoRepository = PhotoRepository();
  final PostRepository _postRepository = PostRepository();
  final AlbumRepository _albumRepository = SocketAlbumRepository();
  
  late final PostListViewModel viewModel;

  @override
  void initState() {
    super.initState();
    viewModel = PostListViewModel(postRepository: _postRepository, isMe: false);
    viewModel.loadFeedPosts();
  }

  @override
  void dispose() {
    viewModel.dispose();
    super.dispose();
  }

  Future<void> _openCreatePhotoPage() async {
    await Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => PhotoFormPage(
          photoRepository: _photoRepository,
          albumRepository: _albumRepository,
          returnToAlbumTitle: 'Home',
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: viewModel,
      builder: (context, _) {
        return Scaffold(
          drawer: const CustomDrawer(),
          appBar: CustomAppBar(
            title: "Home",
            actions: [
              IconButton(
                onPressed: () => viewModel.loadFeedPosts(),
                icon: const Icon(Icons.refresh),
              ),
            ],
          ),
          body: PostList(
            posts: viewModel.posts,
            isLoading: viewModel.isLoading,
            photoRepository: _photoRepository,
            albumRepository: _albumRepository,
            postRepository: _postRepository,
            showActions: false,
            onRefresh: () => viewModel.loadFeedPosts(showLoading: false),
            onPostUpdated: (postId, ownerId) => viewModel.refreshSinglePost(postId, ownerId),
          ),
          floatingActionButton: CustomFAB(
            onPressed: _openCreatePhotoPage,
          ),
        );
      },
    );
  }
}

