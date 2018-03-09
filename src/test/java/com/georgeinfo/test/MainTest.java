/*
 * Author: George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.test;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.georgeinfo.jdbc.dao.helper.BasicType;
import com.georgeinfo.jdbc.dao.helper.DataRow;
import com.georgeinfo.jdbc.dao.utils.DaoException;
import com.georgeinfo.jdbc.dao.utils.SqlParam;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.georgeinfo.test.entities.UserInfo;
import com.georgeinfo.test.jdbc.JdbcDao;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 主要功能测试入口
 *
 * @author George <Georgeinfo@163.com>
 */
public class MainTest {

    static {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        try {
            String log_config_path = System.getProperty("user.dir")
                    + System.getProperty("file.separator")
                    + "src"
                    + System.getProperty("file.separator")
                    + "test"
                    + System.getProperty("file.separator")
                    + "resources"
                    + System.getProperty("file.separator")
                    + "logback.xml";

            configurator.doConfigure(log_config_path);//加载logback配置文件  
        } catch (JoranException ex) {
            ex.printStackTrace();
        }
        //PropertyConfigurator.configure("/home/george/workspace/testjdbc/src/main/java/config/log4j.properties");//加载logj配置文件  
    }

    private static final Logger LOG = LoggerFactory.getLogger(MainTest.class);

    public MainTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // 单元测试方法在下面写
    @Test
    public void hello() throws SQLException {
        JdbcDao dao = new JdbcDao();
        dao.begin();
        try {

            //## 第一部分：【增】 ###################################################
            //插入第一个用户
            dao.execute("insert into user_info(name,creation_time) values(:name,:creationTime)", SqlParam.initParam("name", "张三").addParam("creationTime", new Date()));
            LOG.debug("### Insert the first user:【张三】");

            //插入第二个用户
            Long userId = dao.insertAndGetId("insert into user_info(name,creation_time) values(:name,:creationTime)", SqlParam.initParam("name", "李四").addParam("creationTime", new Date()));
            LOG.debug("### Insert the second user:【李四】");
            //打印第二个插入的用户的ID
            LOG.debug("### The second inserted user ID is:" + userId);

            //插入第三个用户
            Long userIdOfThirdUser = dao.insertAndGetId("insert into user_info(name,creation_time) values(:name,:creationTime)", SqlParam.initParam("name", "王五").addParam("creationTime", new Date()));
            LOG.debug("### Insert the third user:【王五】");

            //## 第二部分：【删】 ###################################################
            //删除第二个用户
            int r = dao.execute("delete from user_info where user_id = :user_id", SqlParam.initParam("user_id", userId));
            LOG.debug("删除结果：" + (r > -1));

            //## 第三部分：【改】 ###################################################
            //修改第三个用户的名字
            dao.execute("update user_info set name = :name where user_id = :userId", SqlParam.initParam("name", "王五改名为“王五弟弟”").addParam("userId", userIdOfThirdUser));

            //## 第四部分：【查】 ###################################################
            ArrayList<DataRow> rows = dao.queryList("select * from user_info where user_id != :param order by user_id asc", SqlParam.initParam("param", -100));
            if (rows == null || rows.isEmpty()) {
                LOG.debug("### No data.");
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                for (DataRow row : rows) {
                    LOG.debug("### User [" + row.getString("name") + "], creation time:" + sdf.format(row.getDate("creation_time")));
                }
            }

            //## 第五部分：【查询一个用户】 ##########################################
            UserInfo user = dao.queryOneEntity("select * from user_info where user_id = :userId", SqlParam.initParam("userId", userIdOfThirdUser), UserInfo.class);
            LOG.debug("### The user is :" + user.getName());

            //## 第六部分：【查询一个基本类型字段值】 ################################
            Integer userIdOfFirstUser = dao.queryOneBasicValue("select user_id from user_info where name = :name limit 0,1", SqlParam.initParam("name", "张三"), BasicType.INTEGER_WRAP);
            LOG.debug("### The user id of the first user is :" + userIdOfFirstUser);

            //提交事务（如果begin()与end()之间，只有查询类的SQL操作，则无需提交事务）
            dao.commit();
        } catch (DaoException ex) {
            LOG.error("### Exception when execute sql.", ex);
            //回滚事务
            dao.rollback();
        } finally {
            //最终，关闭数据库链接（将数据库连接释放回连接池）
            dao.end();
        }
    }
}
