# LitePalDemo
郭大神写的数据库框架LitePal的基本使用demo，LitePal地址https://github.com/LitePalFramework/LitePal。  
# 快速配置 
## 1.引入jia包 

```
dependencies {
    compile 'org.litepal.android:core:1.4.0'
}
```  
## 2.配置litepal.xml   
在 **assets** 文件夹下添加 **litepal.xml** ： 

```
<?xml version="1.0" encoding="utf-8"?>
<litepal>
    <!--数据库名字-->
    <dbname value="user" />
    <!--数据库版本-->
    <version value="1" />
    <list>
        <!--根据model映射创建表-->
        <mapping class="com.wuxiaosu.litepaldemo.model.User" />
    </list>
    <!--将数据库文件存放在sd卡（/sdcard/Android/data/<package name>/files/databases），调试时使用（internal，external）-->
    <!--<storage value="external" />-->
</litepal>
```  
## 3.配置LitePalApplication  
酱紫：
```
<manifest>
    <application android:name="org.litepal.LitePalApplication"
        ...
        >
        ...
    </application>
</manifest>
```  
或者：  

```
<manifest>
    <application android:name="org.litepal.MyOwnApplication"
        ...
        >
        ...
    </application>
</manifest>
```  
或者：  

```
public class MyOwnApplication extends AnotherApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
    ...
}
```
# 开始使用  
## 1.建表  
首先定义model（需要继承 **DataSupport** ），酱紫：

```
public class Album extends DataSupport {

    @Column(unique = true, defaultValue = "unknown")
    private String name;

    private float price;

    private byte[] cover;

    private List<Song> songs = new ArrayList<Song>();

    // generated getters and setters.
    ...
}
```  

```
public class Song extends DataSupport {

    @Column(nullable = false)
    private String name;

    private int duration;

    @Column(ignore = true)
    private String uselessField;

    private Album album;

    // generated getters and setters.
    ...
}
```

然后在**litepal.xml**中添加配置：  

```
<list>
    <mapping class="org.litepal.litepalsample.model.Album"></mapping>
    <mapping class="org.litepal.litepalsample.model.Song"></mapping>
</list>
```
表会在下次数据库操作时自动创建，也可以通过以下方式获取**SQLiteDatabase**实例：

```
SQLiteDatabase db = LitePal.getDatabase();
```  
## 2.更新表  
直接根据需要修改model（增、删字段）就好了：
```
public class Album extends DataSupport {

    @Column(unique = true, defaultValue = "unknown")
    private String name;

    //ignore
    @Column(ignore = true)
    private float price;

    private byte[] cover;

    private Date releaseDate;

    private List<Song> songs = new ArrayList<Song>();

    // generated getters and setters.
    ...
}
```  
然后在**litepal.xml**中增加版本号,下次操作数据库的时候就会自动升级了  
```
<version value="2"/>
```  
## 3.保存数据  
继承**DataSupport**可以直接使用 **save()** 方法，酱紫：  
```
Album album = new Album();
album.setName("album");
album.setPrice(10.99f);
album.setCover(getCoverImageBytes());
album.save();
```  
或者：
```
List<Album> newsList;  
...  
DataSupport.saveAll(newsList); 
```
## 4.修改数据  
用 **save()** 方法修改通过 **find()** 取出的数据：  
```
Album albumToUpdate = DataSupport.find(Album.class, 1);
albumToUpdate.setPrice(20.99f); // raise the price
albumToUpdate.save();
```  
或者根据id使用 **update()** 方法修改数据：

```
Album albumToUpdate = new Album();
albumToUpdate.setPrice(20.99f); // raise the price
albumToUpdate.update(id);
```  
或者通过条件使用 **updateAll()** 方法修改多条数据：

```
Album albumToUpdate = new Album();
albumToUpdate.setPrice(20.99f); // raise the price
albumToUpdate.updateAll("name = ?", "album");
```  
## 5.删除数据  
通过id使用 **DataSupport.delete()** 方法删除单条记录：
```  
DataSupport.delete(Song.class, id);
```  
或者通过条件使用 **DataSupport.deleteAll()** 方法删除多条记录:  
```
DataSupport.deleteAll(Song.class, "duration > ?" , "350");
```
如果对象经过序列化(**save()**)，也可以这样删除：

```
News news = new News();  
news.setTitle("这是一条新闻标题");  
news.setContent("这是一条新闻内容");  
news.save();  
...  
news.delete(); 
```
## 6.查询数据  
通过id查询单条数据：
```
Song song = DataSupport.find(Song.class, id);
```  
通过id查询多条数据:
```
List<Song> newsList = DataSupport.findAll(Song.class, 1, 3, 5, 7); 
```
也可以这样写：
```
long[] ids = new long[] { 1, 3, 5, 7 };  
List<Song> newsList = DataSupport.findAll(Song.class, ids); 
```
查询第一条数据
```
DataSupport.findFirst(Song.class); 
```
查询最后一条数据
```
DataSupport.findLast(Song.class); 
```
查询某张表下的所有数据：  
```
List<Song> allSongs = DataSupport.findAll(Song.class);
```
通过条件查询：
```
List<Song> songs = DataSupport.select("title", "content")
        .where("name like ?", "song%")
        .order("duration")
        .limit(10)
        .offset(10)
        .find(Song.class);
```  
# 多数据库支持  
动态创建数据库，酱紫：
```
LitePalDB litePalDB = new LitePalDB("demo2", 1);
litePalDB.addClassName(Singer.class.getName());
litePalDB.addClassName(Album.class.getName());
litePalDB.addClassName(Song.class.getName());
LitePal.use(litePalDB);
```
或者创建一个与相同配置的数据库，酱紫：
```
LitePalDB litePalDB = LitePalDB.fromDefault("newdb");
LitePal.use(litePalDB);
```
随时切换回默认数据库：
```
LitePal.useDefault();
```
删除数据库：
```
LitePal.deleteDatabase("newdb");
```
更多详情郭大神博客http://blog.csdn.net/sinyu890807/article/category/2522725  
LitePal地址https://github.com/LitePalFramework/LitePal
