package com.innowise.camelcommon.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CamelRouteConstant {

    public static final String DOWNLOAD_FROM_S3_ROUTE = "direct:fromS3";
    public static final String DOWNLOAD_FROM_LOCAL_STORAGE_ROUTE = "direct:fromLocalStorage";
    public static final String DOWNLOAD_FILE_ROUTE = "direct:downloadFile";

}
