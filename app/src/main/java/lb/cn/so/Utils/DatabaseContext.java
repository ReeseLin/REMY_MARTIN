package lb.cn.so.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/3/29
 *  Des：封装的一个Context，主要作用是改变数据库原本的存放位置
 */
public class DatabaseContext extends ContextWrapper {

    public DatabaseContext(Context base) {
        super(base);
    }

    @Override
    public File getDatabasePath(String name) {
        //判断是否存在sd卡
        boolean sdExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (!sdExist) {
            return null;
        } else {
            //获取sd卡路径
            String dbDir = Environment.getExternalStorageDirectory().getPath();
            File file1 = new File(dbDir, "1ASO");
            File file = new File(file1, name);
            //判断目录是否存在，不存在则创建该目录
            if (!file1.exists()) {
                file1.mkdirs();
            }
            //数据库文件是否创建成功
            boolean isFileCreateSuccess = false;
            //判断文件是否存在，不存在则创建该文件
            if (!file.exists()) {
                try {
                    isFileCreateSuccess = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                isFileCreateSuccess = true;
            }
            //返回数据库文件对象
            if (isFileCreateSuccess) {
                return file;
            } else {
                return null;
            }
        }

    }

    /**
     * Android 4.0会调用此方法获取数据库。
     * @param name
     * @param mode
     * @param factory
     * @param errorHandler
     * @return
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }
}
