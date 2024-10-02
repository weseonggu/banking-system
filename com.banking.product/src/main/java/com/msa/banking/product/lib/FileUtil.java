package com.msa.banking.product.lib;

public class FileUtil {
    public static String[] splitFileName(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return new String[]{fileName, ""}; // 확장자가 없는 경우
        }

        String name = fileName.substring(0, lastDotIndex);
        String extension = fileName.substring(lastDotIndex + 1);
        return new String[]{name, extension};
    }
}
