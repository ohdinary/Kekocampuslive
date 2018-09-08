package com.example.dinary.kekocampuslive.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.example.dinary.kekocampuslive.MyApplication;
import com.example.dinary.kekocampuslive.util.QnUploadHelper;
import com.example.dinary.kekocampuslive.widget.ChoosePicDialog;
import com.qiniu.android.http.ResponseInfo;
import com.tencent.TIMUserProfile;

import java.io.File;
import java.io.IOException;

/**
 * 图片选择/裁剪/上传类
 */
public class ChoosePicHelper {

    private Activity mActivity;
    private Fragment mFragment;
    private static final int FROM_CAMERA = 2;	//相机flag
    private static final int FROM_ALBUM = 1;	//相册flag
    private static final int CROP = 0;
    private TIMUserProfile mUserProfile;
    private Uri mCameraFileUri;
    private PicType mPicType;	//图片类型

    /*图片类型为头像还是封面(大小不同)*/
    public static enum PicType {
        Avatar, Cover
    }

    public ChoosePicHelper(Activity activity, PicType picType) {
        mActivity = activity;
        mPicType = picType;
    }
    public ChoosePicHelper(Fragment fragment, PicType picType) {
        mFragment = fragment;
        mActivity = fragment.getActivity();
        mPicType = picType;
        mUserProfile = MyApplication.getApplication().getSelfProfile();
    }

    public void showPicChooserDialog() {
        ChoosePicDialog dialog = new ChoosePicDialog(mActivity);
        dialog.setOnDialogClickListener(new ChoosePicDialog.ChoosePicListener() {
            @Override
            public void onCamera() {
                //拍照
                takePicFromCamera();
            }

            @Override
            public void onAlbum() {
                //相册
                takePicFromAlbum();
            }
        });
        dialog.show();
    }

    /*从本地选择照片*/
    private void takePicFromAlbum() {
        Intent picIntent = new Intent("android.intent.action.GET_CONTENT");
        picIntent.setType("image/*");
        if (mFragment == null) {
            mActivity.startActivityForResult(picIntent, FROM_ALBUM);
        } else {
            mFragment.startActivityForResult(picIntent, FROM_ALBUM);
        }
    }

    /*从相机拍照*/
    private void takePicFromCamera() {
        mCameraFileUri = createAlbumUri();

        //打开相机
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < 24) {
            //小于7.0的版本
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, mCameraFileUri);
            if (mFragment == null) {
                mActivity.startActivityForResult(intentCamera, FROM_CAMERA);
            } else {
                mFragment.startActivityForResult(intentCamera, FROM_CAMERA);
            }
        } else {
            //大于7.0的版本
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, mCameraFileUri.getPath());
            Uri uri = getImageContentUri(mCameraFileUri);
            LogUtil.d("choosepichelper_camera_uri",uri.toString());
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            if (mFragment == null) {
                mActivity.startActivityForResult(intentCamera, FROM_CAMERA);
            } else {
                mFragment.startActivityForResult(intentCamera, FROM_CAMERA);
            }
        }

    }

    /*创建选择本地相片的url*/
    private Uri createAlbumUri() {
        String dirPath = Environment.getExternalStorageDirectory() + "/" + mActivity.getApplication().getApplicationInfo().packageName;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String id = "";
        if (mUserProfile != null) {
            id = mUserProfile.getIdentifier();
        }
        String fileName = id + ".jpg";
        File picFile = new File(dirPath, fileName);
        if (picFile.exists()) {
            picFile.delete();
        }

        return Uri.fromFile(picFile);
    }

    /**
     * 转换 content:// uri
     */
    public Uri getImageContentUri(Uri uri) {
        String filePath = uri.getPath();
        Cursor cursor = mActivity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            return mActivity.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    /*对选择的照片进行处理*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FROM_CAMERA) {
            //从相机选择返回。
            if (resultCode == Activity.RESULT_OK) {
                LogUtil.d("choosepichelper_camera_back_success","111111");
                startCrop(mCameraFileUri);
            }
        } else if (requestCode == FROM_ALBUM) {
            //从相册选择返回。
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                startCrop(uri);
            }
        } else if (requestCode == CROP) {
            //裁剪结束
            if (resultCode == Activity.RESULT_OK) {
                //上传到服务器保存起来
                //七牛上传
                uploadTo7Niu(cropUri.getPath());
            }
        }
    }

    private Uri cropUri = null;

    /*图片剪裁*/
    private void startCrop(Uri uri) {
        cropUri = createCropUri();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("crop", "true");
        if(mPicType == PicType.Avatar) {
            intent.putExtra("aspectX", 300);
            intent.putExtra("aspectY", 300);
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
        }else if(mPicType == PicType.Cover){
            intent.putExtra("aspectX", 500);
            intent.putExtra("aspectY", 300);
            intent.putExtra("outputX", 500);
            intent.putExtra("outputY", 300);
        }
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < 24) {
            //小于7.0的版本
            intent.setDataAndType(uri, "image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
            if (mFragment == null) {
                mActivity.startActivityForResult(intent, CROP);
            } else {
                mFragment.startActivityForResult(intent, CROP);
            }
        } else {
            //大于7.0的版本
            {
                String scheme = uri.getScheme();
                if (scheme.equals("content")) {
                    Uri contentUri = uri;
                    intent.setDataAndType(contentUri, "image/*");
                } else {
                    Uri contentUri = getImageContentUri(uri);
                    intent.setDataAndType(contentUri, "image/*");
                }
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
            if (mFragment == null) {
                mActivity.startActivityForResult(intent, CROP);
            } else {
                mFragment.startActivityForResult(intent, CROP);
            }
        }
    }

    private Uri createCropUri() {
        String dirPath = Environment.getExternalStorageDirectory() + "/" + mActivity.getApplication().getApplicationInfo().packageName;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String id = "";
        if (mUserProfile != null) {
            id = mUserProfile.getIdentifier();
        }

        String fileName = id + "_crop.jpg";
        File picFile = new File(dirPath, fileName);
        if (picFile.exists()) {
            picFile.delete();
        }
        try {
            picFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(picFile);
    }

    /*上传到七牛云*/
    private void uploadTo7Niu(String path) {
        String id = "";
        if (mUserProfile != null) {
            id = mUserProfile.getIdentifier();
        }
        String name = id + "_" + System.currentTimeMillis() + "_avatar";
        QnUploadHelper.uploadPic(path, name, new QnUploadHelper.UploadCallBack() {

            @Override
            public void success(String url) {
                //上传成功
                if (mOnChooserResultListener != null) {
                    mOnChooserResultListener.onSuccess(url);
                }
            }

            @Override
            public void fail(String key, ResponseInfo info) {
                //上传失败！
                if (mOnChooserResultListener != null) {
                    mOnChooserResultListener.onFail(info.error);
                }
            }
        });
    }

    /**
     * 回调接口
     * */
    private OnChooseResultListener mOnChooserResultListener;
    public interface OnChooseResultListener {
        void onSuccess(String url);
        void onFail(String msg);
    }
    public void setOnChooseResultListener(OnChooseResultListener l) {
        mOnChooserResultListener = l;
    }
}
