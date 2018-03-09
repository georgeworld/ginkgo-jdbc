/*
 * Programming by: George <GeorgeNiceWorld@gmail.com>
 * Copyright (C) George(www.georgeinfo.com), All Rights Reserved.
 */
package mainentry;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JFrame;
import javax.swing.JLabel;

import jdbc.annotation.JdbcCommentItem;
import jdbc.annotation.JdbcComments;

/**
 * @author George <GeorgeNiceWorld@gmail.com>
 * @contact http://www.georgeinfo.com
 * @since 2012-11-20
 */
@JdbcComments({
        @JdbcCommentItem(dateTime = "2013-03-15 22:19", version = "v1.0.0", notes = {"从Georgeinfo-SimpleWeb项目中分离出来，当做作为一个Georgeinfo-JDBC项目，版本号从1.0.0开始。"}),
        @JdbcCommentItem(dateTime = "2013-03-29 17:36", version = "v1.0.1", notes = {"修改JDBC Dao部分及分页类部分，实现sql语句可以使用占位符变量功能。"}),
        @JdbcCommentItem(dateTime = "2013-10-30 12:00", version = "v1.6.0", notes = {"重构所有包路径，重新定位该项目，使得该项目的定位是：纯JDBC封装包。",
                "删除分页标签相关的类和.tld标签定义文件，",
                "将JDBC分页上下文对象继承通用分页标签库（Georgeinfo-Pagination）的GenericPaginationContext类。"}),
        @JdbcCommentItem(dateTime = "2013-12-20 13:36", version = "v1.6.6", notes = {"这段时间修改很多东西，今天记录如下：",
                "com.georgeinfo.jdbc.utils.AbstractSQL 新增；",
                "com.georgeinfo.jdbc.utils.SQL 新增；",
                "com.georgeinfo.jdbc.dao.support.CommonDao 修改；",
                "com.georgeinfo.jdbc.dao.support.ExePreSql 修改；",
                "com.georgeinfo.jdbc.dao.support.GSDao 修改；",
                "com.georgeinfo.jdbc.dao.transaction.TransactionContainer 修改"}),
        @JdbcCommentItem(dateTime = "2015-06-14 21:11", version = "v1.8.1", notes = {"增加了插入并获得刚插入记录的ID的dao方法；",
                "增加插入记录集并将刚插入的记录集查询出来的dao方法；",
                "增加其他优化改动。"}),
        @JdbcCommentItem(dateTime = "2015-08-17 10:56", version = "v1.9.0", notes = {"增加了BlockDao功能，使用块状调用来实现Dao功能；",
                "重构部分辅助类的包路径。"}),
        @JdbcCommentItem(dateTime = "2015-08-20 22:08", version = "v2.0.0", notes = {"Dao结构大重构。"}),
        @JdbcCommentItem(dateTime = "2015-09-02 16:25", version = "v2.0.1", notes = {"增加DatabaseTypeDef定义类；", "增加DBConfig定义类。"}),
        @JdbcCommentItem(dateTime = "2015-09-05 22:50", version = "v2.1.1", notes = {"重构了大部分Dao方法，使得逻辑更加严谨。"}),
        @JdbcCommentItem(dateTime = "2015-10-26 16:52", version = "v2.1.2", notes = {"修正了MySQ下，TINYINT字段读取出来解析成boolean的bug；DataRow类，增加了getShort(key)方法。"}),
        @JdbcCommentItem(dateTime = "2018-03-09 22:31", version = "v2.2.0", notes = {"增加了test测试包及相关测试类，移除了多余的类库依赖。",
                "将闭源版(Georgeinfo-JDBC)相关的信息移除，修改项目结构，开源出来，更名为“Ginkgo-JDBC”，意思是“银杏树JDBC框架”。"})
})
public class JdbcMain {

    public static final String VERSION = "v2.2.0";
    public static final String UPDATE_DATE_TIME = "2018-03-09 22:31";

    public JdbcMain() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle("Ginkgo-JDBC");
        mainFrame.setSize(599, 300);
        mainFrame.setLayout(new java.awt.BorderLayout());

        JLabel topLabel = new JLabel("银杏树精简 JDBC 框架 " + getVersion());
        topLabel.setFont(new java.awt.Font("宋体", 1, 18));
        topLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(0, 102, 102), new java.awt.Color(0, 204, 204)));

        JLabel bottomLabel = new JLabel("(C)www.georgeinfo.com");
        bottomLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                openUrl("http://www.georgeinfo.com");
            }

            public void mousePressed(MouseEvent e) {
//                openUrl("http://www.georgeinfo.com");
            }

            public void mouseReleased(MouseEvent e) {
//                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseEntered(MouseEvent e) {
//                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseExited(MouseEvent e) {
//                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        bottomLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottomLabel.setForeground(Color.BLUE);

//        JTextArea changeLogArea = new JTextArea();
//        changeLogArea.setText(IrapidCommentsTool.getCommentsString(IrapidMain.class));
//        changeLogArea.setFont(new java.awt.Font("宋体", 0, 18));
//        JScrollPane mainScrollPane = new JScrollPane();
//        mainScrollPane.setViewportView(changeLogArea);
        mainFrame.add(topLabel, java.awt.BorderLayout.NORTH);
        mainFrame.add(new JdbcChangeLogPanel(JdbcMain.class), java.awt.BorderLayout.CENTER);
        mainFrame.add(bottomLabel, java.awt.BorderLayout.SOUTH);

        mainFrame.setAlwaysOnTop(true);
        setWindowCenter(mainFrame);
        mainFrame.setVisible(true);
    }

    public static String getVersion() {
        return "[" + VERSION + "]  build [" + UPDATE_DATE_TIME + "]";
    }

    /*
     * 使窗口居中显示
     */
    public static void setWindowCenter(Window window) {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        int w = window.getWidth();
        int h = window.getHeight();
        window.setLocation((width - w) / 2, (height - h) / 2);
    }

    public static void openUrl(String urlStr) {
        try {
            //帮助
            URI url = new URI(urlStr);
            java.awt.Desktop.getDesktop().browse(url);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }
}
