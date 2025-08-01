package com.TominoCZ.FBP.util;

import com.TominoCZ.FBP.Tags;

import java.net.URI;
import java.net.URISyntaxException;

public class ModReference {

    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = Tags.MOD_NAME;
    public static final String VERSION = Tags.VERSION;
    public static final URI ISSUE;

    static {
        try {
            ISSUE = new URI("https://github.com/Red-Studio-Ragnarok/Fancier-Block-Particles/issues/new?assignees=JustDesoroxxx&labels=&template=bug_report.md&title=");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
