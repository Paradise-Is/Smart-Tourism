package com.example.smarttourism.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class DocentInfoActivity extends Activity {
    private static final int REQUEST_CAMERA_CODE = 1001;
    //管理员用户名
    private String username;
    private int id;
    private ImageView backBt;
    private ImageView deleteBt;
    private ImageView photo;
    private TextView changeBt;
    private ConstraintLayout nameBar;
    private TextView nameInfo;
    private ConstraintLayout genderBar;
    private TextView genderInfo;
    private ConstraintLayout ageBar;
    private TextView ageInfo;
    private ConstraintLayout phoneBar;
    private TextView phoneInfo;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建讲解员信息界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.docent_info);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        deleteBt = (ImageView) this.findViewById(R.id.deleteBt);
        photo = (ImageView) this.findViewById(R.id.photo);
        changeBt = (TextView) this.findViewById(R.id.changeBt);
        nameBar = (ConstraintLayout) this.findViewById(R.id.name_bar);
        nameInfo = (TextView) this.findViewById(R.id.nameInfo);
        genderBar = (ConstraintLayout) this.findViewById(R.id.gender_bar);
        genderInfo = (TextView) this.findViewById(R.id.genderInfo);
        ageBar = (ConstraintLayout) this.findViewById(R.id.age_bar);
        ageInfo = (TextView) this.findViewById(R.id.ageInfo);
        phoneBar = (ConstraintLayout) this.findViewById(R.id.phone_bar);
        phoneInfo = (TextView) this.findViewById(R.id.phoneInfo);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(getApplicationContext());
        dbHelper.open();
        //获取用户用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        id = intent.getIntExtra("id", 1);
        //获取讲解员信息
        String docent_id = String.valueOf(id);
        String selection = "id=?";
        String[] selectionArgs = {docent_id};
        Cursor cursor = dbHelper.getDatabase().query("Docent", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            //从数据库获取数据进行界面显示
            @SuppressLint("Range")
            String dbPhoto = cursor.getString(cursor.getColumnIndex("docent_photo"));
            //将照片数据显示到界面中
            if (dbPhoto != null && !dbPhoto.isEmpty()) {
                File imgFile = new File(dbPhoto);
                if (imgFile.exists()) {
                    //删除旧图片的缓存
                    photo.setImageDrawable(null);
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    photo.setImageBitmap(bitmap);
                } else {
                    //数据库中无头像数据时提示错误
                    Toast.makeText(this, "照片信息缺失", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "照片信息缺失", Toast.LENGTH_SHORT).show();
            }
            @SuppressLint("Range")
            String dbName = cursor.getString(cursor.getColumnIndex("docent_name"));
            //将昵称数据显示到界面中
            if (dbName != null && !dbName.isEmpty()) {
                nameInfo.setText(dbName);
            }
            @SuppressLint("Range")
            String dbGender = cursor.getString(cursor.getColumnIndex("docent_gender"));
            //将性别数据显示到界面中
            if (dbGender != null && !dbGender.isEmpty()) {
                genderInfo.setText(dbGender);
            }
            @SuppressLint("Range")
            String dbAge = cursor.getString(cursor.getColumnIndex("docent_age"));
            //将年龄数据显示到界面中
            if (dbAge != null && !dbAge.isEmpty()) {
                ageInfo.setText(dbAge);
            }
            @SuppressLint("Range")
            String dbPhone = cursor.getString(cursor.getColumnIndex("docent_phone"));
            //将联系电话数据显示到界面中
            if (dbPhone != null && !dbPhone.isEmpty()) {
                phoneInfo.setText(dbPhone);
            }
        }
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
        deleteBt.setOnClickListener(new DeleteBtListener());
        changeBt.setOnClickListener(new ChangeBtListener());
        nameBar.setOnClickListener(new NameListener());
        genderBar.setOnClickListener(new GenderListener());
        ageBar.setOnClickListener(new AgeListener());
        phoneBar.setOnClickListener(new PhoneListener());
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
                    int size = Math.min(photo.getWidth(), photo.getHeight());
                    Bitmap scaledBitmap = BitmapUtils.zoomBitmap(bitmap, size);
                    //显示处理后的图片
                    photo.setImageBitmap(scaledBitmap);
                    //bitmap图片保存到本地
                    saveImage(scaledBitmap);
                    Toast.makeText(this, "新照片上传成功", Toast.LENGTH_SHORT).show();
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
            int size = Math.min(photo.getWidth(), photo.getHeight());
            BitmapUtils.zoomBitmap(bitmap, size);
            //显示处理后的图片
            photo.setImageBitmap(bitmap);
            //bitmap图片保存到本地
            saveImage(bitmap);
            Toast.makeText(this, "新照片上传成功", Toast.LENGTH_SHORT).show();
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
            ContentValues values = new ContentValues();
            values.put("docent_photo", file.getAbsolutePath());
            dbHelper.getDatabase().update("Docent", values, "id = ?", new String[]{String.valueOf(id)});
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
            photo.setImageBitmap(bitmap);
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

    private class DeleteBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DocentInfoActivity.this);
            builder.setMessage("是否确认要删除该讲解员");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //从表User中删除指定用户的记录
                    dbHelper.getDatabase().delete("Docent", "id = ? ", new String[]{String.valueOf(id)});
                    Toast.makeText(DocentInfoActivity.this, "删除该讲解员成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            builder.setNeutralButton("取消", null);
            builder.show();
        }
    }

    private class ChangeBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DocentInfoActivity.this);
            builder.setTitle("上传新照片");
            //选择照片上传方式
            builder.setItems(new String[]{"拍照", "选取本地照片"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            //运行时请求权限
                            if (ContextCompat.checkSelfPermission(DocentInfoActivity.this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(DocentInfoActivity.this,
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

    private class NameListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DocentInfoActivity.this);
            builder.setTitle("修改讲解员姓名");
            //创建一个输入框
            final EditText input = new EditText(DocentInfoActivity.this);
            input.setHint("请输入姓名");
            //显示当前昵称
            input.setText(nameInfo.getText());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty() && newName.length() <= 15) {
                        ContentValues values = new ContentValues();
                        values.put("docent_name", newName);
                        dbHelper.getDatabase().update("Docent", values, "id = ?", new String[]{String.valueOf(id)});
                        nameInfo.setText(newName);
                        Toast.makeText(DocentInfoActivity.this, "讲解员昵称修改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DocentInfoActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNeutralButton("取消", null);
            //显示弹窗
            builder.create().show();
        }
    }

    private class GenderListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DocentInfoActivity.this);
            builder.setTitle("选择性别");
            //定义可选的性别选项
            final String[] genders = {"男", "女"};
            //设置单选列表，默认不选中(-1)
            builder.setSingleChoiceItems(genders, -1, null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 获取弹窗中ListView的选中项
                    ListView lw = ((AlertDialog) dialog).getListView();
                    int selectedPosition = lw.getCheckedItemPosition();
                    if (selectedPosition != -1) {
                        String selectedGender = genders[selectedPosition];
                        ContentValues values = new ContentValues();
                        values.put("docent_gender", selectedGender);
                        dbHelper.getDatabase().update("Docent", values, "id = ?", new String[]{String.valueOf(id)});
                        genderInfo.setText(selectedGender);
                        Toast.makeText(DocentInfoActivity.this, "讲解员性别修改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DocentInfoActivity.this, "请选择性别", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNeutralButton("取消", null);
            builder.create().show();
        }
    }

    private class AgeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DocentInfoActivity.this);
            builder.setTitle("修改讲解员年龄");
            //创建一个输入框
            final EditText input = new EditText(DocentInfoActivity.this);
            input.setHint("请输入年龄");
            //显示当前昵称
            input.setText(ageInfo.getText());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newAge = input.getText().toString().trim();
                    if (!newAge.isEmpty() && newAge.length() <= 3) {
                        ContentValues values = new ContentValues();
                        values.put("docent_age", newAge);
                        dbHelper.getDatabase().update("Docent", values, "id = ?", new String[]{String.valueOf(id)});
                        ageInfo.setText(newAge);
                        Toast.makeText(DocentInfoActivity.this, "讲解员年龄修改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DocentInfoActivity.this, "年龄不能为空且要符合实际", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNeutralButton("取消", null);
            //显示弹窗
            builder.create().show();
        }
    }

    private class PhoneListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DocentInfoActivity.this);
            builder.setTitle("修改讲解员联系电话");
            //创建一个输入框
            final EditText input = new EditText(DocentInfoActivity.this);
            input.setHint("请输入联系电话");
            //显示当前昵称
            input.setText(phoneInfo.getText());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newPhone = input.getText().toString().trim();
                    Pattern phonePattern = Pattern.compile("(13[0-9]|14[57]|15[012356789]|18[02356789])\\d{8}");
                    if (!newPhone.isEmpty() && phonePattern.matcher(newPhone).matches()) {
                        ContentValues values = new ContentValues();
                        values.put("docent_phone", newPhone);
                        dbHelper.getDatabase().update("Docent", values, "id = ?", new String[]{String.valueOf(id)});
                        phoneInfo.setText(newPhone);
                        Toast.makeText(DocentInfoActivity.this, "讲解员联系电话修改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DocentInfoActivity.this, "联系电话输入不符合规范", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNeutralButton("取消", null);
            //显示弹窗
            builder.create().show();
        }
    }
}
