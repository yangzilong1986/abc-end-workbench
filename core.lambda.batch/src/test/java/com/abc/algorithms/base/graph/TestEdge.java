package com.abc.algorithms.base.graph;

import org.jgrapht.graph.DefaultEdge;

public class TestEdge
        extends DefaultEdge {

    private static final long serialVersionUID = 1L;

    public TestEdge() {
        super();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSource() == null) ? 0 : getSource().hashCode());
        result = prime * result + ((getTarget() == null) ? 0 : getTarget().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TestEdge other = (TestEdge) obj;
        if (getSource() == null) {
            if (other.getSource() != null)
                return false;
        } else if (!getSource().equals(other.getSource()))
            return false;
        if (getTarget() == null) {
            if (other.getTarget() != null)
                return false;
        } else if (!getTarget().equals(other.getTarget()))
            return false;
        return true;
    }
}
