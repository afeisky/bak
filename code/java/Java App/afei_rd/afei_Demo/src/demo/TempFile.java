package demo;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
/**
 * 功能: 创建临时文件(在指定的路径下)
 */
public class TempFile implements ActionListener
{
    private File tempPath;
    public static void main(String args[]){
        TempFile ttf = new TempFile();
        ttf.init();
        ttf.createUI();
    }
    //创建UI
    public void createUI()
    {
        JFrame frame = new JFrame();
        JButton jb = new JButton("创建临时文件");
        jb.addActionListener(this);
        frame.add(jb,"North");
        frame.setSize(200,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    //初始化
    public void init(){
        tempPath = new File("./temp");
        if(!tempPath.exists() || !tempPath.isDirectory())
        {
            tempPath.mkdir();  //如果不存在，则创建该文件夹
        }
    }
    //处理事件
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            //在tempPath路径下创建临时文件"mytempfileXXXX.tmp"
            //XXXX 是系统自动产生的随机数, tempPath对应的路径应事先存在
            File tempFile = File.createTempFile("mytempfile", ".txt", tempPath);
            System.out.println(tempFile.getAbsolutePath());
            FileWriter fout = new FileWriter(tempFile);
            PrintWriter out = new PrintWriter(fout);
            out.println("some info!" );
            out.close(); //注意：如无此关闭语句，文件将不能删除
            System.out.println(tempFile.getAbsolutePath());
            Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "g:\\mail\\转发_ [HZ SWD3 RELEASE] - 副本.eml"});
            //tempFile.delete();
            tempFile.deleteOnExit();
        }
        catch(IOException e1)
        {
            System.out.println(e1);
        }
    }
}