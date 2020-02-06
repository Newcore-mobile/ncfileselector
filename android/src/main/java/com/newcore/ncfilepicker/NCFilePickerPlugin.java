package com.newcore.ncfilepicker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.FilePickerUtils;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * NCFilePickerPlugin
 */
public class NCFilePickerPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {
    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "plugin.newcore/nc_file_picker");
        NCFilePickerPlugin plugin = new NCFilePickerPlugin(registrar.activity());
        registrar.addActivityResultListener(plugin);
        registrar.addRequestPermissionsResultListener(plugin);
        channel.setMethodCallHandler(plugin);
    }

    private Activity mActivity;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Integer maxCount = 1;
    private Result mPendingResult;

    private NCFilePickerPlugin(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("pickFile")) {
            Integer maxCount = call.argument("maxCount");
            if (maxCount == null) {
                maxCount = 1;
            }
            this.mPendingResult = result;
            pickDoc(maxCount);
        } else {
            result.notImplemented();
        }
    }

    private void pickDoc(int maxCount) {
        if (ContextCompat.checkSelfPermission(this.mActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            this.maxCount = maxCount;
            ActivityCompat.requestPermissions(this.mActivity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            onPickDoc(maxCount);
        }
    }

    private void onPickDoc(int maxCount) {
        FilePickerBuilder.getInstance().setMaxCount(maxCount)
                .setActivityTheme(R.style.LibAppTheme)
                .pickFile(this.mActivity);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FilePickerConst.REQUEST_CODE_DOC) {
            if(mPendingResult == null) return false;
            if(resultCode== Activity.RESULT_OK && data!=null)
            {
                ArrayList<String> docPaths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                if(docPaths == null || docPaths.isEmpty()) {
                    mPendingResult.error("not select file",null,null);
                } else {
                    List<Map<String,Object>> docList = new ArrayList<>();
                    for(String path:docPaths) {
                        Map<String,Object> map = new HashMap<>();
                        File docFile = new File(path);
                        map.put("path",path);
                        map.put("mimeType",FilePickerUtils.getFileExtension(docFile));
                        map.put("name",docFile.getName());
                        map.put("size",docFile.length());
                        docList.add(map);
                    }
                    mPendingResult.success(docList);
                }
            } else {
                mPendingResult.error("not select file",null,null);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPickDoc(this.maxCount);
            } else {
                new AlertDialog.Builder(this.mActivity)
                        .setTitle("提示")
                        .setMessage("App需要一些权限，请前往设置里授予该权限")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
                                mActivity.startActivityForResult(intent,PERMISSION_REQUEST_CODE);
                            }
                        });
                return false;
            }
            return true;
        }
        return false;
    }
}











