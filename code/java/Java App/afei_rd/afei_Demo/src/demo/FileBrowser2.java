package demo;

import java.io.File;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;

public class FileBrowser2 {

	Display display = new Display();
	Shell shell = new Shell(display);

	File rootDir;
	TreeViewer treeViewer;

	public FileBrowser2() {
		Action actionSetRootDir = new Action("Set Root Dir") {
			public void run() {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				String path = dialog.open();
				if (path != null) {
					treeViewer.setInput(new File(path));
				}
			}
		};
		final ViewerFilter directoryFilter = new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return ((File) element).isDirectory();
			}
		};
		Action actionShowDirectoriesOnly = new Action("Show directories only") {
			public void run() {
				if (!isChecked())
					treeViewer.removeFilter(directoryFilter);
				else
					treeViewer.addFilter(directoryFilter);
			}
		};
		actionShowDirectoriesOnly.setChecked(false);
		Action actionDeleteFile = new Action("Delete the selected file") {
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
				File file = (File) selection.getFirstElement();
				if (file == null) {
					System.out.println("Please select a file first.");
					return;
				}
				MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO);
				messageBox.setMessage("Are you sure to delete file: " + file.getName() + "?");
				if (messageBox.open() == SWT.YES) {
					File parentFile = file.getParentFile();
					if (file.delete()) {
						System.out.println("File has been deleted. ");
						// Notifies the viewer for update.
						treeViewer.refresh(parentFile, false);
					} else {
						System.out.println("Unable to delete file.");
					}
				}
			}
		};
		ToolBar toolBar = new ToolBar(shell, SWT.FLAT);
		ToolBarManager manager = new ToolBarManager(toolBar);
		manager.add(actionSetRootDir);
		manager.add(actionShowDirectoriesOnly);
		manager.add(actionDeleteFile);
		manager.update(true);

		shell.setLayout(new GridLayout());
		treeViewer = new TreeViewer(shell, SWT.BORDER);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.setContentProvider(new ITreeContentProvider() {
			public Object[] getChildren(Object parentElement) {
				File[] files = ((File) parentElement).listFiles();
				if (files == null)
					return new Object[0];
				return files;
			}

			public Object getParent(Object element) {
				return ((File) element).getParentFile();
			}

			public boolean hasChildren(Object element) {
				File file = (File) element;
				File[] files = file.listFiles();
				if (files == null || files.length == 0)
					return false;
				return true;
			}

			public Object[] getElements(Object inputElement) {
				File[] files = ((File) inputElement).listFiles();
				if (files == null)
					return new Object[0];
				return files;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				shell.setText("Now browsing: " + newInput);
			}
		});

		treeViewer.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return getIcon((File) element);
			}

			public String getText(Object element) {
				return ((File) element).getName();
			}
		});
		treeViewer.setSorter(new ViewerSorter() {
			public int category(Object element) {
				File file = (File) element;
				if (file.isDirectory())
					return 0;
				else
					return 1;
			}
		});

		treeViewer.setInput(new File("C:/temp"));
		shell.setSize(400, 260);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	// private File renameFile(File file, String newName) {
	// File dest = new File(file.getParentFile(), newName);
	// if (file.renameTo(dest)) {
	// return dest;
	// } else {
	// return null;
	// }
	// }
	private ImageRegistry imageRegistry;
	Image iconFolder = new Image(shell.getDisplay(), "C:/icons/web/go.gif");
	Image iconFile = new Image(shell.getDisplay(), "C:/icons/web/go.gif");

	private Image getIcon(File file) {
		if (file.isDirectory())
			return iconFolder;
		int lastDotPos = file.getName().indexOf('.');
		if (lastDotPos == -1)
			return iconFile;
		Image image = getIcon(file.getName().substring(lastDotPos + 1));
		return image == null ? iconFile : image;
	}

	private Image getIcon(String extension) {
		if (imageRegistry == null)
			imageRegistry = new ImageRegistry();
		Image image = imageRegistry.get(extension);
		if (image != null)
			return image;
		System.out.println("extension:"+extension);
		Program program = Program.findProgram(extension);
		ImageData imageData = (program == null ? null : program.getImageData());
		if (imageData != null) {
			image = new Image(shell.getDisplay(), imageData);
			imageRegistry.put(extension, image);
		} else {
			image = iconFile;
		}
		return image;
	}

	public static void main(String[] args) {
		new FileBrowser2();
	}
}