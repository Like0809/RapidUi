package com.like.rapidui.ui.icon.fonts.fontawesome;


import com.like.rapidui.ui.icon.Icon;
import com.like.rapidui.ui.icon.IconFontDescriptor;

public class FontAwesomeModule implements IconFontDescriptor {

    @Override
    public String ttfFileName() {
        return "android-iconify-fontawesome.ttf";
    }

    @Override
    public Icon[] characters() {
        return FontAwesomeIcons.values();
    }
}
