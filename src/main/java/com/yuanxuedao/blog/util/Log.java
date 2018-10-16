package com.yuanxuedao.blog.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {
    public static  Logger logger = Logger.getLogger(Log.class);
    static {
        PropertyConfigurator.configure("/Users/cloud/Desktop/frontEndTestCode/blog/src/main/resources/log4j.properties");
    }

}
