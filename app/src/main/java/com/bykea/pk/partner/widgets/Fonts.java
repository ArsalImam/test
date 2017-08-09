package com.bykea.pk.partner.widgets;

public enum Fonts {
    Open_Sans_Regular("open_sans_regular.ttf"),
    Roboto_Italic("roboto_italic.ttf"),
    Roboto_light("roboto_light.ttf"),
    Roboto_Light_Italic("roboto_light_italic.ttf"),
    Roboto_Medium("roboto_medium.ttf"),
    Roboto_Medium_Italic("roboto_medium_italic.ttf"),
    Roboto_Regular("roboto_regular.ttf"),
    Roboto_Thin("roboto_thin.ttf"),
    Roboto_Thin_Italic("roboto_thin_italic.ttf"),
    Signika_Regular("signika_regular.ttf"),
    Museo_Sans("MuseoSans_1.otf"),
    Roboto_Bold("roboto_bold.ttf"),
    Open_Sans_light("open_sans_light.ttf");

    private final String name;

    Fonts(String s) {
        name = s;
    }

    public String getName() {
        return name;
    }
}