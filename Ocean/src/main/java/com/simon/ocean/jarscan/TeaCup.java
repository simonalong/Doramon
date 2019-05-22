package com.simon.ocean.jarscan;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Objects;

/**
 * 读取外部jar包用的工具类，只能读取jar包内的class文件，如果jar包内还含有jar包，则读取不了
 *
 * @author zhouzhenyong
 * @since 2018/7/17 下午3:48
 */
public final class TeaCup {

    private Method addURL;
    private URLClassLoader loader;
    private static TeaCup teaCup = new TeaCup();

    private TeaCup() {
        addURL = initAddMethod();
        loader = URLClassLoader.class.cast(ClassLoader.getSystemClassLoader());
    }

    public static TeaCup getInstance() {
        return teaCup;
    }

    /**
     * 初始化addUrl 方法.
     *
     * @return 可访问addUrl方法的Method对象
     */
    private Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            add.setAccessible(true);
            return add;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> loadClass(String clsName) throws ClassNotFoundException {
        return loader.loadClass(clsName);
    }

    /**
     * 加载文件下面中的所有jar
     *
     * @param jarRootPath jar包所在文件路径，如：/local/test/work/tea.jar
     */
    public void read(String jarRootPath) {
        File file = new File(jarRootPath);
        readFile(file);
    }

    /**
     * 加载文件下面中的所有jar
     *
     * @param url jar包所在文件的路径
     */
    public void read(URL url) {
        try {
            readFile(new File(url.toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载文件下面中的所有jar
     *
     * @param file jar包所在文件的路径
     */
    public void read(File file) {
        readFile(file);
    }

    /**
     * 加载jar包中的所有类
     *
     * @param jarPath jar包所在文件的路径组
     */
    public void read(String... jarPath) {
        Arrays.stream(jarPath).map(File::new).forEach(this::addURL);
    }

    /**
     * 加载jar包中的所有类
     *
     * @param urls jar包所在文件的路径组
     */
    public void read(URL... urls) {
        Arrays.stream(urls).forEach(this::addURL);
    }

    /**
     * 加载jar包中的所有类
     *
     * @param jarPath jar包所在文件的路径组
     */
    public void read(File... jarPath) {
        Arrays.stream(jarPath).forEach(this::addURL);
    }

    private void addURL(File file) {
        try {
            addURL.invoke(loader, file.toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addURL(URL url) {
        try {
            addURL.invoke(loader, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param rootFile 当前目录的文件
     */
    private void readFile(File rootFile) {
        if (rootFile.isDirectory()) {
            Arrays.stream(Objects.requireNonNull(rootFile.listFiles())).forEach(this::readFile);
        } else {
            if (rootFile.getAbsolutePath().endsWith(".jar") || rootFile.getAbsolutePath().endsWith(".zip")) {
                addURL(rootFile);
            }
        }
    }
}
