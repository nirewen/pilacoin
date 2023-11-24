package br.ufsm.csi.tapw.pilacoin.util;

import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Logger {
    private static org.slf4j.Logger getInstance() {
        return LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[3].getClassName());
    }

    public static void log(String message) {
        getInstance().info(message);
    }

    public static void logBox(String message) {
        List<String> lines = Arrays.asList(message.split("\n"));
        int boxWidth = lines.stream().max(Comparator.comparingInt(String::length)).get().length();

        getInstance().info("╭" + "─".repeat(boxWidth + 2) + "╮");

        for (String line : lines) {
            if (line.startsWith("---") || line.endsWith("---")) {
                getInstance().info("├" + "─".repeat(boxWidth + 2) + "┤");

                continue;
            }

            getInstance().info("│ " + String.format("%-" + boxWidth + "s", line) + " │");
        }

        getInstance().info("╰" + "─".repeat(boxWidth + 2) + "╯");
    }
}
