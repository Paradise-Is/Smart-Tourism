package com.example.smarttourism.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smarttourism.R;
import com.example.smarttourism.util.BitmapUtils;
import com.example.smarttourism.util.DBHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

public class DocentAddActivity extends Activity {
    private static final int REQUEST_CAMERA_CODE = 1001;
    private ImageView backBt;
    private TextView logBt;
    private ImageView photoImg;
    private String photoPath = "";
    private EditText nameEt;
    private Spinner genderSp;
    private EditText ageEt;
    private EditText phoneEt;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建讲解员登记界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.docent_add);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        logBt = (TextView) this.findViewById(R.id.logBt);
        photoImg = (ImageView) this.findViewById(R.id.photoImg);
        nameEt = (EditText) this.findViewById(R.id.nameEt);
        genderSp = (Spinner) this.findViewById(R.id.genderSp);
        ageEt = (EditText) this.findViewById(R.id.ageEt);
        phoneEt = (EditText) this.findViewById(R.id.phoneEt);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(getApplicationContext());
        dbHelper.open();
        //下拉框的选项数据
        String[] genderTypes = {"男", "女"};
        //创建 ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //设置适配器
        genderSp.setAdapter(adapter);
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
        logBt.setOnClickListener(new LogBtListener());
        photoImg.setOnClickListener(new PhotoImgListener());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //系统相册获取
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            //获取地图URI
            Uri imageUri = data.getData();
            try {
                //获取图片在本地的实际文件路径
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                //将图片解码成Bitmap对象
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null) {
                    //调整图片尺寸
                    int size = Math.min(photoImg.getWidth(), photoImg.getHeight());
                    Bitmap scaledBitmap = BitmapUtils.zoomBitmap(bitmap, size);
                    //显示处理后的图片
                    photoImg.setImageBitmap(scaledBitmap);
                    //bitmap图片保存到本地
                    saveImage(scaledBitmap);
                } else {
                    Toast.makeText(this, "无法解析选取的图片", Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //系统相机获取
        else if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            //获取拍照后生成
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            //对图片进行处理
            int size = Math.min(photoImg.getWidth(), photoImg.getHeight());
            BitmapUtils.zoomBitmap(bitmap, size);
            //显示处理后的图片
            photoImg.setImageBitmap(bitmap);
            //bitmap图片保存到本地
            saveImage(bitmap);
        }
    }

    private void saveImage(Bitmap bitmap) {
        File filesDir;
        //判断sd卡是否挂载，优先使用外部存储
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //路径：storage/sdcard/Android/data/包名/files
            filesDir = this.getExternalFilesDir("");
        }
        //否则使用手机内部存储
        else {
            //路径：data/data/包名/files
            filesDir = this.getFilesDir();
        }
        //创建FileOutputStream对象，将Bitmap压缩保存到文件中
        FileOutputStream fos = null;
        try {
            //添加时间戳，保证图片名不一致
            File file = new File(filesDir, "icon_" + System.currentTimeMillis() + ".png");
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            //保存成功后，保存头像路径
            photoPath = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //判断本地是否存在保存的头像图片
    private boolean readImage() {
        File filesDir;
        //判断sd卡是否挂载
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //路径1：storage/sdcard/Android/data/包名/files
            filesDir = getExternalFilesDir("");
        }
        //手机内部存储
        else {
            //路径：data/data/包名/files
            filesDir = getFilesDir();
        }
        File file = new File(filesDir, "icon.png");
        if (file.exists()) {
            //将存储转移到内存
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            photoImg.setImageBitmap(bitmap);
            return true;
        }
        return false;
    }

    //判断本地是否有该图片,没有则去联网请求
    @Override
    protected void onResume() {
        super.onResume();
        //判断本地是否已有保存的头像图片
        if (readImage()) {
            return;
        }
    }

    private class BackBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }

    private class LogBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String name = nameEt.getText().toString();
            String gender = genderSp.getSelectedItem().toString();
            String age = ageEt.getText().toString();
            String phone = phoneEt.getText().toString();
            Pattern phonePattern = Pattern.compile("(13[0-9]|14[57]|15[012356789]|18[02356789])\\d{8}");
            if (photoPath.equals("")) {
                Toast.makeText(DocentAddActivity.this, "个人照片必须上传", Toast.LENGTH_SHORT).show();
            } else if (name.equals("")) {
                Toast.makeText(DocentAddActivity.this, "姓名必须填写", Toast.LENGTH_SHORT).show();
            } else if (age.equals("")) {
                Toast.makeText(DocentAddActivity.this, "年龄必须填写", Toast.LENGTH_SHORT).show();
            } else if (phone.equals("")) {
                Toast.makeText(DocentAddActivity.this, "联系电话必须填写", Toast.LENGTH_SHORT).show();
            } else if (!phonePattern.matcher(phone).matches()) {
                Toast.makeText(DocentAddActivity.this, "联系电话不符合规范", Toast.LENGTH_SHORT).show();
            } else {
                //构造讲解员的各字段数据
                ContentValues values = new ContentValues();
                values.put("docent_name", name);
                values.put("docent_gender", gender);
                values.put("docent_age", age);
                values.put("docent_photo", photoPath);
                values.put("docent_phone", phone);
                //添加讲解员信息
                long result = dbHelper.getDatabase().insert("Docent", null, values);
                if (result != -1) {
                    Toast.makeText(DocentAddActivity.this, "成功登记讲解员", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(DocentAddActivity.this, "出错了，登记失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class PhotoImgListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DocentAddActivity.this);
            builder.setTitle("上传照片");
            //选择照片上传方式
            builder.setItems(new String[]{"拍照", "选取本地照片"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            //运行时请求权限
                            if (ContextCompat.checkSelfPermission(DocentAddActivity.this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(DocentAddActivity.this,
                                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
                            } else {
                                //启动相机
                                Intent intentOne = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intentOne, 200);
                            }
                            break;
                        case 1:
                            Intent intentTwo = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intentTwo, 100);
                            break;
                    }
                }
            });
            builder.create().show();
        }
    }
}
