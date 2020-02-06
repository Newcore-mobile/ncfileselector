import 'package:flutter/material.dart';
import 'package:ncfilepicker/nc_file_picker.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String result;

  @override
  void initState() {
    super.initState();
  }

  void openFile() async {
    List<NCFileInfo> docPaths;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      docPaths = await NcFilePicker.pickFile(maxCount: 4);
      print(docPaths);
    } catch(e) {
      print("error:$e");
      docPaths = [];
    }
    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.

    if (!mounted) return;
    setState(() {
      result = 'result:${docPaths.toString()}';
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              FlatButton(
                child: Text("选择文件"),
                onPressed: () {
                  openFile();
                },
              ),
              Text(result ?? "")
            ],
          )
        ),
      ),
    );
  }
}
