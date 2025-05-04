package com.example.smarttourism.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.regex.Pattern;

public class MineInfoActivity extends Activity {
    private static final int REQUEST_CAMERA_CODE = 1001;
    //用户用户名
    private String username;
    private ImageView backBt;
    private ImageView headshot;
    private TextView changeBt;
    private ConstraintLayout nicknameBar;
    private TextView nicknameInfo;
    private ConstraintLayout genderBar;
    private TextView genderInfo;
    private ConstraintLayout introductionBar;
    private TextView introductionInfo;
    private ConstraintLayout birthdayBar;
    private TextView birthdayInfo;
    private ConstraintLayout phoneBar;
    private TextView phoneInfo;
    private ConstraintLayout emailBar;
    private TextView emailInfo;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建显示用户个人信息界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_info);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        headshot = (ImageView) this.findViewById(R.id.headshot);
        changeBt = (TextView) this.findViewById(R.id.changeBt);
        nicknameBar = (ConstraintLayout) this.findViewById(R.id.nickname_bar);
        nicknameInfo = (TextView) this.findViewById(R.id.nicknameInfo);
        genderBar = (ConstraintLayout) this.findViewById(R.id.gender_bar);
        genderInfo = (TextView) this.findViewById(R.id.genderInfo);
        introductionBar = (ConstraintLayout) this.findViewById(R.id.introduction_bar);
        introductionInfo = (TextView) this.findViewById(R.id.introductionInfo);
        birthdayBar = (ConstraintLayout) this.findViewById(R.id.birthday_bar);
        birthdayInfo = (TextView) this.findViewById(R.id.birthdayInfo);
        phoneBar = (ConstraintLayout) this.findViewById(R.id.phone_bar);
        phoneInfo = (TextView) this.findViewById(R.id.phoneInfo);
        emailBar = (ConstraintLayout) this.findViewById(R.id.email_bar);
        emailInfo = (TextView) this.findViewById(R.id.emailInfo);
        //实现数据库功能
        dbHelper = new DBHelper(this);
        dbHelper.open();
        //获取用户用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        //将相关信息显示到界面上
        String selection = "username=?";
        String[] selectionArgs = {username};
        //根据用户查询到对应项
        Cursor cursor = dbHelper.getDatabase().query("User", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            //从数据库获取数据进行界面显示
            @SuppressLint("Range")
            String dbHeadshot = cursor.getString(cursor.getColumnIndex("headshot"));
            //将头像数据显示到界面中
            if (dbHeadshot != null && !dbHeadshot.isEmpty()) {
                File imgFile = new File(dbHeadshot);
                if (imgFile.exists()) {
                    //删除旧图片的缓存
                    headshot.setImageDrawable(null);
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    headshot.setImageBitmap(bitmap);
                } else {
                    //数据库中无头像数据时显示默认头像
                    headshot.setImageResource(R.mipmap.mine_headshot);
                }
            } else {
                headshot.setImageResource(R.mipmap.mine_headshot);
            }
            @SuppressLint("Range")
            String dbNickname = cursor.getString(cursor.getColumnIndex("nickname"));
            //将昵称数据显示到界面中
            if (dbNickname != null && !dbNickname.isEmpty()) {
                nicknameInfo.setText(dbNickname);
            }
            @SuppressLint("Range")
            String dbGender = cursor.getString(cursor.getColumnIndex("gender"));
            //将性别数据显示到界面中
            if (dbGender != null && !dbGender.isEmpty()) {
                genderInfo.setText(dbGender);
            }
            @SuppressLint("Range")
            String dbIntroduction = cursor.getString(cursor.getColumnIndex("introduction"));
            //将简介数据显示到界面中
            if (dbIntroduction != null && !dbIntroduction.isEmpty()) {
                introductionInfo.setText(dbIntroduction);
            }
            @SuppressLint("Range")
            String dbBirthday = cursor.getString(cursor.getColumnIndex("birthday"));
            //将生日数据显示到界面中
            if (dbBirthday != null && !dbBirthday.isEmpty()) {
                birthdayInfo.setText(dbBirthday);
            }
            @SuppressLint("Range")
            String dbPhone = cursor.getString(cursor.getColumnIndex("phone"));
            //将电话数据显示到界面中
            if (dbPhone != null && !dbPhone.isEmpty()) {
                phoneInfo.setText(dbPhone);
            }
            @SuppressLint("Range")
            String dbEmail = cursor.getString(cursor.getColumnIndex("email"));
            //将邮箱数据显示到界面中
            if (dbEmail != null && !dbEmail.isEmpty()) {
                emailInfo.setText(dbEmail);
            }
        } else {
            Toast.makeText(MineInfoActivity.this, "系统出错了┭┮﹏┭┮", Toast.LENGTH_SHORT).show();
        }
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
        changeBt.setOnClickListener(new ChangeBtListener());
        nicknameBar.setOnClickListener(new NicknameListener());
        genderBar.setOnClickListener(new GenderListener());
        introductionBar.setOnClickListener(new IntroductionListener());
        birthdayBar.setOnClickListener(new BirthdayListener());
        phoneBar.setOnClickListener(new PhoneListener());
        emailBar.setOnClickListener(new EmailListener());
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
                    int size = Math.min(headshot.getWidth(), headshot.getHeight());
                    //用控件的最小边长作为目标尺寸
                    Bitmap circularBitmap = BitmapUtils.circleBitmap(bitmap, size);
                    //显示处理后的图片
                    headshot.setImageBitmap(circularBitmap);
                    //bitmap图片保存到本地
                    saveImage(circularBitmap);
                    Toast.makeText(this, "头像修改成功", Toast.LENGTH_SHORT).show();
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
            int size = Math.min(headshot.getWidth(), headshot.getHeight());
            bitmap = BitmapUtils.circleBitmap(bitmap, size);
            //显示处理后的图片
            headshot.setImageBitmap(bitmap);
            //bitmap图片保存到本地
            saveImage(bitmap);
            Toast.makeText(this, "头像修改成功", Toast.LENGTH_SHORT).show();
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
            //保存成功后，更新数据库中用户的头像路径
            dbHelper.updateUserHeadshot(username, file.getAbsolutePath());
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
            headshot.setImageBitmap(bitmap);
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

    private class ChangeBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MineInfoActivity.this);
            builder.setTitle("设置头像");
            //选择头像获取方式
            builder.setItems(new String[]{"拍照", "选取本地照片"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            //运行时请求权限
                            if (ContextCompat.checkSelfPermission(MineInfoActivity.this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MineInfoActivity.this,
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

    private class NicknameListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MineInfoActivity.this);
            builder.setTitle("修改昵称");
            //创建一个输入框
            final EditText input = new EditText(MineInfoActivity.this);
            input.setHint("请输入新昵称");
            //显示当前昵称
            input.setText(nicknameInfo.getText());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newNickname = input.getText().toString().trim();
                    if (!newNickname.isEmpty() && newNickname.length() <= 10) {
                        // 更新数据库中的昵称
                        dbHelper.updateUserNickname(username, newNickname);
                        // 更新界面上的昵称
                        nicknameInfo.setText(newNickname);
                        Toast.makeText(MineInfoActivity.this, "昵称修改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MineInfoActivity.this, "昵称必须小于等于10个字符且不能为空", Toast.LENGTH_SHORT).show();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(MineInfoActivity.this);
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
                        // 更新数据库中用户性别
                        dbHelper.updateUserGender(username, selectedGender);
                        // 更新界面显示（假设有一个TextView显示性别）
                        genderInfo.setText(selectedGender);
                        Toast.makeText(MineInfoActivity.this, "性别修改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MineInfoActivity.this, "请选择性别", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNeutralButton("取消", null);
            builder.create().show();
        }
    }

    private class IntroductionListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MineInfoActivity.this);
            builder.setTitle("修改简介");
            //创建一个输入框
            final EditText input = new EditText(MineInfoActivity.this);
            input.setHint("请输入你的简介");
            //显示当前简介
            input.setText(introductionInfo.getText());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newIntroduction = input.getText().toString().trim();
                    if (!newIntroduction.isEmpty() && newIntroduction.length() <= 50) {
                        // 更新数据库中的昵称
                        dbHelper.updateUserIntroduction(username, newIntroduction);
                        // 更新界面上的昵称
                        introductionInfo.setText(newIntroduction);
                        Toast.makeText(MineInfoActivity.this, "简介修改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MineInfoActivity.this, "简介必须小于等于50个字符且不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNeutralButton("取消", null);
            //显示弹窗
            builder.create().show();
        }
    }

    private class BirthdayListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //获取当前日期作为默认值
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            //创建 DatePickerDialog 对象
            DatePickerDialog datePickerDialog = new DatePickerDialog(MineInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                    String birthday = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    birthdayInfo.setText(birthday);
                    dbHelper.updateUserBirthday(username, birthday);
                    Toast.makeText(MineInfoActivity.this, "生日修改成功", Toast.LENGTH_SHORT).show();
                }
            }, year, month, day);
            //显示弹窗
            datePickerDialog.show();
        }
    }

    private class PhoneListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MineInfoActivity.this);
            builder.setTitle("修改电话");
            //创建一个输入框
            final EditText input = new EditText(MineInfoActivity.this);
            input.setHint("请输入你的电话");
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
                        // 更新数据库中的昵称
                        dbHelper.updateUserPhone(username, newPhone);
                        // 更新界面上的昵称
                        phoneInfo.setText(newPhone);
                        Toast.makeText(MineInfoActivity.this, "电话修改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MineInfoActivity.this, "电话不符合规范", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNeutralButton("取消", null);
            //显示弹窗
            builder.create().show();
        }
    }

    private class EmailListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MineInfoActivity.this);
            builder.setTitle("修改邮箱");
            //创建一个输入框
            final EditText input = new EditText(MineInfoActivity.this);
            input.setHint("请输入你的邮箱");
            //显示当前昵称
            input.setText(emailInfo.getText());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newEmail = input.getText().toString().trim();
                    Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
                    String selection = "email=?";
                    String[] selectionArgs = {newEmail};
                    //查询邮箱是否已经被注册过
                    Cursor cursor = dbHelper.getDatabase().query("User", null, selection, selectionArgs, null, null, null);
                    if (cursor.getCount() != 0) {
                        Toast.makeText(MineInfoActivity.this, "该邮箱已被注册", Toast.LENGTH_SHORT).show();
                    } else if (!newEmail.isEmpty() && emailPattern.matcher(newEmail).matches()) {
                        // 更新数据库中的昵称
                        dbHelper.updateUserEmail(username, newEmail);
                        // 更新界面上的昵称
                        emailInfo.setText(newEmail);
                        Toast.makeText(MineInfoActivity.this, "邮箱修改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MineInfoActivity.this, "邮箱不符合规范", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNeutralButton("取消", null);
            //显示弹窗
            builder.create().show();
        }
    }
}
