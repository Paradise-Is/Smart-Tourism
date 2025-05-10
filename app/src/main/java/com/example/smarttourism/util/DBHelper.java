package com.example.smarttourism.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {
    //数据库名
    private static final String DATABASE_NAME = "SmartTourism.db";
    //数据库版本号
    private static final int DATABASE_VERSION = 1;
    //创建用户信息表（用户名，密码，电子邮箱，头像，昵称，性别，电话，个人简介，生日）
    private static final String CREATE_User = "create table if not exists User("
            + "username text primary key,"
            + "password text not null,"
            + "email text not null,"
            + "headshot text,"
            + "nickname text not null,"
            + "gender text,"
            + "phone text,"
            + "introduction text,"
            + "birthday text)";
    //创建管理员信息表（用户名，密码）
    private static final String CREATE_Admin = "create table if not exists Admin("
            + "username text primary key,"
            + "password text not null)";
    //创建投诉记录表（id，投诉者用户名，投诉类型，投诉内容，投诉日期，投诉者联系方式，投诉状态）
    private static final String CREATE_Complaint = "create table if not exists Complaint("
            + "id integer primary key autoincrement, "
            + "complaint_username text not null, "
            + "complaint_type text not null, "
            + "complaint_content text not null, "
            + "complaint_date text not null, "
            + "complaint_contact text not null, "
            + "status text not null);";
    //创建报警记录表（id，报警者用户名，报警日期，报警位置纬度，报警位置经度）
    private static final String CREATE_Alarm = "create table if not exists Alarm("
            + "id integer primary key autoincrement, "
            + "alarm_username text not null, "
            + "alarm_date text not null,"
            + "alarm_latitude text, "
            + "alarm_longitude text); ";
    //创建攻略内容表（id，攻略标题，攻略内容，攻略发布日期，攻略发布者用户名）
    private static final String CREATE_Guide = "create table if not exists Guide("
            + "id integer primary key autoincrement, "
            + "guide_title text not null, "
            + "guide_content text not null, "
            + "guide_date text not null, "
            + "guide_username text not null);";
    //创建讲解员信息表（id，讲解员姓名，讲解员性别，讲解员年龄，讲解员照片，讲解员联系方式）
    private static final String CREATE_Docent = "create table if not exists Docent("
            + "id integer primary key autoincrement, "
            + "docent_name text not null, "
            + "docent_gender text not null, "
            + "docent_age text not null, "
            + "docent_photo text not null, "
            + "docent_phone text not null);";
    //创建游览车信息表（id，车牌号，载客量，游览车纬度，游览车经度，游览车状态）
    private static final String CREATE_Coach = "create table if not exists Coach("
            + "id integer primary key autoincrement, "
            + "coach_license text not null, "
            + "coach_capacity text not null, "
            + "gps_latitude text, "
            + "gps_longitude text, "
            + "status text not null);";
    //创建东湖景区景点信息表（id，景点名，景点描述，景点票价，景点纬度，景点经度，景点照片）
    private static final String CREATE_Sight = "create table if not exists Sight("
            + "id integer primary key autoincrement, "
            + "name text not null, "
            + "description text not null, "
            + "price text not null, "
            + "latitude text not null, "
            + "longitude text not null, "
            + "image text not null);";
    //创建东湖景区景点评价表（id，评论者用户名，景点标识，评论内容，评论日期）
    private static final String CREATE_SightComment = "create table if not exists SightComment("
            + "comment_id integer primary key autoincrement, "
            + "comment_username text not null, "
            + "sight_id integer not null, "
            + "comment_text text not null, "
            + "comment_date text not null);";
    //创建东湖景区景点票据表（id，购买者用户名，景点标识，购买数量，购买单价，合计金额，购买日期）
    private static final String CREATE_SightPurchase = "create table if not exists SightPurchase("
            + "purchase_id integer primary key autoincrement, "
            + "purchase_username text not null, "
            + "sight_id integer not null, "
            + "quantity text not null, "
            + "price text not null, "
            + "total text not null, "
            + "purchase_date text not null);";
    //创建访客量表（id，日期年，日期月，日期日，访客量）
    private static final String CREATE_VisitorStat = "create table if not exists VisitorStat("
            + "id integer primary key autoincrement,"
            + "year text not null, "
            + "month text not null, "
            + "day text not null, "
            + "count integer not null);";
    //数据库操作实例
    private static DBHelper instance;
    private SQLiteDatabase database;

    //构造函数，创建数据库
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //获取数据库操作实例
    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    //创建数据库表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_User);
        db.execSQL(CREATE_Admin);
        db.execSQL(CREATE_Complaint);
        db.execSQL(CREATE_Alarm);
        db.execSQL(CREATE_Guide);
        db.execSQL(CREATE_Docent);
        db.execSQL(CREATE_Coach);
        db.execSQL(CREATE_Sight);
        db.execSQL(CREATE_SightComment);
        db.execSQL(CREATE_SightPurchase);
        db.execSQL(CREATE_VisitorStat);
        //初始化创建一个管理员账号
        db.execSQL("insert into Admin(username,password)values('admin','123456')");
        //初始化景区景点信息
        insertSightsInfo(db);
    }

    //更新指定用户邮箱
    public void updateUserEmail(String username, String newEmail) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", newEmail);
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //更新指定用户头像并将图片路径保存到数据库中
    public void updateUserHeadshot(String username, String headshotPath) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("headshot", headshotPath);
        // 更新User表中 username 对应的记录
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //更新指定用户昵称
    public void updateUserNickname(String username, String newNickname) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nickname", newNickname);
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //更新指定用户性别
    public void updateUserGender(String username, String newGender) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("gender", newGender);
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //更新指定用户电话
    public void updateUserPhone(String username, String newPhone) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", newPhone);
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //更新指定用户简介
    public void updateUserIntroduction(String username, String newIntroduction) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("introduction", newIntroduction);
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //更新指定用户生日
    public void updateUserBirthday(String username, String newBirthday) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("birthday", newBirthday);
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //提供访客量数据
    public void incrementToday() {
        database = this.getWritableDatabase();
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        ContentValues v = new ContentValues();
        v.put("count", "count+1");
        String where = "year=? and month=? and day=?";
        String[] args = {year, month, day};
        int updated = database.update("VisitorStat", v, where, args);
        if (updated == 0) {
            ContentValues values = new ContentValues();
            values.put("year", year);
            values.put("month", month);
            values.put("day", day);
            values.put("count", 1);
            database.insert("VisitorStat", null, values);
        }
    }

    //计算近N天的合计访客量
    public int sumLastNDays(int N) {
        database = this.getWritableDatabase();
        int total = 0;
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < N; i++) {
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);
            String year = String.valueOf(cal.get(Calendar.YEAR));
            String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
            String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            Cursor cursor = database.rawQuery(
                    "select count from VisitorStat where year=? and month=? and day=?",
                    new String[]{year, month, day}
            );
            if (cursor.moveToFirst()) {
                //获取当日访客量
                total += cursor.getInt(0);
            }
            cursor.close();
        }
        return total;
    }

    //计算近N天的平均访客量
    public float avgLastNDays(int N) {
        int sum = sumLastNDays(N);
        return N > 0 ? (float) sum / N : 0;
    }

    //获取最近N天，每天的(标签，访客量)
    public List<Pair<String, Integer>> getLastNDaysCounts(int N) {
        database = this.getWritableDatabase();
        List<Pair<String, Integer>> out = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = N - 1; i >= 0; i--) {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            Cursor cursor = database.rawQuery(
                    "select count from VisitorStat where year=? and month=? and day=?",
                    new String[]{year, month, day}
            );
            int cnt = 0;
            if (cursor.moveToFirst()) cnt = cursor.getInt(0);
            cursor.close();
            String label = (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
            out.add(new Pair<>(label, cnt));
        }
        return out;
    }

    //获取最近N月，每月的(标签，访客量)
    public List<Pair<String, Integer>> getLastNMonthsCounts(int N) {
        database = this.getWritableDatabase();
        List<Pair<String, Integer>> out = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = N - 1; i >= 0; i--) {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -i);
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            Cursor cursor = database.rawQuery(
                    "select SUM(count) from VisitorStat where year=? and month=?",
                    new String[]{year, month}
            );
            int cnt = 0;
            if (cursor.moveToFirst()) cnt = cursor.getInt(0);
            cursor.close();
            String label = year + "-" + (calendar.get(Calendar.MONTH) + 1);
            out.add(new Pair<>(label, cnt));
        }
        return out;
    }

    //获取最近N年，每年的(标签，访客量)
    public List<Pair<String, Integer>> getLastNYearsCounts(int N) {
        database = this.getWritableDatabase();
        List<Pair<String, Integer>> out = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = N - 1; i >= 0; i--) {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -i);
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            Cursor cursor = database.rawQuery(
                    "select SUM(count) from VisitorStat where year=?",
                    new String[]{year}
            );
            int cnt = 0;
            if (cursor.moveToFirst()) cnt = cursor.getInt(0);
            cursor.close();
            out.add(new Pair<>(year, cnt));
        }
        return out;
    }

    //获取固定时间段的景点销售额
    public Map<Integer, Float> sumSalesByPeriod(String period) {
        database = this.getWritableDatabase();
        String where;
        switch (period) {
            case "日":
                where = "date(purchase_date)=date('now')";
                break;
            case "周":
                where = "date(purchase_date)>=date('now','-6 days')";
                break;
            case "月":
                where = "strftime('%Y-%m',purchase_date)=strftime('%Y-%m','now')";
                break;
            case "年":
            default:
                where = "strftime('%Y',purchase_date)=strftime('%Y','now')";
        }
        String sql = "select sight_id, SUM(CAST(total AS REAL)) from SightPurchase "
                + "where " + where + " group by sight_id";
        Cursor cursor = database.rawQuery(sql, null);
        Map<Integer, Float> map = new LinkedHashMap<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            float sum = (float) cursor.getDouble(1);
            map.put(id, sum);
        }
        cursor.close();
        return map;
    }

    //获取所有景点 id→name 的映射
    public Map<Integer, String> getAllSightNames() {
        Map<Integer, String> ret = new LinkedHashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("Sight", new String[]{"id", "name"}, null, null, null, null, "id ASC");
        while (c.moveToNext()) {
            @SuppressLint("Range")
            int id = c.getInt(c.getColumnIndex("id"));
            @SuppressLint("Range")
            String name = c.getString(c.getColumnIndex("name"));
            ret.put(id, name);
        }
        c.close();
        return ret;
    }

    //根据景点标识查询景点名
    public String getSightName(int id) {
        database = this.getWritableDatabase();
        String name = "";
        Cursor cursor = database.rawQuery("select name from Sight where id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }

    //统计指定状态的投诉数
    public int countComplaintsByStatus(String status) {
        database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(
                "select COUNT(*) from Complaint where status = ?",
                new String[]{status}
        );
        int cnt = 0;
        if (cursor.moveToFirst()) {
            cnt = cursor.getInt(0);
        }
        cursor.close();
        return cnt;
    }

    //统计所有投诉数
    public int countAllComplaints() {
        database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("select COUNT(*) from Complaint", null);
        int cnt = 0;
        if (cursor.moveToFirst()) {
            cnt = cursor.getInt(0);
        }
        cursor.close();
        return cnt;
    }

    //数据库版本更新
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists User");
        db.execSQL("drop table if exists Admin");
        db.execSQL("drop table if exists Complaint");
        db.execSQL("drop table if exists Alarm");
        db.execSQL("drop table if exists Guide");
        db.execSQL("drop table if exists Docent");
        db.execSQL("drop table if exists Coach");
        db.execSQL("drop table if exists Sight");
        db.execSQL("drop table if exists SightComment");
        db.execSQL("drop table if exists SightPurchase");
        db.execSQL("drop table if exists VisitorStat");
        onCreate(db);
    }

    private void insertSightsInfo(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Sight (name, description, price, latitude, longitude, image) VALUES " +
                "('湖北省博物馆', '湖北省博物馆筹建于1953年，坐落于湖北省武汉市武昌区东湖风景区，占地面积81909平方米，建筑" +
                "面积49611平方米，展厅面积13427平方米，有中国规模最大的古乐器陈列馆。湖北省博物馆现有馆藏文物26万余件(套)，以青" +
                "铜器、漆木器、简牍最有特色，其中国家一级文物945件(套)、国宝级文物16件(套)。越王勾践剑、曾侯乙编钟、郧县人头骨化石、元" +
                "青花四爱图梅瓶为该馆四大镇馆之宝。湖北省博物馆是全国八家中央地方共建国家级博物馆之一、国家一级博物馆、出土木漆器保护国" +
                "家文物局重点科研基地、国家AAAAA级旅游景区，也是湖北省规模最大、藏品最为丰富、科研实力最强的国家级综合性博物馆。" +
                "1960年，时任中华人民共和国副主席的董必武来馆视察，并亲笔题写馆名。2018年10月11日，入选“全国中小学生研学实践教育基地”" +
                "名单。', '60', '30.5614', '114.3655', 'bowuguan')," +
                "('东湖凌波门栈桥', '湖面波光粼粼，栈桥上可以远眺湖景，非常惬意。而且栈桥的建设和维护都相当不错，走起来很舒服。'," +
                " '20', '30.5434', '114.3713', 'zhanqiao')," +
                "('东湖樱园', '武汉东湖樱花园是赏樱的绝佳之地，樱花如雪般飘落，宛如仙境。湖面波光粼粼，微风拂面，樱花树茂密繁盛，粉色的花" +
                "瓣在阳光下如梦如幻。这里有浓厚的文化氛围，仿楚国的建筑风格独特，展示了楚国的历史和文化。梅园里的梅花与樱花相互映衬，形成" +
                "了独特的景观。尽管人多拥挤，但这里的美景令人陶醉，值得一去。', '40', '30.5450', '114.4093', 'yingyuan')," +
                "('东湖梅园', '东湖梅园（武汉东湖磨山梅园）为全国四大梅园，是全国著名的赏梅胜地，又是我国梅花研究中心所在地。内有妙香国、江南第一枝" +
                "、花溪、放鹤亭、梅友雕像、冷艳亭等景点，其中妙香国为中国梅文化馆所在地。东湖梅园创建于1956年，面积已扩大到800余亩，定植梅树2万余株" +
                "。梅园位于湖北省武汉市东湖风景名胜区磨山景区南麓，三面临水，回环错落，自成一体，周围有劲松修竹掩映，自然成为“岁寒三友”景观，是我国梅园" +
                "胜地之一。这里山青水绿，风景秀丽。早春时节，梅花盛开，繁花似锦，暗香四溢，前来赏梅、咏梅、画梅、摄梅的中外游人络绎不绝。'," +
                " '30', '30.5441', '114.4050', 'meiyuan')," +
                "('磨山景区', '磨山位于湖北省武汉市，是市民休闲娱乐的场所。磨山景区，是东湖风景名胜区的六个景区之一。她居东湖中心，三面环水，逶迤六峰。" +
                "东一峰，《湖北通志》载：“刘备郊天台，在磨儿山”。因为此峰形圆如磨，故称磨山。古名磨儿山，形圆如磨，故名。为沿东湖群山中主要山脉，三面环水，" +
                "六峰逶迤，长达8里，主峰高百余米，有“十里长湖，八里磨山”之称，风景极佳。山北有以楚文化为内涵的楚文化游览区；山南有以湖水地区乡土植物为主的" +
                "十三个植物专类园；西部山头有纪念朱德为东湖题词的朱碑亭。磨山景区从北开始，依次建有楚天极目、天台晨曦、常春花苑、朱碑亭等四景、健身项目－滑道。" +
                "它是磨山的一条动态风景线。武汉市东湖风景区磨山景区内的楚文化旅游区，有一个名为“惟楚有材”的景点，自从开放以来，以其丰富的楚文化韵味，受到了" +
                "游人的青昧。据载：历史上曾有“楚材晋用”、“楚材秦用”之说。杜鹃园位于磨山景区植物专类园区，南邻东湖荷园，北望楚文化游览区，东接异国花园，由原杜鹃园、" +
                "友谊林、桂花园组成”，杜鹃山种植杜鹃35万株，大型乔木之中植满不同种类不同色彩的杜鹃花，天然营造出“满山杜鹃红”的盛景，无论是近看还是远眺都非常壮观。" +
                "百亩杜鹃景观自然和谐，杜鹃花海中，数条观花栈道穿行其间，并有数个大型观景台供游客赏景。游客可徜徉花间栈道，可漫步林间溪水，可远观漫山花海，" +
                "可近赏花丛景石”，整个溪流自山间顺沿高低地势流至杜鹃园湖畔，全长160米，用2500吨野山石垒砌而成，溪水边人工造雾景观、不仅为杜鹃花创造了绝佳的生长环境，" +
                "更用溪水、水雾、杜鹃等元素营造出了“听声方知有溪水，雾中更见杜鹃红”的园林意境。', '60', '30.5505', '114.4128', 'moshan')," +
                "('武汉欢乐谷', '武汉欢乐谷，作为华中地区知名的主题公园，以其丰富的游乐设施和多样的娱乐项目吸引着众多游客。园内各种现代设施应有尽有，过山车、云霄飞车" +
                "等项目带来刺激的体验。此外，欢乐谷还有适合亲子游玩的项目，如迷你乐园和亲子乐园，让家庭成员在游玩中增进感情。', '80', '30.5944', '114.3960', 'huanlegu')," +
                "('听涛景区', '东湖听涛风景区位于东湖西北岸，是东湖风景区中最早建成开放的景区。该景区建有以纪念爱国诗人屈原为主体的景点群，还有新建的沙滩浴场，是夏夜人们纳凉避暑、" +
                "戏水休憩的好地方。景区中的主要风景旅游点有行吟阁、长天楼、九女墩纪念碑、湖光阁、寓言雕塑园、碧塘观鱼等。', '20', '30.5640', '114.3741', 'tingtao')," +
                "('屈原纪念馆', '屈原纪念馆是位于武汉市武昌区东湖景区内，环境很好，空气清新.', '35', '30.5695', '114.3733', 'quyuan')," +
                "('楚城', '城门在先秦时期是国家的象征，根据楚人喜临水居高筑城的特点，楚城选择在磨山北麓依山傍水的要津上。它是进入楚文化旅游区的第一景点，城门设计古拙、气势恢宏，" +
                "高23.4米，长105米，其中望楼高12.4米，城墙高11米，整个城由水门、陆门、城墙和烽火台组成，城门全部用红色岩石砌成。楚天每天举行一次迎宾仪式，迎宾队伍着楚装、" +
                "奏楚乐、行楚礼，迎候中外佳宾的到来。', '45', '30.5529', '114.4156', 'chucheng')," +
                "('落雁景区', '东湖落雁景区位于东湖的东北岸边，是一个湿地公园，以湖滨湿地、古树名木为特色。每年冬天，这里聚集着数以万计来自北方的鸬鹚，并常有大雁、野鸭、" +
                "白鹭等鸟类。景区还有雁洲索桥、鹊桥相会、鸳鸯合欢等人文景点，是当地人婚纱摄影的热门场所。', '10', '30.5580', '114.4381', 'luoyan')," +
                "('吹笛景区', '东湖吹笛景区位于东湖的东南部，是由之前的马鞍山森林公园改建而成，拥有6个自然村湾散落园，最高马鞍山海拔136米，有大小山峰17座，森林覆盖率高达80%，" +
                "湖畔有座吹笛山，山上森林茂密，可以爬山。', '15', '30.5189', '114.4477', 'chuidi')," +
                "('白马景区', '东湖落雁区的北面是白马风景区，区内有一白马洲。相传公元208年赤壁之战后，鲁肃转回夏口骑马过洲，战马陷泥而死，鲁肃含泪葬马于洲，" +
                "故此地称为白马洲。', '10', '30.5978', '114.4245', 'baima')," +
                "('沙滩景区', '武汉东湖沙滩景区拥有全亚洲最大的城市内湖，可以“看海”。最佳游玩天气是30度左右的阴天，这样既能享受沙滩的乐趣，又不会被晒得难受。'," +
                " '45', '30.5681', '114.3926', 'shatan')," +
                "('武汉植物园', '武汉植物园原名武汉植物研究所，筹建于1956年，1958年11月正式成立，为我国解放后中国科学院最早成立的植物园。', " +
                "'90', '30.5460', '114.4252', 'zhiwuyuan')," +
                "('湖光阁', '湖光阁位于东湖的听涛景区，东湖风景区历史悠久，景色宜人，湖光山色与人文景观融为一体，是武汉较大的湖泊之一，非常值得一游。', " +
                "'55', '30.5671', '114.3958', 'huguangge')," +
                "('东湖楚风园', '东湖楚风园是东湖绿道的起点，从这里出发可以前往磨山景区，也可以乘坐船只前往。', '35', '30.5773', '114.3776', 'chufengyuan')," +
                "('东湖牡丹园', '东湖牡丹园被誉为“江南牡丹第一园”，交通便利，是赏牡丹的好去处。园区内牡丹花品种繁多，颜色各异，还有亭台楼阁、曲桥流水等园林景观，文化底蕴深厚，" +
                "值得一游。', '30', '30.5729', '114.3731', 'mudanyuan')," +
                "('楚才园', '楚才园以刻石、铸铁、铸铜作圆铸33尊，平均高度5米，浮雕25组，总长252 米，宛若一长幅历史画卷，表现楚国的名人、重大事件和重大成就，集中展现楚国八百年" +
                "的开拓史、民族史、内政史、外交史、法制史、军事史、经济史、科技史、 交通史、学术史等等。楚才园因其主体直观性强的雕塑艺术和磅礴气势而独具风格，自成体系，堪称东湖楚" +
                "文化建设的经典之作。漫步楚才园，人们可从中品味楚文化博大精深的丰富内涵。', '40', '30.5534', '114.4168', 'chucaiyuan')," +
                "('桃花岛', '桃花岛的人不是很多，环境很舒服。在草坪上露营野餐，绿道骑自行车或散步都非常惬意。而且樱花盛开时，桃花岛的桃花非常美丽，拍照十分写意.', " +
                "'57', '30.5962', '114.4215', 'taohuadao')," +
                "('楚天台', '楚天台是东湖磨山楚文化游览区内的标志性建筑，按章华台之“层台累榭，三休乃至”的形制而建。层阶巨殿、高台耸立、依山傍水、屹立于磨山第二主峰上，" +
                "可与江南三大名楼媲美，楚天台高35.26米，楼顶上安置1.2米高铜凤一只，从湖畔凤标踏步345级而上，正面墙上镶有600多块天然大理石拼成“楚天仙境丹凤朝阳”图案，" +
                "成为楚天一绝。整楼建筑面积2260平方米，外五内六层，楚国出土大批文物复仿制品荟萃于台内，再现楚国艺术的楚风、楚韵、楚味。第一层为楚音乐文物展，" +
                "第二层为楚乐舞厅，第二层半内为楚国漆器艺术展河名人画家为楚天台的题词，第三层为展现古楚国名人的腊像厅，第四层为楚国绘画与书法展，第五层展出湖北随州" +
                "曾侯乙墓出土的二十八宿天文衣箱。“楚天台”匾牌为中国佛教协会会长赵朴初题写。', '66', '30.5513', '114.4161', 'chutiantai')"
        );
    }

    //实现数据库连接
    public void open() {
        //获取可写数据库
        database = this.getWritableDatabase();
    }

    //数据库连接断开
    public void close() {
        if (database != null) {
            database.close();
        }
    }
}
