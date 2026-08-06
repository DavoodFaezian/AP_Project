import 'dart:async';
import 'dart:convert';
import 'dart:io';

class SocketService {
  static const String _host = '192.168.56.1'; // '10.0.2.2' for Android Emulator
  static const int _port = 1234;
  static const Duration _timeout = Duration(seconds: 30);

  static Future<String> sendRequest(String jsonRequest) async {
    Socket? socket;

    try {
      socket = await Socket.connect(
        _host,
        _port,
        timeout: const Duration(seconds: 10),
      );

      // Append newline delimiter required by Java reader.readLine()
      final request = jsonRequest.endsWith('\n')
          ? jsonRequest
          : '$jsonRequest\n';

      socket.add(utf8.encode(request));
      await socket.flush();

      // Fix: Use .cast<List<int>>() to match Utf8Decoder's expected input type
      final response = await socket
          .cast<List<int>>()
          .transform(utf8.decoder)
          .transform(const LineSplitter())
          .first
          .timeout(
        _timeout,
        onTimeout: () => throw TimeoutException(
          'The server did not return a response within ${_timeout.inSeconds} seconds.',
        ),
      );

      if (response.trim().isEmpty) {
        throw const FormatException('The server returned an empty response.');
      }

      return response;
    } on SocketException catch (e) {
      throw Exception('Socket connection failed: ${e.message}');
    } on TimeoutException catch (e) {
      throw Exception('Socket request timed out: ${e.message}');
    } on FormatException catch (e) {
      throw Exception('Invalid server response: ${e.message}');
    } catch (e) {
      throw Exception('Socket request failed: $e');
    } finally {
      socket?.destroy();
    }
  }
}
