import 'dart:convert';
import 'dart:io';

class SocketService {

  static Future<String> sendRequest(String jsonRequest) async {
    Socket? socket;
    try {
      // 10.0.2.2 برای شبیه‌ساز اندروید و localhost برای ویندوز/وب
      socket = await Socket.connect('localhost', 1234);

      // ارسال درخواست
      socket.write(jsonRequest);
      await socket.flush();

      // دریافت پاسخ (حل مشکل تبدیل Type)
      String response = await socket.map((data) => utf8.decode(data)).first;

      return response;
    } catch (e) {
      throw Exception("Socket connection failed: $e");
    } finally {
      socket?.destroy();
    }
  }
}