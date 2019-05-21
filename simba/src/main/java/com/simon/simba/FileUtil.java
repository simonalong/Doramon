package com.simon.simba;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * @author zhouzhenyong
 * @since 2018/1/4 上午10:52
 */
@UtilityClass
public class FileUtil {

    private Charset CRYPTO_CHARSET = Charset.forName("UTF-8");

    /**
     * 通过绝对路径获取文件类
     */
    public static File getFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        file.setReadable(true);
        file.setWritable(true);
        return file;
    }

    /**
     * 向文件中写入对应的文件
     */
    public static void writeFile(File file, String content) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content.getBytes(CRYPTO_CHARSET));
            outputStream.flush();
            outputStream.close();
        }
    }

    public static BufferedReader readFile(File file) throws IOException {
        return Files.newBufferedReader(file.toPath(), CRYPTO_CHARSET);
    }

    /**
     * 读取资源文件中的内容
     * @param cls 类所在的位置
     * @param resourceFileName 资源文件中的位置比如：/script/base.groovy，其中前面一定要有"/"
     * @return 文件的字符数据
     */
    public String readFromResource(Class cls, String resourceFileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = cls.getResourceAsStream(resourceFileName);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } finally {
            inputStream.close();
            assert bufferedReader != null;
            bufferedReader.close();
        }
        return stringBuilder.toString();
    }

    /**
     * 通过文件的绝对路径读取文件信息
     */
    public String read(String filePath) throws IOException {
        FileReader fileReader;
        StringBuilder stringBuilder = new StringBuilder();
        fileReader = new FileReader(filePath);
        char[] cbuf = new char[32];
        int hasRead = 0;
        while ((hasRead = fileReader.read(cbuf)) > 0) {
            stringBuilder.append(cbuf, 0, hasRead);
        }
        return stringBuilder.toString();
    }

    /**
     * 向绝对路径中的文件写入对应的数据信息
     */
    public void write(String filePath, String content) throws IOException {
        writeFile(getFile(filePath), content);
    }

    public  String readUrl(String FileName) throws IOException {
        String read;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(FileName);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setConnectTimeout(5000);
            urlCon.setReadTimeout(5000);
            BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            while ((read = br.readLine()) != null) {
                stringBuilder.append(read);
            }
            br.close();
        } catch (IOException e) {

        }
        return stringBuilder.toString();
    }
    public static void appendFile(String filePath, List<String> content) {
        FileWriter fw = null;
        if (content == null || content.isEmpty()) {
            return;
        }
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File file = new File(filePath);
            if ((!file.exists()) &&
                    (!file.createNewFile())) {
                throw new IOException("create file '" + filePath +
                        "' failure.");
            }
            fw = new FileWriter(file, true);
            PrintWriter pw = new PrintWriter(fw);
            for (String str : content) {
                if(str != null && !"".equals(str)) {
                    pw.println(str);
                }
            }
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void appendFile(String filePath, String content) {
        List<String> contents = new ArrayList<>();
        contents.add(content);
        appendFile(filePath, contents);
    }
}
