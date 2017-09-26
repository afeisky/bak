//# Copyright (C) 2017 TCL Mobile
package tctpatch;

/**
 *
 * @author chaofei.wu.hz@tcl.com
 */

import net.sf.json.JSONObject;

/**
 *
 * @author chaofei
 */
public class DataClass {
    public int result;
    public JSONObject argJsonObject;
    public String comment;
    public DataClass(int _result){
        result=_result;
        comment="";
    }
}
