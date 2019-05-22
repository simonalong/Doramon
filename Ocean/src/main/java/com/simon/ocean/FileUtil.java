package com.simon.ocean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

/**
 * @author zhouzhenyong
 * @since 2018/1/4 上午10:52
 */
@UtilityClass
public class FileUtil {

    private static final Charset CRYPTO_CHARSET = Charset.forName("UTF-8");

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

    public Boolean contain(String fileName){
        return new File(fileName).exists();
    }

    public void delete(String fileName){
        new File(fileName).delete();
    }

    /**
     * 向文件中写入对应的文件
     */
    public void writeFile(File file, String content) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content.getBytes(CRYPTO_CHARSET));
            outputStream.flush();
            outputStream.close();
        }
    }

    public BufferedReader readFile(File file) throws IOException {
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

    public String readUrl(String FileName) throws IOException {
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
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 获取文件的后缀
     * @param parenPath 文件的父目录：/xxxx/xx
     * @param fileName 不带后缀的文件名：test
     * @return /xxxx/xx/test.yml -> yml
     */
    public String getFilePostfix(String parenPath, String fileName) {
        File file = new File(parenPath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                File result = Stream.of(files)
                    .filter(f -> {
                        String fm = f.getName();
                        return fm.substring(0, fm.lastIndexOf(".")).equals(fileName);
                    }).findFirst().get();

                String rt = result.getName();
                return rt.substring(rt.lastIndexOf(".") + 1);
            }
        }
        return null;
    }
}
