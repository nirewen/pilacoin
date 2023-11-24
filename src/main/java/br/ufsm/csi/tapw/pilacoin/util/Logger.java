package br.ufsm.csi.tapw.pilacoin.util;

import org.slf4j.LoggerFactory;

public class Logger {
    private static org.slf4j.Logger getInstance() {
        return LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[3].getClassName());
    }

    public static void log(String message) {
        getInstance().info(message);
    }

    public static void logBox(String message) {
        String[] lines = message.split("\n");
        Integer boxWidth = 48;

        getInstance().info("╭" + "─".repeat(boxWidth + 2) + "╮");

        for (String line : lines) {
            getInstance().info("│ " + String.format("%-" + boxWidth + "s", line) + " │");
        }

        getInstance().info("╰" + "─".repeat(boxWidth + 2) + "╯");
    }
}
