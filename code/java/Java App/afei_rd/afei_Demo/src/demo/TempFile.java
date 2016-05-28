package demo;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
/**
 * ����: ������ʱ�ļ�(��ָ����·����)
 */
public class TempFile implements ActionListener
{
    private File tempPath;
    public static void main(String args[]){
        TempFile ttf = new TempFile();
        ttf.init();
        ttf.createUI();
    }
    //����UI
    public void createUI()
    {
        JFrame frame = new JFrame();
        JButton jb = new JButton("������ʱ�ļ�");
        jb.addActionListener(this);
        frame.add(jb,"North");
        frame.setSize(200,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    //��ʼ��
    public void init(){
        tempPath = new File("./temp");
        if(!tempPath.exists() || !tempPath.isDirectory())
        {
            tempPath.mkdir();  //��������ڣ��򴴽����ļ���
        }
    }
    //�����¼�
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            //��tempPath·���´�����ʱ�ļ�"mytempfileXXXX.tmp"
            //XXXX ��ϵͳ�Զ������������, tempPath��Ӧ��·��Ӧ���ȴ���
            File tempFile = File.createTempFile("mytempfile", ".txt", tempPath);
            System.out.println(tempFile.getAbsolutePath());
            FileWriter fout = new FileWriter(tempFile);
            PrintWriter out = new PrintWriter(fout);
            out.println("some info!" );
            out.close(); //ע�⣺���޴˹ر���䣬�ļ�������ɾ��
            System.out.println(tempFile.getAbsolutePath());
            Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "g:\\mail\\ת��_ [HZ SWD3 RELEASE] - ����.eml"});
            //tempFile.delete();
            tempFile.deleteOnExit();
        }
        catch(IOException e1)
        {
            System.out.println(e1);
        }
    }
}