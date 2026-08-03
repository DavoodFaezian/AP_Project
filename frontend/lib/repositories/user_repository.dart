import '../models/app_theme.dart';
import '../models/user.dart';

class UserRepository {
  UserRepository();

  final Map<String, User> _users = {
    'user1': const User(
      id: 'user1',
      userName: 'alice',
      password: 'alice123',
      followerIds: {'photo1', 'photo2'},
      followingIds: {'album1'},
      theme: AppTheme.light,
      profilePictureUrl: 'https://example.com/alice.jpg',
    ),
    'user2': const User(
      id: 'user2',
      userName: 'bob',
      password: 'bob123',
      followerIds: {'photo3'},
      followingIds: {'album2', 'album3'},
      theme: AppTheme.dark,
    ),
  };

  Future<User?> getUserById(String id) async {
    await Future.delayed(const Duration(milliseconds: 300));
    return _users[id];
  }

  Future<List<User>> getAllUsers() async {
    await Future.delayed(const Duration(milliseconds: 300));
    return _users.values.toList();
  }

  Future<void> addUser(User user) async {
    _users[user.id] = user;
  }

  Future<void> updateUser(User user) async {
    if (_users.containsKey(user.id)) {
      _users[user.id] = user;
    }
  }

  Future<void> deleteUser(String id) async {
    _users.remove(id);
  }
}
