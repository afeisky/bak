package tctpatch;

/**
 *
 * @author chaofei.wu.hz@tcl.com
 */
//# Copyright (C) 2017 TCL Mobile
public class FileNode {

    private String filename;
    private String type; // "A"/"M"/"D" Add/Modiry/Delete
    private boolean isSelect;

    FileNode(String _filename) {
        filename = _filename;
    }

    public String getFileName() {
        return filename;
    }

    public boolean getSelect() {
        return isSelect;
    }

    public void setSelect(boolean value) {
        isSelect = value;
    }

    public String getType() {
        return type;
    }


    public void setType(String value) {
         type = value;
          
    }
}
