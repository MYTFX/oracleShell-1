
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class TreeUtils {
    public TreeUtils() {
    }

    public static TreePath find(DefaultMutableTreeNode root, String s) {
        Enumeration e = root.depthFirstEnumeration();

        while(e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
            if (node.toString().equalsIgnoreCase(s)) {
                return new TreePath(node.getPath());
            }
        }

        return null;
    }

    public static TreePath getPath(TreeNode treeNode) {
        List<Object> nodes = new ArrayList();
        if (treeNode != null) {
            nodes.add(treeNode);

            for(treeNode = treeNode.getParent(); treeNode != null; treeNode = treeNode.getParent()) {
                nodes.add(0, treeNode);
            }
        }

        return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
    }

    public static void addChildNodes(DefaultTreeModel model, DefaultMutableTreeNode previosNode, List<Map<String, String>> mapList) {
        previosNode.removeAllChildren();
        System.out.println("previsoNode:" + previosNode.getChildCount() + getPath(previosNode) + ",maplist size:" + mapList.size() + mapList.get(0));
        Iterator var4 = mapList.iterator();

        while(var4.hasNext()) {
            Map<String, String> map = (Map)var4.next();
            if (((String)map.get("type")).toString().equals("D")) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(((String)map.get("name")).toString());
                model.insertNodeInto(child, previosNode, previosNode.getChildCount());
            }
        }

        model.reload(previosNode);
    }
}
