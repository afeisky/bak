//# Copyright (C) 2017 TCL Mobile
package tclpatch;

/**
 *
 * @author chaofei.wu.hz@tcl.com
 */
import java.awt.event.MouseAdapter;  
import java.awt.event.MouseEvent;  
  
import javax.swing.JTree;  
import javax.swing.tree.TreePath;  
import javax.swing.tree.DefaultTreeModel;  
  
public class CheckBoxTreeNodeSelectionListener extends MouseAdapter  
{  
    @Override  
    public void mouseClicked(MouseEvent event)  
    {  
        JTree tree = (JTree)event.getSource();  
        int x = event.getX();  
        int y = event.getY();  
        int row = tree.getRowForLocation(x, y);  
        TreePath path = tree.getPathForRow(row);  
        //System.out.print(row+" ("+x+","+y+")\n");
        if(path != null)  
        {  
            CheckBoxTreeNode node = (CheckBoxTreeNode)path.getLastPathComponent();
            //System.out.print(path+","+path.getPathCount()+"\n");     
            //BEGIN---chaofei.wu add
            int nLevel=path.getPathCount();            
            if (nLevel<=1){ //chaofei.wu add
               tree.expandPath(path); //always expand Root node.
               return;
            }
            if (x>((nLevel-1)*22+17)){
                return;
            }    
            //END---chaofei.wu add            
            if(node != null)  
            {  
                boolean isSelected = !node.isSelected();  
                node.setSelected(isSelected);  
                ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(node);  
            }  
            
        }  
        
    }  
}  