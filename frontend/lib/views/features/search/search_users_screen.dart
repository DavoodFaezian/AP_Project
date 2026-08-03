import 'package:flutter/material.dart';

// وارد کردن کامپوننت‌های قبلی پروژه
import '../../components/widgets/custom_appbar.dart';
import '../../components/widgets/custom_drawer.dart';
import '../../components/widgets/empty_screen.dart';
import '../../components/widgets/input_decoration.dart';

class SearchUserPage extends StatefulWidget {
  const SearchUserPage({super.key});

  @override
  State<SearchUserPage> createState() => _SearchUserPageState();
}

class _SearchUserPageState extends State<SearchUserPage> {
  final TextEditingController _searchController = TextEditingController();
  
  // لیست فرضی نتایج جستجو (در پروژه واقعی از ViewModel یا Repository پر می‌شود)
  List<Map<String, String>> _searchResults = [];
  bool _isLoading = false;
  bool _hasSearched = false;

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  // متد اجرای جستجو
  Future<void> _performSearch(String query) async {
    final trimmedQuery = query.trim();
    if (trimmedQuery.isEmpty) {
      setState(() {
        _searchResults = [];
        _hasSearched = false;
      });
      return;
    }

    setState(() {
      _isLoading = true;
      _hasSearched = true;
    });

    // TODO: صدا زدن متد سرچ از ریپازیتوری با sessionId (مثلاً: UserRepository.searchUsers(query))
    await Future.delayed(const Duration(milliseconds: 600)); // شبیه‌سازی ریکوئست شبکه

    setState(() {
      _isLoading = false;
      // نمونه دیتای تستی
      _searchResults = [
        {'username': 'ali_dev', 'name': 'Ali Rezaei'},
        {'username': 'sara_m', 'name': 'Sara Mohammadi'},
      ].where((user) =>
          user['username']!.toLowerCase().contains(trimmedQuery.toLowerCase()) ||
          user['name']!.toLowerCase().contains(trimmedQuery.toLowerCase())).toList();
    });
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
          // فیلد جستجو در بالای صفحه
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: TextField(
              controller: _searchController,
              onChanged: _performSearch,
              decoration: buildInputDecoration(
                'Search username or name...',
                suffixIcon: _searchController.text.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear),
                        onPressed: () {
                          _searchController.clear();
                          _performSearch('');
                        },
                      )
                    : const Icon(Icons.search),
              ),
            ),
          ),

          // بخش نمایش نتایج یا وضعیت EmptyState
          Expanded(
            child: Builder(
              builder: (context) {
                if (_isLoading) {
                  return const Center(child: CircularProgressIndicator());
                }

                // اگر هنوز جستجویی انجام نشده است
                if (!_hasSearched) {
                  return const EmptyState(
                    imagePath: 'assets/images/Image post-cuate.png',
                    title: 'Search for Users',
                    subtitle: 'Type a username above to start searching.',
                  );
                }

                // اگر جستجو انجام شد ولی نتیجه‌ای نداشت
                if (_searchResults.isEmpty) {
                  return const EmptyState(
                    imagePath: 'assets/images/Image post-cuate.png',
                    title: 'No Users Found',
                    subtitle: 'Try searching with a different keyword.',
                  );
                }

                // نمایش لیست کاربران پیدا شده
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
                          user['name']!,
                          style: const TextStyle(fontWeight: FontWeight.bold),
                        ),
                        subtitle: Text('@${user['username']}'),
                        trailing: IconButton(
                          icon: const Icon(Icons.arrow_forward_ios, size: 16),
                          onPressed: () {
                            // رفتن به صفحه پروفایل کاربر انتخاب‌شده
                          },
                        ),
                        onTap: () {
                          // رفتن به صفحه پروفایل کاربر
                        },
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