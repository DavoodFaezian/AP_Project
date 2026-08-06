import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:test_app/models/user_profile.dart';
import 'package:test_app/repositories/album_repository.dart';
import 'package:test_app/repositories/photo_repository.dart';
import 'package:test_app/repositories/post_repository.dart';
import 'package:test_app/repositories/user_repository.dart';
import 'package:test_app/services/session_manager.dart';
import 'package:test_app/viewmodels/post_list_view_model.dart';
import 'package:test_app/views/components/widgets/custom_appbar.dart';
import 'package:test_app/views/components/widgets/custom_drawer.dart';
import 'package:test_app/views/components/widgets/socket_image.dart';
import 'package:test_app/views/features/post/post_form_page.dart';
import 'package:test_app/views/features/post/post_list.dart';
import 'package:test_app/views/layout/screens/settings/setting_screen.dart';

import '../../components/widgets/custom_fab.dart';

class ProfileScreen extends StatefulWidget {
  final String? userId;
  final bool isMePage;

  const ProfileScreen({super.key, this.userId, this.isMePage = false});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  final UserRepository _userRepository = UserRepository();
  final PostRepository _postRepository = PostRepository();
  final PhotoRepository _photoRepository = PhotoRepository();
  final AlbumRepository _albumRepository = SocketAlbumRepository();

  UserProfile? _profile;
  late final PostListViewModel _postViewModel;
  bool _isLoadingProfile = true;
  bool _isFollowing = false;
  bool _isProcessingFollow = false;

  bool get isMe => widget.userId == null || widget.userId == SessionManager.instance.userId;

  @override
  void initState() {
    super.initState();
    _postViewModel = PostListViewModel(
      postRepository: _postRepository,
      isMe: isMe,
      targetUserId: isMe ? null : widget.userId,
    );
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoadingProfile = true);
    try {
      if (isMe) {
        _profile = await _userRepository.getUserProfile();
        // Update global context
        await SessionManager.instance.refreshProfile();
      } else {
        _profile = await _userRepository.getUserProfileById(widget.userId!);
        _isFollowing = await _userRepository.checkIsFollowing(widget.userId!);
      }
      await _postViewModel.loadPosts();
    } catch (e) {
      debugPrint("Error loading profile data: $e");
    } finally {
      if (mounted) setState(() => _isLoadingProfile = false);
    }
  }

  Future<void> _toggleFollow() async {
    if (_isProcessingFollow) return;

    setState(() => _isProcessingFollow = true);
    try {
      if (_isFollowing) {
        await _userRepository.unfollowUser(widget.userId!);
      } else {
        await _userRepository.followUser(widget.userId!);
      }
      // Re-fetch profile to update follower count and status
      _profile = await _userRepository.getUserProfileById(widget.userId!);
      _isFollowing = await _userRepository.checkIsFollowing(widget.userId!);
      
      // Update global context (our following count changed)
      await SessionManager.instance.refreshProfile();
    } catch (e) {
      debugPrint("Follow action failed: $e");
    } finally {
      if (mounted) setState(() => _isProcessingFollow = false);
    }
  }

  void _openAddPost() async {
    final result = await Navigator.push<bool>(
      context,
      MaterialPageRoute(
        builder: (context) => PostFormPage(
          photoRepository: _photoRepository,
          albumRepository: _albumRepository,
          postRepository: _postRepository,
        ),
      ),
    );

    if (result == true) {
      _postViewModel.loadPosts();
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoadingProfile) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    if (_profile == null) {
      return Scaffold(
        appBar: AppBar(),
        body: const Center(child: Text("Profile not found")),
      );
    }

    return Scaffold(
      drawer: const CustomDrawer(),
      appBar: widget.isMePage
          ? CustomAppBar(title: _profile!.userName)
          : CustomAppBar(
              title: _profile!.userName,
              leading: IconButton(
                  icon: const Icon(Icons.arrow_back),
                  onPressed: () => Navigator.pop(context)),
            ),
      body: AnimatedBuilder(
        animation: _postViewModel,
        builder: (context, _) {
          return NestedScrollView(
            headerSliverBuilder: (context, innerBoxIsScrolled) => [
              SliverToBoxAdapter(
                child: _buildProfileHeader(),
              ),
            ],
            body: PostList(
              posts: _postViewModel.posts,
              isLoading: _postViewModel.isLoading,
              photoRepository: _photoRepository,
              albumRepository: _albumRepository,
              postRepository: _postRepository,
              showActions: isMe,
              onRefresh: () => _postViewModel.loadPosts(showLoading: false),
              onPostUpdated: (postId, ownerId) =>
                  _postViewModel.refreshSinglePost(postId, ownerId),
            ),
          );
        },
      ),
      floatingActionButton: isMe ? CustomFAB(onPressed: _openAddPost) : null,
    );
  }

  Widget _buildProfileHeader() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 24, 16, 0),
          child: Row(
            children: [
              // Profile Photo
              Hero(
                tag: 'profile_avatar_${_profile!.userId}',
                child: Container(
                  padding: const EdgeInsets.all(3),
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    gradient: LinearGradient(
                      colors: [const Color(0xFF5B21B6), const Color(0xFF5B21B6).withOpacity(0.5)],
                    ),
                  ),
                  child: CircleAvatar(
                    radius: 40,
                    backgroundColor: Colors.white,
                    child: Padding(
                      padding: const EdgeInsets.all(2),
                      child: ClipOval(
                        child: _profile!.profilePhotoName != null
                            ? SocketImage(
                                photoName: _profile!.profilePhotoName!,
                                ownerId: _profile!.userId,
                                sessionId: SessionManager.instance.sessionId!,
                                builder: (context, provider) => Image(
                                  image: provider,
                                  fit: BoxFit.cover,
                                  width: 80,
                                  height: 80,
                                ),
                              )
                            : const Icon(Icons.person, size: 40, color: Colors.grey),
                      ),
                    ),
                  ),
                ),
              ),
              const SizedBox(width: 24),
              // Stats
              Expanded(
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    _buildStatColumn(_postViewModel.posts.length.toString(), "Posts").animate().fadeIn(delay: 200.ms).scale(),
                    _buildStatColumn(_profile!.followerIds.length.toString(), "Followers").animate().fadeIn(delay: 300.ms).scale(),
                    _buildStatColumn(_profile!.followingIds.length.toString(), "Following").animate().fadeIn(delay: 400.ms).scale(),
                  ],
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: 16),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                _profile!.userName,
                style: const TextStyle(
                  fontWeight: FontWeight.bold,
                  fontSize: 18,
                ),
              ),

            ],
          ),
        ),
        const SizedBox(height: 20),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: Row(
            children: [
              Expanded(
                child: isMe
                    ? ElevatedButton(
                        onPressed: () {
                          Navigator.of(context).push(
                            MaterialPageRoute(
                              builder: (context) => const SettingsScreen(),
                            ),
                          );
                        },
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFFF3E8FF),
                          foregroundColor: const Color(0xFF5B21B6),
                          elevation: 0,
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                        ),
                        child: const Text("Edit Profile", style: TextStyle(fontWeight: FontWeight.bold)),
                      )
                    : ElevatedButton(
                        onPressed: _toggleFollow,
                        style: ElevatedButton.styleFrom(
                          backgroundColor: _isFollowing ? Colors.grey.shade200 : const Color(0xFF5B21B6),
                          foregroundColor: _isFollowing ? Colors.black87 : Colors.white,
                          elevation: 0,
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                        ),
                        child: _isProcessingFollow
                            ? const SizedBox(
                                height: 16,
                                width: 16,
                                child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                              )
                            : Text(_isFollowing ? "Following" : "Follow", style: const TextStyle(fontWeight: FontWeight.bold)),
                      ),
              ),
              if (!isMe) ...[
                const SizedBox(width: 8),
                Expanded(
                  child: OutlinedButton(
                    onPressed: () {},
                    style: OutlinedButton.styleFrom(
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                      side: BorderSide(color: Colors.grey.shade300),
                    ),
                    child: const Text("Message", style: TextStyle(color: Colors.black, fontWeight: FontWeight.bold)),
                  ),
                ),
              ],
            ],
          ),
        ),
        const SizedBox(height: 20),
        const Divider(height: 1, thickness: 0.5),
      ],
    ).animate().fadeIn().slideY(begin: 0.05, end: 0);
  }

  Widget _buildStatColumn(String count, String label) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Text(count, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
        Text(label, style: const TextStyle(fontSize: 12, color: Colors.grey)),
      ],
    );
  }
}
