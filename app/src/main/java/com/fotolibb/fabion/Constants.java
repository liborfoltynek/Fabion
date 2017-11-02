package com.fotolibb.fabion;

/**
 * Created by Libb on 30.10.2017.
 */

public class Constants {
    public static int RO_LOGIN = 5698;
    public static int RO_MONTHVIEW = 5699;

    public static int RC_EVENT_UPDATE = 443;
    public static int RC_EVENT_NEW = 442;
    public static  String getUrlService()
    {
        return  urlService;
    }

    public static String FAB_USER = "FabUser";
    public static String PAR_FUSER = "FUser";
    public static String PAR_FEVENT = "FEvent";

    public static void setUrlService(String url) {
        if (!urlServiceSet) {
            urlService = url;
            urlServiceSet = true;
        }
    }

    private static  String urlService;
    private static boolean urlServiceSet = false;
}
