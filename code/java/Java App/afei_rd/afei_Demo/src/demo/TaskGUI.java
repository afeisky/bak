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
	private Task task = new Task(this);// Task�Ǻ�̨�����࣬��Ҫ����һ��TaskGUI���Ͳ���
	// �����������Ϊ���ʵ������
	private Text taskCountText;// ���������������ı���
	private Button startButton;// ��ʼ
	private Button stopButton;// ����
	private ProgressBar progressBar;// ��ʾ��������
	private Text consoleText;// ��ʾ��ǰ��̨���ȵ���Ϣ��
	// ������

	public static void main(String[] args) {
		try {
			TaskGUI window = new TaskGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ǰ̨ҳ���ִ�з�������ʾ���ɲ�����ǰ̨���桢
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
		startButton.addSelectionListener(new SelectionAdapter() {// �����ʼ��ť
			public void widgetSelected(SelectionEvent e) {
				setButtonState(false);// �����2��Button�����仯
				// �õ������������߳�ʹ�õı���Ҫ������Ϊfinal
				String str = taskCountText.getText();
				final int taskCount = new Integer(str).intValue();
				// ���ý������ĸ���
				progressBar.setMaximum(taskCount - 1);
				consoleText.insert("back Thread run start... ...  ");
				// Ϊ��̨�¿�һ���̣߳����У���run��������������̨��start�������������߳��Զ�����
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
				task.stop();// ��ִ̨��stop������ʵ����Ҫ��̨����ֹͣ
			}
		});
		progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// ����2�������ã��ı������ʾ��ʽ����2�е����������ῴ����ȫ����Ϣ��
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

	public void setButtonState(boolean bFlag) {// ����ҳ���2����ť״̬
		startButton.setEnabled(bFlag);
		stopButton.setEnabled(!bFlag);
	}

	// Ϊ��̨ȡ��ҳ�����д�ļ���GET����
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
		private TaskGUI gui;// ͨ���������õ�ǰ̨�������
		private boolean stopFlag;// �Ƿ�ֹͣ�ı�־
		// ������ ȡ��ǰ̨�������

		public Task(TaskGUI taskGUI) {
			this.gui = taskGUI;
		}

		public void stop() {
			stopFlag = true;
		}

		// ����ǰ̨run������ִ�����ݣ����������������ǰ̨new���Ǹ��߳�����
		public void start(int taskCount) {
			stopFlag = false;// ��ִ��״̬��ʼ��ִ��
			insertConsoleText("backGO start ... ...  ");
			for (int i = 0; i < taskCount; i++) {
				if (stopFlag) {// ���stop��ť���������Ϊtrue������ѭ��
					break;
				}
				try {// ÿ��1��һ��ѭ��
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// ҳ���ϵ���Ϣ�ۼ�
				insertConsoleText("task" + (i + 1) + "the end ");
				// �ƶ��������Ľ���
				moveProgressBar(i);
			}
			insertConsoleText("the thread end of the task!! ");
			setTaskGUIButtonState(true);
		}

		// �޸�ҳ�水ť��״̬
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
