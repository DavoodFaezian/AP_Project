import 'package:flutter/material.dart';
import '../../../components/widgets/custom_appbar.dart';

class AboutScreen extends StatelessWidget {
  const AboutScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(
        title: "About",
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => Navigator.of(context).pop(),
        ),
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            const SizedBox(height: 40),
            // App Logo / Icon
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: const Color(0xFF5B21B6).withOpacity(0.1),
              ),
              child: const Icon(
                Icons.camera_rounded,
                size: 80,
                color: Color(0xFF5B21B6),
              ),
            ),
            const SizedBox(height: 24),
            const Text(
              "PhotoShare",
              style: TextStyle(
                fontSize: 28,
                fontWeight: FontWeight.bold,
                color: Color(0xFF1E1B4B),
              ),
            ),
            const Text(
              "Version 1.0.0",
              style: TextStyle(
                color: Colors.grey,
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 32),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    "The App",
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF1E1B4B),
                    ),
                  ),
                  const SizedBox(height: 12),
                  Text(
                    "PhotoShare is a social media platform designed for photography enthusiasts. "
                    "Share your best moments, organize them into albums, and connect with other creators around the world.",
                    style: TextStyle(
                      fontSize: 15,
                      color: Colors.grey.shade700,
                      height: 1.6,
                    ),
                  ),
                  const SizedBox(height: 32),
                  const Text(
                    "Key Features",
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFF1E1B4B),
                    ),
                  ),
                  const SizedBox(height: 16),
                  _buildFeatureItem(Icons.photo_library_rounded, "Advanced Album Management"),
                  _buildFeatureItem(Icons.search_rounded, "Global Photo & User Search"),
                  _buildFeatureItem(Icons.chat_bubble_outline_rounded, "Real-time Comments"),
                  _buildFeatureItem(Icons.person_add_alt_1_rounded, "Social Connections"),
                  _buildFeatureItem(Icons.security_rounded, "Secure Socket Connection"),
                  const SizedBox(height: 40),
                  const Divider(),
                  const SizedBox(height: 24),
                  Center(
                    child: Column(
                      children: [
                        const Text(
                          "Connect with us",
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            color: Color(0xFF1E1B4B),
                          ),
                        ),
                        const SizedBox(height: 16),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            _buildSocialIcon(Icons.language),
                            const SizedBox(width: 20),
                            _buildSocialIcon(Icons.email_outlined),
                            const SizedBox(width: 20),
                            _buildSocialIcon(Icons.info_outline),
                          ],
                        ),
                        const SizedBox(height: 40),
                        Text(
                          "Made with ❤️ using Flutter",
                          style: TextStyle(
                            color: Colors.grey.shade500,
                            fontSize: 12,
                          ),
                        ),
                        const SizedBox(height: 20),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildFeatureItem(IconData icon, String text) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: const Color(0xFFF3E8FF),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Icon(icon, size: 20, color: const Color(0xFF5B21B6)),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Text(
              text,
              style: const TextStyle(
                fontSize: 15,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSocialIcon(IconData icon) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        shape: BoxShape.circle,
        border: Border.all(color: Colors.grey.shade200),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.03),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Icon(icon, color: const Color(0xFF5B21B6), size: 24),
    );
  }
}
