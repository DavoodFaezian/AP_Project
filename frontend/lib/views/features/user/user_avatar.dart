import 'package:flutter/material.dart';
import '../../../models/user.dart';
import '../../../repositories/user_repository.dart';

class UserAvatar extends StatelessWidget {
  const UserAvatar({
    super.key,
    required this.userId,
    required this.userRepository,
    this.radius = 20,
  });

  final String userId;
  final UserRepository userRepository;
  final double radius;

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<User?>(
      // بر اساس متد جدید userRepository
      future: userRepository.getUserById(userId), 
      builder: (context, snapshot) {
        final user = snapshot.data;
        final isLoading = snapshot.connectionState == ConnectionState.waiting;
        
        final profilePhotoId = user?.profilePhotoId;
        
        // استخراج حرف اول نام کاربری جهت نمایش در صورت نداشتن عکس
        final initial = (user != null && user.username.isNotEmpty)
            ? user.username[0].toUpperCase()
            : '?';

        if (isLoading) {
          return CircleAvatar(
            radius: radius,
            backgroundColor: Colors.grey.shade300,
            child: SizedBox(
              width: radius,
              height: radius,
              child: const CircularProgressIndicator(
                strokeWidth: 2,
                valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
              ),
            ),
          );
        }

        // اگر آیدی عکس پروفایل وجود داشت
        if (profilePhotoId != null && profilePhotoId.isNotEmpty) {
          return CircleAvatar(
            radius: radius,
            // آدرس یا ای پی سرور برای دریافت فایل تصویر بر اساس profilePhotoId
            backgroundImage: NetworkImage('http://YOUR_SERVER_IP:PORT/files/$profilePhotoId'),
            backgroundColor: Colors.grey.shade200,
          );
        }

        // نمایش حرف اول نام کاربری در صورت عدم وجود عکس پروفایل
        return CircleAvatar(
          radius: radius,
          backgroundColor: Theme.of(context).primaryColor,
          child: Text(
            initial,
            style: TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: radius * 0.9,
            ),
          ),
        );
      },
    );
  }
}