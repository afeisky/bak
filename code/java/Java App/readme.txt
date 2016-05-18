
从www.eclipse.org下载
eclipse-jee-mars-2-win32-x86_64.zip
下载
WB_v1.8.0_UpdateSite_for_Eclipse4.5.zip

解压eclipse
运行，
在菜单[Help]->install new software-->Work with [Add]->打开Add Repository对话框，选中WindowBuilder目录或压缩包。
安装完后，会在eclipse->new project 下面很多菜单中，有一个WindowBuilder->SWT Designer->SWT/JFace Java Project.

netbeans-8.zip 也可以用于SWT开发，但不如eclipse

 现在，我们班是准备好了，让我们编译上面的类。我已经保存了类FetchingEmail.java目录： /home/manisha/JavaMailAPIExercise. 我们需要 javax.mail.jar andactivation.jar 在classpath中。执行下面的命令从命令提示符编译类（两个罐子被放置在 /home/manisha/目录下）：

javac -cp /home/manisha/activation.jar:/home/manisha/javax.mail.jar: FetchingEmail.java

现在，这个类被编译，执行下面的命令来运行：

java -cp /home/manisha/activation.jar:/home/manisha/javax.mail.jar: FetchingEmail


 public static void main(String[] args) {
	if (args.length != 5) {
	    System.out.println("usage: java sendfile <to> <from> <smtp> <file> true|false");
	    System.exit(1);
	}
	String to = args[0];
	String from = args[1];
	String host = args[2];
	String filename = args[3];
	boolean debug = Boolean.valueOf(args[4]).booleanValue();
	}
}