package com.demo.screencapture.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

/**
 * Copyright (c) 2018, 数字多⽹网络技术有限公司 All rights reserved.
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/21
 */

public class ReadAndWriterFileUtils {

    public static void write1(String file, String conent, boolean isAppend) {
        BufferedWriter out = null;
        try {
            //append 是否追加到文件尾，默认false
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, isAppend)));
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 追加 FileWriter
     */
    public static void write2(String fileName, String content) {
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(fileName, true);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用RandomAccessFile
     */
    public static void write3(String fileName, String content) {
        RandomAccessFile randomFile = null;
        try {
            // 打开一个随机访问文件流，按读写方式
            randomFile = new RandomAccessFile(fileName, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void write4(){
        try {
        // 创建字节输出流对象
        // FileOutputStream fos = new FileOutputStream("fos3.txt");
        // 创建一个向具有指定name的文件中写入数据的输出文件流。如果第二个参数为true,则将字节写入文件末尾处，而不是写入文件开始处
        FileOutputStream fos2 = new FileOutputStream("fos3.txt", true);// 第二個参数为true表示程序每次运行都是追加字符串在原有的字符上

        // 写数据
        for (int i = 0; i < 3; i++) {
            fos2.write(("hello" + i).getBytes());
            fos2.write("\r\n".getBytes());// 写入一个换行
        }
        // 释放资源
        fos2.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        try {
            File file = new File("d://text.txt");
            if (file.createNewFile()) {
                System.out.println("Create file successed");
            }
            write1("d://text.txt", "123ab",false);
            write2("d://text.txt", "123qq");
            write3("d://text.txt", "123ww");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

}
