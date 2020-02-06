import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

class NcFilePicker {
  static const MethodChannel _channel =
      const MethodChannel('plugin.newcore/nc_file_picker');

  static Future<List<NCFileInfo>> pickFile({int maxCount = 1}) async {
    if(Platform.isIOS) {
      return Future.error('暂不支持iOS系统');
    }
    var arguments = {'maxCount': maxCount};
    bool success = true;
    String error = '';
    List<dynamic> docs = await _channel.invokeMethod('pickFile',arguments).catchError((error) {
      success = false;
      error = '未选择文件';
    });
    if(success) {
      var result = docs.map((value) {
        final Map<dynamic,dynamic> map = value;
        return NCFileInfo(name:map['name'], path:map['path'], mimeType:map['mimeType'],size: map['size']);
      }).toList();
      return Future.value(result);
    } else {
      return Future.error(error);
    }
  }
}

class NCFileInfo {
  final String name;
  final String path;
  final String mimeType;
  final int size;

  NCFileInfo({this.name, this.path, this.mimeType,this.size});

  @override
  String toString() {
    return 'NCFileInfo{name: $name, path: $path, mimeType: $mimeType,size: $size}\n';
  }
}
