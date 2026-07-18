import '../models/app_theme.dart';
import '../models/user.dart';

class UserRepository {
  UserRepository();

  final Map<String, User> _users = {
    'user1': const User(
      id: 'user1',
      userName: 'alice',
      password: 'alice123',
      photoIds: {'photo1', 'photo2'},
      albumIds: {'album1'},
      sharedPhotoIds: {'photo9'},
      theme: AppTheme.light,
      profilePictureUrl: 'https://example.com/alice.jpg',
    ),
    'user2': const User(
      id: 'user2',
      userName: 'bob',
      password: 'bob123',
      photoIds: {'photo3'},
      albumIds: {'album2', 'album3'},
      sharedPhotoIds: {},
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
