package com.innowise.soundfilemicroservice.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CamelConstant {

    public static final String UPLOAD_TO_SQS_ROUTE = "direct:toSQS";
    public static final String UPLOAD_S3_ROUTE = "direct:toS3";
    public static final String UPLOAD_TO_LOCAL_STORAGE_ROUTE = "direct:toLocalStorage";
    public static final String DOWNLOAD_FILE_ROUTE = "direct:downloadFile";

}
