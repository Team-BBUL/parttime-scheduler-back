package com.sidam_backend.resources;

import java.util.ArrayList;

public class ColorSet {

    final private ArrayList<String> colors = new ArrayList<>();
    private int idx;

    public ColorSet() {
        idx = 0;
        colorSetting();
    }

    public ColorSet(int n) {
        idx = n;
        colorSetting();
    }

    private void colorSetting() {
        colors.add("0xFF41E8FF");
        colors.add("0xFF2BD600");
        colors.add("0xFF1270B0");
        colors.add("0xFFFF5151");
        colors.add("0xFFC275FF");
        colors.add("0xFFD0D0D0");
        colors.add("0xFFFF0099");
        colors.add("0xFFEFAC00");
        colors.add("0xFFEB70FF");
        colors.add("0xFF01A9DB");
        colors.add("0xFFFACC2E");
        colors.add("0xFF1C1C1C");
        colors.add("0xFFB40404");
        colors.add("0xFFD358F7");
        colors.add("0xFF088A68");
        colors.add("0xFF0404B4");
        colors.add("0xFF3ADF00");
        colors.add("0xFF3B0B2E");
        colors.add("0xFFFFBF00");
    }

    private void increase() {
        idx++;
        idx = idx % colors.size();
    }

    public String getColor() {

        int index = idx;
        increase();

        return colors.get(index);
    }
}
