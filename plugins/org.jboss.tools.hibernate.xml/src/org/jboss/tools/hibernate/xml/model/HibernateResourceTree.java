package org.jboss.tools.hibernate.xml.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelObjectConstants;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.filesystems.XFileObject;
import org.jboss.tools.common.model.filesystems.impl.FileSystemsImpl;
import org.jboss.tools.common.model.filesystems.impl.Libs;
import org.jboss.tools.common.model.impl.trees.FileSystemResourceTree;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class HibernateResourceTree extends FileSystemResourceTree {

    protected boolean accepts0(XModelObject o) {
        int type = o.getFileType();
        if(type == XModelObject.FOLDER) return !XModelObjectConstants.TRUE.equals(o.get("overlapped")); //$NON-NLS-1$
        if(type != XModelObject.FILE) return false;
        String pathpart = o.getPathPart();
        String pp = pathpart;
        pp = pp.toLowerCase();
        return pp.endsWith(".hbm.xml");
    }

    public void setModel(XModel model) {
    	super.setModel(model);
    	if(EclipseResourceUtil.isProjectFragment(model)) {
    		FileSystemsImpl fs = (FileSystemsImpl)FileSystemsHelper.getFileSystems(model);
    		if(fs != null) fs.forceUpdate();
    	}
    }

	public XModelObject[] getChildren(XModelObject object) {
		if(object == getRoot()) {
			XModelObject[] os = object.getChildren();
			List<XModelObject> list = new ArrayList<XModelObject>();
			for (int i = 0; i < os.length; i++) {
				String name = os[i].getAttributeValue(XModelObjectConstants.ATTR_NAME);
				if(name.startsWith("src")) { //$NON-NLS-1$
					list.add(os[i]);
				}
			}
			for (int i = 0; i < os.length; i++) {
				String name = os[i].getAttributeValue(XModelObjectConstants.ATTR_NAME);
				if(name.startsWith(Libs.LIB_PREFIX)) {
					list.add(os[i]);
				}
			}
			return list.toArray(new XModelObject[0]);
		}
		
        if(!hasChildren(object)) return new XModelObject[0];
        List<XModelObject> l = new ArrayList<XModelObject>();
        XModelObject[] cs = object.getChildren();
        for (int i = 0; i < cs.length; i++) if(accept(cs[i])) l.add(cs[i]);
        return l.toArray(new XModelObject[0]);
	}

	public XModelObject getParent(XModelObject object) {
		return object.getParent();
	}

    private boolean accept(XModelObject c) {
        if(c.getFileType() == XFileObject.FOLDER) {
            String overlapped = c.get("overlapped"); //$NON-NLS-1$
            if(overlapped != null && overlapped.length() > 0) {
            	String overlappedSystem = c.get("overlappedSystem"); //$NON-NLS-1$
            	if(!"FileSystems/WEB-INF".equals(overlappedSystem)) return false; //$NON-NLS-1$
            } 
        } else if(c.getFileType() == XFileObject.FILE) {
        	String nm = c.getAttributeValue("name"); //$NON-NLS-1$
        	if(nm.length() == 0) return false;
        	if(!accepts0(c)) return false;
        }
        return true;
    }

	public boolean isSelectable(XModelObject object) {
		return (object != null && (object.getFileType() == XFileObject.FILE || object.getFileType() == XFileObject.FOLDER));
	}

    public String getPath(XModelObject o) {
        if(o == null || o.getFileType() == XModelObject.SYSTEM) return ""; //$NON-NLS-1$
        String s = XModelObjectLoaderUtil.getResourcePath(o);
        String p = o.getPath();
        if(p == null) return ""; //$NON-NLS-1$
        int b = "FileSystems/".length(), e = p.length() - s.length(); //$NON-NLS-1$
        if(e < b) return ""; //$NON-NLS-1$
        if(o.getFileType() == XFileObject.FOLDER) s += "/"; //$NON-NLS-1$
        if(s.startsWith("/")) s = s.substring(1);
        return s;
    }

    public XModelObject find(String value) {
    	if(value != null && value.endsWith("/")) {
    		value = value.substring(0, value.length() - 1);
    	}
    	XModelObject result = model.getByPath(value);
    	if(result == null && value != null && !value.startsWith("/")) { //$NON-NLS-1$
    		result = model.getByPath("/" + value); //$NON-NLS-1$
    	}
        return result;
    }

}
