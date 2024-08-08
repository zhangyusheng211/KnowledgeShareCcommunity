package com.moxi.mogublog.admin;

import java.io.*;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 *
 * @author ZhiHu.Liu
 * @since 2021/12/2 16:37
 */
public class FileConvert {

    public static void convertFileEncoding(File srcFile, String srcCharset,
                                           String destFilePath, String destCharset, boolean isDeleteSrc) throws IOException {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;

        try {
            fis = new FileInputStream(srcFile);
            isr = new InputStreamReader(fis, srcCharset);

            // BufferedReader中defaultCharBufferSize = 8192.
            // 即：8192 × 2 byte = 16k
            // 若是utf-8,中文占3个字节，16K / 3  = 5461，即只要每行中文字符数 < 5461,读取的行数就是准确的，
            // 否则，可能会截断一行，多写入'\n',但这种情况一般不存在。
            // 如果源文件中最后一行没有换行符，转码后的文件最后会多写入一个换行符
            br = new BufferedReader(isr);

            // 以UTF-8格式写入文件,file.getAbsolutePath()即该文件的绝对路径,false代表不追加直接覆盖,true代表追加文件
            fos = new FileOutputStream(destFilePath + "\\" + srcFile.getName(), false);
            osw = new OutputStreamWriter(fos, destCharset);

            String str;

            // 创建StringBuffer字符串缓存区
            StringBuffer sb = new StringBuffer();

            // 通过readLine()方法遍历读取文件
            while ((str = br.readLine()) != null) {
                // 使用readLine()方法无法进行换行,需要手动在原本输出的字符串后面加"\n"或"\r"
                sb.append(str).append('\n');
            }
            osw.write(sb.toString());
            osw.flush();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
//		e.printStackTrace();
            throw e;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            throw e;
        } catch (IOException e) {
            // TODO: handle exception
            throw e;
        } finally {
            // 与同一个文件关联的所有输出流(输入流)，只需关闭一个即可
            if (null != fis)
                try {
                    fis.close();
                    fis = null;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            if (null != fos)
                try {
                    fos.close();
                    fos = null;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        if (isDeleteSrc) {
            if (srcFile.delete())
                System.out.println(srcFile.getAbsolutePath() + " file is already deleted.");
            else
                System.out.println(srcFile.getAbsolutePath() + " file delete fail.");
        }
    }

    public static void main(String[] args) throws IOException {
        String srcPathName = "D:\\java\\mogu_blog_business\\mogu_admin\\src\\main\\resources\\mogu_admin_gbk";
        String destPathName = "D:\\java\\mogu_blog_business\\mogu_admin\\src\\main\\resources\\script\\db\\groovy\\mogu_admin";
        File path = new File(srcPathName);
        for (File file : Objects.requireNonNull(path.listFiles())) {
            convertFileEncoding(file, "GBK", destPathName, "UTF-8", true);
        }
    }


}
