//# Copyright (C) 2017 TCL Mobile
package tclpatch;

import java.util.ArrayList;

/**
 *
 * @author chaofei.wu.hz@tcl.com
 */
public class GitProject {
    private String path;    
    private String name;    
    private boolean find; 
    private boolean update; 
    private ArrayList<FileNode> fileLine;
    public GitProject(String _path, String _name,boolean _find) {    
        path =_path;
        name =_name;   
        find=_find;
    }    
    public String getPath() {    
        return path;    
    }  
    public String getName() {    
        return name;    
    }      
    public boolean getFind() {    
        return find;    
    } 
    public void setFind(boolean value) {    
        find=value;  
    } 
    public boolean getUpdate() {    
        return update; 
    } 
    public void setUpdate(boolean value) {    
        update=value;  
    } 
    public void setFileLine(ArrayList<FileNode> _fileLine) {    
        fileLine=_fileLine;
    }  
    public ArrayList<FileNode> getFileLine() {    
        return fileLine;    
    } 
}
