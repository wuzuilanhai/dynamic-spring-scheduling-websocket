package com.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by zhanghaibiao on 2018/1/21.
 */
public class JShell {

    private static final String RUNNING_SHELL_FILE = "deploy.sh";

    private static final String OS_NAME = "os.name";

    private static final String WINDOWS = "windows";

    private static final String CHMOD_COMMAND = "/bin/chmod";

    private static final String CHMOD_COMMAND_ARGS = "755";

    private static final String SH_COMMAND = "sh";

    public static void main(String[] args) {
        executeShell("dmoz", "/root/tmp/tutorial");
    }

    private static void executeShell(String spider, String dir) {
        boolean window = System.getProperty(OS_NAME).toLowerCase().startsWith(WINDOWS);
        String homeDirectory = JShell.class.getClassLoader().getResource("").getPath();
        copyFile(homeDirectory);
        ProcessBuilder builder = new ProcessBuilder();
        if (!window) {
            builder.command(CHMOD_COMMAND, CHMOD_COMMAND_ARGS, RUNNING_SHELL_FILE);
            builder.command(SH_COMMAND, RUNNING_SHELL_FILE, spider, dir);
            builder.directory(new File(homeDirectory));
            try {
                Process process = builder.start();
                StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
                streamGobbler.run();
                int exitCode = process.waitFor();
                assert exitCode == 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void copyFile(String directory) {
        File dest = new File(directory.concat(RUNNING_SHELL_FILE));
        if (dest.exists()) return;
        try {
            Files.copy(JShell.class.getClassLoader().getResourceAsStream(RUNNING_SHELL_FILE), dest.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
