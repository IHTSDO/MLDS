package ca.intelliware.mockj2ee.servlet;

import java.util.Enumeration;

class EmptyEnumeration implements Enumeration {
    
    public boolean hasMoreElements() {
        return false;
    }

    public Object nextElement() {
        return null;
    }

}
