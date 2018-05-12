package com.like.rapidui.ui.icon.fonts.material;

import com.like.rapidui.ui.icon.Icon;
import com.like.rapidui.ui.icon.IconFontDescriptor;

public class MaterialModule implements IconFontDescriptor {

    @Override
    public String ttfFileName() {
        return "android-iconify-material.ttf";
    }

    @Override
    public Icon[] characters() {
        return MaterialIcons.values();
    }
}
