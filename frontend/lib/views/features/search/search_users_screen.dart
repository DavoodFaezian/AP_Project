import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import '../../../services/socket_service.dart';
import '../../../services/session_manager.dart';
import '../../components/widgets/custom_appbar.dart';
import '../../components/widgets/custom_drawer.dart';
import '../../components/widgets/empty_screen.dart';
import '../../components/widgets/input_decoration.dart';

// صفحه نمایش پست‌های کاربر کلیک‌شده
import '../post/user_posts_page.dart';

class SearchUserPage extends StatefulWidget {
  const SearchUserPage({super.key});

  @override
  State<SearchUserPage> createState() => _SearchUserPageState();
}

class _SearchUserPageState extends State<SearchUserPage> {
  final TextEditingController _searchController = TextEditingController();
  
  // تایمر برای اعمال Debounce
  Timer? _debounceTimer;

  List<Map<String, dynamic>> _searchResults = [];
  bool _isLoading = false;
  bool _hasSearched = false;

  @override
  void dispose() {
    _debounceTimer?.cancel(); // ابطال تایمر هنگام خروج از صفحه
    _searchController.dispose();
    super.dispose();
  }

  // ۱. متد مدیریت تغییرات متن تایپ‌شده همراه با ۵۰۰ میلی‌ثانیه تاخیر هوشمند (Debounce)
  void _onSearchChanged(String query) {
    if (_debounceTimer?.isActive ?? false) {
      _debounceTimer!.cancel();
    }

    _debounceTimer = Timer(const Duration(milliseconds: 500), () {
      _performSearch(query);
    });
  }

  // ۲. ارسال درخواست سرچ به بک‌اند جاوا
  Future<void> _performSearch(String query) async {
    final trimmedQuery = query.trim();

    if (trimmedQuery.isEmpty) {
      setState(() {
        _searchResults = [];
        _hasSearched = false;
        _isLoading = false;
      });
      return;
    }

    setState(() {
      _isLoading = true;
      _hasSearched = true;
    });

    try {
      // نمونه فرمت ارسالی به سوکت بک‌اند جاوا:
      final requestMap = {
        "actionName": "SEARCH_USERS",
        "payload": {
          "sessionId": SessionManager.instance.sessionId,
          "query": trimmedQuery,
        }
      };

      String jsonRequest = jsonEncode(requestMap) + "\n";
      String rawResponse = await SocketService.sendRequest(jsonRequest);
      Map<String, dynamic> responseMap = jsonDecode(rawResponse);

      if (responseMap['status'] == 'SUCCESS') {
        setState(() {
          _searchResults = List<Map<String, dynamic>>.from(responseMap['users'] ?? []);
          _isLoading = false;
        });
      }


      // شبیه‌سازی تاخیر پاسخ سوکت برای تست
      await Future.delayed(const Duration(milliseconds: 400));

      if (!mounted) return;

      setState(() {
        _isLoading = false;
        // دیتای تستی فرضی دریافت شده از بک‌اند
        _searchResults = [
          {
            'userId': 'user_101',
            'username': 'ali_dev',
            'fullName': 'Ali Rezaei',
          },
          {
            'userId': 'user_102',
            'username': 'sara_m',
            'fullName': 'Sara Mohammadi',
          },
        ];
      });
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _isLoading = false;
      });
    }
  }

  // ۳. رفتن به صفحه پست‌های کاربر انتخاب‌شده
  void _navigateToUserPosts(Map<String, dynamic> user) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => UserPostsPage(
          userId: user['userId'] as String,
          username: user['username'] as String,
          fullName: user['fullName'] as String,
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(
        title: 'Search Users',
      ),
      drawer: const CustomDrawer(),
      body: Column(
        children: [
          // فیلد سرچ
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: TextField(
              controller: _searchController,
              onChanged: _onSearchChanged, // اتصال به Debounce
              decoration: buildInputDecoration(
                'Search username or name...',
                suffixIcon: _searchController.text.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear),
                        onPressed: () {
                          _searchController.clear();
                          _onSearchChanged('');
                        },
                      )
                    : const Icon(Icons.search),
              ),
            ),
          ),

          // بخش نتایج یا اسکرین‌های خالی
          Expanded(
            child: Builder(
              builder: (context) {
                if (_isLoading) {
                  return const Center(child: CircularProgressIndicator());
                }

                if (!_hasSearched) {
                  return const EmptyState(
                    imagePath: 'assets/images/Image post-cuate.png',
                    title: 'Search for Users',
                    subtitle: 'Start typing to search users in real-time.',
                  );
                }

                if (_searchResults.isEmpty) {
                  return const EmptyState(
                    imagePath: 'assets/images/Image post-cuate.png',
                    title: 'No Users Found',
                    subtitle: 'No user matches your search query.',
                  );
                }

                return ListView.builder(
                  itemCount: _searchResults.length,
                  padding: const EdgeInsets.symmetric(horizontal: 8),
                  itemBuilder: (context, index) {
                    final user = _searchResults[index];
                    return Card(
                      elevation: 1,
                      margin: const EdgeInsets.symmetric(vertical: 4, horizontal: 8),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: ListTile(
                        leading: const CircleAvatar(
                          child: Icon(Icons.person),
                        ),
                        title: Text(
                          user['fullName'] ?? '',
                          style: const TextStyle(fontWeight: FontWeight.bold),
                        ),
                        subtitle: Text('@${user['username']}'),
                        trailing: const Icon(Icons.chevron_right),
                        onTap: () => _navigateToUserPosts(user),
                      ),
                    );
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}