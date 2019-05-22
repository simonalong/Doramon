package com.simon.ocean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;


/**
 * 调用shell 命令
 *
 * @author zhouzhenyong
 * @since 2018/6/28 下午9:09
 */
@UtilityClass
public class ShellUtil {

    /**
     * 执行shell 命令
     *
     * @param shellStr shell 命令，如：open -e fileName
     */
    public void call(String shellStr) {
        try {
            Process process = Runtime.getRuntime().exec(shellStr);
            int exitValue = process.waitFor();
            if (0 != exitValue) {
                System.out.println("call shell failed. error code is :" + exitValue);
            }
        } catch (Throwable e) {
            System.out.println("call shell failed. " + e);
        }
    }

    /**
     * 执行有返回值的shell
     *
     * @param shellStr shell 命令，如：ls -al
     * @return 命令执行后的返回值
     */
    public String callShell(String shellStr) {
        Process process = null;
        List<String> processList = new ArrayList<String>();
        try {
            process = Runtime.getRuntime().exec(shellStr);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                processList.add(line);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder stringBuilder = new StringBuilder();

        processList.forEach(str -> stringBuilder.append(str).append("\n"));
        return stringBuilder.toString();
    }
}
