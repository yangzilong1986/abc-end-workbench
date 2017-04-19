package com.abc.algorithms.base.introduction;

public class DirectedGraph<T> extends Graph<T> {

    @Override
    public void addEdge(T source, T target) {
        // TODO Auto-generated method stub
        Vertex<T> sourceVertex=isContainVertext(source);
        Vertex<T> targetVertex=isContainVertext(target);
        if (sourceVertex==null) {
            throw new IllegalArgumentException("不包含起始节点!");
        }
        if (targetVertex==null) {
            throw new IllegalArgumentException("不包含终端节点!");
        }
        if (isContainEdge(source, target)!=null) {
            throw new IllegalArgumentException("重复添加该边！");
        }
        // 添加新的边
        edges.add(new DirectedEdge<T>(source, target));
        int row = 0, column = 0;
        int counter = 0;
        int i=0;
        for (Vertex<T> vertex : vertices) {
            if (vertex.value.equals(source)&&source.equals(target)) {
                vertex.adjacencyVertices.add(targetVertex);//更新邻接表
                row = i;
                column=i;
                setMatrixValue(row, column);// 设置邻接矩阵的值
                break;

            }else if (vertex.value.equals(source)) {
                vertex.adjacencyVertices.add(targetVertex);//更新邻接表
                row = i;
                counter++;
                if (counter == 2) {
                    setMatrixValue(row, column);// 设置邻接矩阵的值
                    break;
                }
            }else if (vertex.value.equals(target)) {
                column = i;
                counter++;
                if (counter == 2) {
                    setMatrixValue(row, column);
                    break;
                }
            }
            i++;
        }

    }

}

