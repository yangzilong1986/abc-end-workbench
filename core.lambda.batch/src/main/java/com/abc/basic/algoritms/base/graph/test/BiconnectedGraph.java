package com.abc.basic.algoritms.base.graph.test;

import com.abc.basic.algoritms.base.graph.DefaultEdge;
import com.abc.basic.algoritms.base.graph.SimpleGraph;

public class BiconnectedGraph
        extends SimpleGraph<String, DefaultEdge>
{
    // ~ Static fields/initializers ---------------------------------------------

    /**
     */
    private static final long serialVersionUID = 6007460525580983710L;

    // ~ Constructors -----------------------------------------------------------

    public BiconnectedGraph()
    {
        super(DefaultEdge.class);

        addVertices();
        addEdges();
    }

    // ~ Methods ----------------------------------------------------------------

    private void addEdges()
    {
        addEdge("0", "1");
        addEdge("1", "2");
        addEdge("2", "3");
        addEdge("3", "4");
        addEdge("4", "5");
        addEdge("5", "0");
    }

    private void addVertices()
    {
        addVertex("0");
        addVertex("1");
        addVertex("2");
        addVertex("3");
        addVertex("4");
        addVertex("5");
    }
}
