package com.like.rapidui.ui.icon.fonts.entypo;

import com.like.rapidui.ui.icon.Icon;
import com.like.rapidui.ui.icon.IconFontDescriptor;

public class EntypoModule implements IconFontDescriptor {

    @Override
    public String ttfFileName() {
        return "android-iconify-entypo.ttf";
    }

    @Override
    public Icon[] characters() {
        return EntypoIcons.values();
    }
}
