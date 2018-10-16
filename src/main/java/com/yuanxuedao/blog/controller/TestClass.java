package com.yuanxuedao.blog.controller;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class TestClass {

    public static Logger logger = Logger.getLogger(TestClass.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure("/Users/cloud/Desktop/frontEndTestCode/blog/src/main/resources/log4j.properties");

        logger.debug("1111");
        logger.info("1111");
        logger.warn("1111");
        logger.error("ssss");
    }
}
