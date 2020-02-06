import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:ncfilepicker/nc_file_picker.dart';

void main() {
  const MethodChannel channel = MethodChannel('plugin.newcore/nc_file_picker');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('pickFile', () async {
    expect(NcFilePicker.pickFile, '42');
  });
}
