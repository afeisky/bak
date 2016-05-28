package demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TaskGUI {
	private Display display = Display.getDefault();
	private Shell shell = new Shell();
	private Task task = new Task(this);// Task是后台处理类，需要传入一个TaskGUI类型参数
	// 将界面组件设为类的实例变量
	private Text taskCountText;// 可输入任务数的文本框
	private Button startButton;// 开始
	private Button stopButton;// 结束
	private ProgressBar progressBar;// 显示的任务条
	private Text consoleText;// 显示当前后台进度的信息条
	// 主函数

	public static void main(String[] args) {
		try {
			TaskGUI window = new TaskGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 前台页面的执行方法，显示出可操作的前台界面、
	public void open() {
		shell.setSize(300, 300);
		shell.setLayout(new GridLayout());
		Group group = new Group(shell, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(4, false));

		new Label(group, SWT.NONE).setText("taskCount:");
		taskCountText = new Text(group, SWT.BORDER);
		taskCountText.setText("10");
		taskCountText.setLayoutData(new GridData(100, -1));
		taskCountText.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) { // only input NO.
				e.doit = "0123456789".indexOf(e.text) >= 0;
			}
		});
		startButton = new Button(group, SWT.PUSH);
		startButton.setText("GO");
		startButton.addSelectionListener(new SelectionAdapter() {// 点击开始按钮
			public void widgetSelected(SelectionEvent e) {
				setButtonState(false);// 点击后，2个Button发生变化
				// 得到任务数，多线程使用的变量要求类型为final
				String str = taskCountText.getText();
				final int taskCount = new Integer(str).intValue();
				// 设置进度条的格数
				progressBar.setMaximum(taskCount - 1);
				consoleText.insert("back Thread run start... ...  ");
				// 为后台新开一个线程，运行，当run方法结束（即后台的start（）结束），线程自动销毁
				new Thread() {
					public void run() {
						task.start(taskCount);
					}
				}.start();
				consoleText.insert("back Thread run end... ...  ");
			}
		});
		stopButton = new Button(group, SWT.PUSH);
		stopButton.setText("STOP");
		stopButton.setEnabled(false);
		stopButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				task.stop();// 后台执行stop方法，实际是要后台任务停止
			}
		});
		progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// 下面2个是设置，文本框的显示格式，第2行的如果不加则会看不到全部信息了
		consoleText = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		consoleText.setLayoutData(new GridData(GridData.FILL_BOTH));
		shell.layout();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void setButtonState(boolean bFlag) {// 设置页面的2个按钮状态
		startButton.setEnabled(bFlag);
		stopButton.setEnabled(!bFlag);
	}

	// 为后台取得页面组件写的几个GET方法
	public Shell getShell() {
		return shell;
	}

	public Text getConsoleText() {
		return consoleText;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public class Task {
		private TaskGUI gui;// 通过构造器得到前台界面对象
		private boolean stopFlag;// 是否停止的标志
		// 构造器 取得前台界面对象

		public Task(TaskGUI taskGUI) {
			this.gui = taskGUI;
		}

		public void stop() {
			stopFlag = true;
		}

		// 就是前台run方法的执行内容，这个方法结束，则前台new的那个线程销毁
		public void start(int taskCount) {
			stopFlag = false;// 将执行状态初始化执行
			insertConsoleText("backGO start ... ...  ");
			for (int i = 0; i < taskCount; i++) {
				if (stopFlag) {// 点击stop按钮则这个属性为true，跳出循环
					break;
				}
				try {// 每隔1秒一次循环
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 页面上的信息累加
				insertConsoleText("task" + (i + 1) + "the end ");
				// 移动进度条的进度
				moveProgressBar(i);
			}
			insertConsoleText("the thread end of the task!! ");
			setTaskGUIButtonState(true);
		}

		// 修改页面按钮的状态
		private void setTaskGUIButtonState(final boolean bFlag) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					gui.setButtonState(bFlag);
				}
			});
		}

		private void moveProgressBar(final int progress) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					gui.getProgressBar().setSelection(progress);
				}
			});
		}

		private void insertConsoleText(final String str) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					gui.getConsoleText().insert(str);
				}
			});
		}
	}
}
