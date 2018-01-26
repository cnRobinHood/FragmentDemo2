package com.xiezh.findlost.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiezh.findlost.adapter.CameraImageAdapter;
import com.xiezh.findlost.service.ItemService;
import com.xiezh.findlost.utils.DataManager;
import com.xiezh.fragmentdemo2.R;

import java.io.File;

public class AddItemActivity extends BaseActivity implements View.OnClickListener {
    private static final int TAKE_PHOTO = 1;
    private static final int RESULT_LOAD_IMAGE = 2;
    private TextView backImage;//返回按钮
    private TextView sureImage;//确认发布按钮
    private EditText itemRemark;//对失物的描述
    private ImageView cameraImage;
    private File file;
    private Dialog dialog;
    private View inflate;
    private TextView take_photo;
    private TextView choose_local;

    private GridView imageGridView;
    private Bitmap photo;
    private CameraImageAdapter cameraImageAdapter;
    private Context context;

    private TextView save;
    private TextView not_save;
    private TextView cancel;

    private int click_id;
    private ItemService.ItemBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (ItemService.ItemBinder) service;
            binder.uploadItem(context);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        init();
        itemRemark.setText(DataManager.remarkStr);
        context = this;
    }

    /**
     * 绑定组件
     */
    void init() {
        backImage = (TextView) findViewById(R.id.back);//返回按钮
        sureImage = (TextView) findViewById(R.id.sure);//确认发布按钮
        itemRemark = (EditText) findViewById(R.id.item_remark);//对失物的描述
        imageGridView = (GridView) findViewById(R.id.new_item_iamge);
        cameraImage = (ImageView) findViewById(R.id.camera);

        backImage.setOnClickListener(this);
        sureImage.setOnClickListener(this);
        backImage.setOnClickListener(this);
        cameraImage.setOnClickListener(this);

        cameraImageAdapter = new CameraImageAdapter(AddItemActivity.this);
        DataManager.cameraImageAdapter = cameraImageAdapter;

        imageGridView.setAdapter(cameraImageAdapter);

    }

    @Override
    public void onClick(View v) {
        click_id = v.getId();
        switch (v.getId()) {
            case R.id.back:
                save();
                break;
            case R.id.sure:
                DataManager.remarkStr = itemRemark.getText().toString();
                Intent intent = new Intent(AddItemActivity.this, ItemService.class);
                bindService(intent, connection, Service.BIND_AUTO_CREATE);
                itemRemark.setText("");
                cameraImageAdapter.mData.clear();
                cameraImageAdapter.notifyDataSetChanged();
                finish();


                break;
            case R.id.item_remark:
                break;
            case R.id.camera:
                //openTakePhoto();
                show();
                break;
            case R.id.save:
                DataManager.remarkStr = itemRemark.getText().toString();
                finish();
                break;
            case R.id.not_save:
                DataManager.remarkStr = null;
                finish();
                break;
            case R.id.cancel:
                dialog.dismiss();
                break;
            case R.id.take_photo:
                applyWritePermission();
                break;
            case R.id.choose_photo:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //设定结果返回
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
        }

    }


    @Override
    public void onBackPressed() {
        save();
    }

    void save() {
        String str = itemRemark.getText().toString();
        if (str.equals("")) {
            finish();
        } else {
            show();
        }
    }

    private void openTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PHOTO);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
                    //Log.e("TAG", "---------" + FileProvider.getUriForFile(this, "com.xiezh.findlost.provider", file));
                    cameraImageAdapter.add(BitmapFactory.decodeFile(file.getAbsolutePath()));
                    DataManager.newItemImage.add(file.getAbsolutePath());
                    Log.i("TAKE_PHOTO", file.getAbsolutePath());
                }
                break;

                /*if (data != null) {
                    Bundle bundle = data.getExtras(); // 从data中取出传递回来缩略图的信息，图片质量差，适合传递小图片
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    cameraImageAdapter.add(bitmap);

                } else {
                    Toast.makeText(getApplicationContext(), "找不到图片", Toast.LENGTH_SHORT).show();
                }*/
                   /* //拍摄图片并选择
                    if (uri != null) {
                        //拿到图片
                        if(photo != null){
                            Toast.makeText(context,"photo不为空",Toast.LENGTH_LONG).show();
                            cameraImageAdapter.add(photo);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "找不到图片", Toast.LENGTH_SHORT).show();
                    }
                    break;*/
            case RESULT_LOAD_IMAGE:
                if (requestCode == RESULT_LOAD_IMAGE & data != null) {
                    //获取返回的数据，这里是android自定义的Uri地址
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    //获取选择照片的数据视图
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    String picturePath = null;

                    while (cursor.moveToNext()) {
                        //从数据视图中获取已选择图片的路径
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);

                        File selectImage = new File(picturePath);
                        cameraImageAdapter.add(BitmapFactory.decodeFile(selectImage.getAbsolutePath()));
                        DataManager.newItemImage.add(selectImage.getAbsolutePath());
                    }

                    cursor.close();
                    //将图片显示到界面上
                }

                break;
        }
    }

    public void applyWritePermission() {

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= 23) {
            int check = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (check == PackageManager.PERMISSION_GRANTED) {
                //调用相机
                useCamera();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            useCamera();
        }
    }

    private void useCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/findlost/camera/" + System.currentTimeMillis() + ".jpg");
        file.getParentFile().mkdirs();
        //改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
        Uri uri = FileProvider.getUriForFile(this, "com.xiezh.findlost.provider", file);

        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            useCamera();
        } else {
            // 没有获取 到权限，从新请求，或者关闭app
            Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show();
        }
    }

    public void show() {
        dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        if (click_id != R.id.camera) {
            //填充对话框的布局
            inflate = LayoutInflater.from(this).inflate(R.layout.dialog_bottom, null);
            //初始化控件
            save = inflate.findViewById(R.id.save);
            not_save = inflate.findViewById(R.id.not_save);
            cancel = inflate.findViewById(R.id.cancel);

            save.setOnClickListener(this);
            not_save.setOnClickListener(this);
            cancel.setOnClickListener(this);
        } else {
            //填充对话框的布局
            inflate = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_camera, null);
            take_photo = inflate.findViewById(R.id.take_photo);
            choose_local = inflate.findViewById(R.id.choose_photo);

            take_photo.setOnClickListener(this);
            choose_local.setOnClickListener(this);
        }
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }
}