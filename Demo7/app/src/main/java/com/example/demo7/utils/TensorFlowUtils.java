package com.example.demo7.utils;

/**
 */

public class TensorFlowUtils {
    public static int ClassNumber(String str) {
        switch (str) {
            case "closeeyes":
                return 1;
            case "facepalm":
                return 2;
            case "normal":
                return 3;
            case "phone":
                return 4;
            case "smoke":
                return 5;
            case "cover":
                return 6;
            case "nobody":
                return 7;
            default:
                return -1;
        }
    }
}
