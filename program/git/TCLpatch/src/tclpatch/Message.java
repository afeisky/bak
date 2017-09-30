//# Copyright (C) 2017 TCL Mobile
package tclpatch;

/**
 *
 * @author chaofei.wu.hz@tcl.com
 */
public class Message {

    public int what;

    public int percent;
    public int arg1;
    public int arg2;
    
    public String argStr1;
    public String argStr2;    

    public Object obj;
    
    public String toString() {
        return obj.toString();
    }
}
