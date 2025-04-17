package org.acme.utils;

import static org.acme.utils.ExecUtil.run;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.smallrye.common.os.OS;

public final class CommandLineUtil {

    private CommandLineUtil() {
        // Utility class
    }

    public static String getFullCommandLine() {
        String os = System.getProperty("os.name").toLowerCase();
        String rawCmd = "";
        switch (OS.current()) {
          case LINUX:
            rawCmd = readProcCmdline();
          case MAC: 
             rawCmd = run("ps", "-p", getPid(), "-o", "command=");
             break;
          case WINDOWS: 
             rawCmd =  run("wmic", "process", "where", "ProcessId=" + getPid(), "get", "CommandLine");
             break;
            default:
        }

        try {
            if (os.contains("linux")) {
                rawCmd = readProcCmdline();
            } else if (os.contains("mac")) {
                rawCmd = run("ps", "-p", getPid(), "-o", "command=");
            } else if (os.contains("win")) {
                rawCmd = run("wmic", "process", "where", "ProcessId=" + getPid(), "get", "CommandLine");
            }
        } catch (Exception e) {
            return "ERROR retrieving command line: " + e.getMessage();
        }

        return absolutizeJarIfNeeded(rawCmd);
    }

    private static String readProcCmdline() {
        try {
          byte[] bytes = Files.readAllBytes(Path.of("/proc/self/cmdline"));
          return new String(bytes).replace('\0', ' ').trim();
        } catch (IOException e) {
            throw new RuntimeException("Error reading /proc/self/cmdline: " + e.getMessage(), e);
        }
    }

    private static String getPid() {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName(); // format: pid@host
        return jvmName.split("@")[0];
    }

    private static String absolutizeJarIfNeeded(String cmdline) {
        if (cmdline.contains("-jar")) {
            String[] parts = cmdline.split("\\s+");
            StringBuilder rebuilt = new StringBuilder();

            for (int i = 0; i < parts.length; i++) {
                if ("-jar".equals(parts[i]) && i + 1 < parts.length) {
                    String jarPath = parts[i + 1];
                    Path absolutePath = Paths.get(jarPath).toAbsolutePath();
                    rebuilt.append("-jar ").append(absolutePath).append(" ");
                    i++; // skip next (it's the jar path we just handled)
                } else {
                    rebuilt.append(parts[i]).append(" ");
                }
            }

            return rebuilt.toString().trim();
        }
        return cmdline;
    }
}
