package com.sxt.chat.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.steelkiwi.cropiwa.util.UriUtil;
import com.sxt.chat.R;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.dialog.AlertDialogBuilder;
import com.sxt.chat.json.OCRObject;
import com.sxt.chat.utils.glide.GlideRoundTransformer;
import com.sxt.chat.youtu.OCRListener;
import com.sxt.chat.youtu.OCRTask;
import com.sxt.chat.youtu.SDKConfig;

import java.io.File;

/**
 * Created by 11837 on 2018/6/5.
 */

public class YouTuActivity extends HeaderActivity {

    private final int REQUEST_CODE_GALLERY = 100;
    private final int REQUEST_CODE_CAMARER = 101;
    private final int REQUEST_CROP_PHOTO = 102;
    private final int REQUEST_CODE_TAKE_PHOTO = 103;
    private final int MAX_LENGTH = 2 * 1024 * 1024;
    private TextView result;
    private RadioGroup rgCardType;
    private OCRTask ocrTask;
    private File file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youto);
        setTitle(R.string.ocr_scan);
        result = findViewById(R.id.result);
        rgCardType = findViewById(R.id.radio_group_card_type);
    }

    private void startCardOCR(final String imgPath) {
        File file = new File(imgPath);
        if (file.exists()) {
            Glide.with(this).load(file)/*.override(320, 198)*/
                    .transform(new CenterCrop(this), new GlideRoundTransformer(this, 4))
                    /*.bitmapTransform(new GlideRoundTransformer(this, 8))*/
                    .into(((ImageView) findViewById(R.id.img)));

            if (ocrTask != null && !ocrTask.isCancelled()) {
                ocrTask.cancel(true);
            }
            ocrTask = new OCRTask(imgPath, getCardType(), 0, new OCRListener() {
                @Override
                public void onStart() {
                    showDialog();
                }

                @Override
                public void onFaied(final Exception e) {
                    dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            result.setText(e.getMessage());
                        }
                    });
                }

                @Override
                public void onSuccess(OCRObject ocrResult, String result) {
                    dismiss();
                    YouTuActivity.this.result.setText(result);
                }
            });
            ocrTask.execute();
        }
    }

    private void showDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading.show();
            }
        });
    }

    public void dismiss() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loading != null && loading.isShowing()) {
                    loading.dismiss();
                }
            }
        });
    }

    public void startGallery(View view) {
        boolean b = checkPermission(REQUEST_CODE_GALLERY, Manifest.permission_group.STORAGE, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
        if (b) {
            startGalleryApp();
        }
    }

    public void startCamera(View view) {
        boolean b = checkPermission(REQUEST_CODE_CAMARER, Manifest.permission_group.CAMERA, new String[]{Manifest.permission.CAMERA});
        if (b) {
            startCameraApp();
        }
    }

    private void startCameraApp() {
        startActivityForResult(new Intent(this, TakePhotoActivity.class), REQUEST_CODE_TAKE_PHOTO);

//        调用系统相机拍照
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        file = new File(this.getExternalCacheDir() + File.separator + System.currentTimeMillis() + ".png");
//
//        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
//        //添加权限
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(intent, REQUEST_CODE_CAMARER);
    }

    private void startGalleryApp() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }

        intent = Intent.createChooser(intent, "选择图片");
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    private void startCropActivity(File file) {
        //在手机相册中显示刚拍摄的图片
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);

        Intent intent = new Intent(this, CropActivity.class);
        intent.putExtra(CropActivity.CROP_IMG_URI, contentUri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                if (data != null && data.getData() != null) {
                    String path = UriUtil.uri2Path(this, data.getData());
                    if (path != null) {
                        File file = new File(path);
                        if (file.exists()) {
                            if (file.length() > MAX_LENGTH) {
                                startCropActivity(file);
                            } else {
                                startCardOCR(path);
                            }
                        }
                    }
                }
            } else if (requestCode == REQUEST_CODE_CAMARER) {
                startCropActivity(file);
            } else if (requestCode == REQUEST_CROP_PHOTO) {
                if (data != null) {
                    Uri uri = data.getParcelableExtra(CropActivity.CROP_IMG_URI);
                    if (uri != null) {
                        startCardOCR(UriUtil.uri2Path(this, uri));
                    }
                }
            } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                if (data != null) {
                    Uri uri = data.getParcelableExtra(CropActivity.CROP_IMG_URI);
                    if (uri != null) {
                        File file = new File(uri.getPath());
                        if (file.length() > MAX_LENGTH) {
                            startCropActivity(file);
                        } else {
                            startCardOCR(file.getPath());
                        }
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onPermissionsAllowed(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsAllowed(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_CAMARER:
                startCameraApp();
                break;
            case REQUEST_CODE_GALLERY:
                startGalleryApp();
                break;
        }
    }

    @Override
    public void onPermissionsRefusedNever(int requestCode, String[] permissions, int[] grantResults) {
        super.onPermissionsRefusedNever(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_CAMARER:
                onPermissionRefuseNever(R.string.permission_request_CAMERA);
                break;
            case REQUEST_CODE_GALLERY:
                onPermissionRefuseNever(R.string.permission_request_READ_EXTERNAL_STORAGE);
                break;
        }
    }

    private void onPermissionRefuseNever(int stringRes) {
        String appName = getString(R.string.app_name);
        String message = String.format(getString(stringRes), appName);
        SpannableString span = new SpannableString(message);
        span.setSpan(new TextAppearanceSpan(this, R.style.text_color_2_15_style), 0, message.indexOf(appName), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int start = message.indexOf(appName) + appName.length();
        span.setSpan(new TextAppearanceSpan(this, R.style.text_color_1_17_bold_style), message.indexOf(appName), start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new TextAppearanceSpan(this, R.style.text_color_2_15_style), start, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        showPermissionRefusedNeverDialog(span);
    }

    /**
     * 权限被彻底禁止后 , 弹框提醒用户去开启
     */
    private void showPermissionRefusedNeverDialog(CharSequence message) {
        new AlertDialogBuilder(this)
                .setTitle(R.string.message_alert, true)
                .setMessage(message)
                .setLeftButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setRightButton(R.string.confirm, (dialog, which) -> {
                    dialog.dismiss();
                    goToAppSettingsPage();
                })
                .setShowLine(true)
                .setCanceledOnTouchOutside(false)
                .show();
    }

    public String getCardType() {
        switch (rgCardType.getCheckedRadioButtonId()) {
            case R.id.radio_card_type_2:
                return SDKConfig.TYPE_CREDIT_CARDCOR;
            case R.id.radio_card_type_3:
                return SDKConfig.TYPE_FOOD_CARDCOR;
            case R.id.radio_card_type_4:
                return SDKConfig.TYPE_IMAGE_CARDCOR;
            default:
                return SDKConfig.TYPE_IMAGE_CARDCOR;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ocrTask != null && !ocrTask.isCancelled()) {
            ocrTask.cancel(true);
        }
    }
}
