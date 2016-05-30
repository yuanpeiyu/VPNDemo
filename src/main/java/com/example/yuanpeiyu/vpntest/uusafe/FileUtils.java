package com.example.yuanpeiyu.vpntest.uusafe;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {

    public static String readStream(InputStream inputStream) {
        final int bufferSize = 1024;
        final byte[] buffer = new byte[bufferSize];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int count;
        try {
            while ((count = inputStream.read(buffer, 0, bufferSize)) != -1) {
                if (count > 0) {
                    bos.write(buffer);
                }
            }
            byte[] data = bos.toByteArray();
            return new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //do nothing
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable e) {
            }
        }
    }

    /**
     * Writes a file to Disk.
     * This is an I/O operation and this method executes in the main thread, so it is recommended to
     * perform this operation using another thread.
     *
     * @param file The file to write to Disk.
     */
    public static boolean writeToFile(File file, String fileContent) {
        try {
            writeToFile(file, fileContent, false);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void writeToFile(File file, String fileContent, boolean isAppend) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, isAppend);
            writer.write(fileContent);
        } finally {
            closeQuietly(writer);
        }
    }

    public static List<String> readStreamLines(InputStream inputStream) {
        List<String> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        do {
            try {

                line = reader.readLine();
                if (line != null) {
                    list.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        } while (line != null);
        return list;
    }

    public static boolean delDir(File dir, boolean selfDelete) {
        if (dir == null)
            return false;
        if (!dir.exists()) {
            // 如果文件不存在,直接返回true
            return true;
        }
        if (!dir.isDirectory()) {
            // 如果文件不是文件夹，返回false
            return false;
        }
        // 现在文件存在且是文件夹
        File[] fl = dir.listFiles();

        if (fl == null)
            return false;

        for (File file : fl) {
            if (file.isFile()) {
                if (!file.delete()) {
                    // 如果删除文件失败，返回false
                    return false;
                }
            } else if (file.isDirectory()) {
                // 递归,如果删除文件夹失败，返回false
                if (!delDir(file, true)) {
                    return false;
                }
            }
        }
        if (!selfDelete) {
            return true;
        }
        return dir.delete();
    }

    /**
     * 解压指定zip文件
     *
     * @param unZipfile 压缩文件的路径
     * @param destDir   解压到的目录
     */
    public static boolean unZip(String unZipfile, String destDir) {// unZipfileName需要解压的zip文件名
        boolean isSuccess = false;
        FileOutputStream fileOut = null;
        File file;
        InputStream inputStream = null;
        byte[] buf = new byte[2048];
        int readBytes;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(unZipfile);
            for (Enumeration entries = zipFile.entries(); entries
                    .hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                file = new File(destDir + File.separator + entry.getName());

                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    // 如果指定文件的目录不存在,则创建
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    inputStream = zipFile.getInputStream(entry);
                    fileOut = new FileOutputStream(file);
                    while ((readBytes = inputStream.read(buf)) > 0) {
                        fileOut.write(buf, 0, readBytes);
                    }
                }
            }
            isSuccess = true;
        } catch (Exception e) {
        } finally {
            try {
                if (null != fileOut) {
                    fileOut.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != zipFile) {
                    zipFile.close();
                }
            } catch (Exception e) {
            }
        }
        return isSuccess;
    }

    public static boolean copyAsset(Context context, String assetFileName, File destFile) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        OutputStream os = null;
        try {
            is = am.open(assetFileName);
            os = new FileOutputStream(destFile);
            byte buffer[] = new byte[4 * 1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            os.flush();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            closeQuietly(is);
            closeQuietly(os);
        }
    }

    public static long getAssetFileLength(Context context, String assetFileName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        OutputStream os = null;
        try {
            is = am.open(assetFileName);
            return is.available();
        } catch (Exception e) {
            return -1;
        } finally {
            closeQuietly(is);
            closeQuietly(os);
        }
    }
}
