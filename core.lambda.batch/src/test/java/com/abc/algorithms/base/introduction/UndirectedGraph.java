package com.abc.algorithms.base.introduction;

public class UndirectedGraph<T> extends Graph<T> {

    @Override
    public void addEdge(T source, T target) {
        // TODO Auto-generated method stub
        if (source.equals(target)) {
            throw new IllegalArgumentException("无向图不能包含自旋!");
        }
        Vertex<T> sourceVertex=isContainVertext(source);
        Vertex<T> targetVertex=isContainVertext(target);
        if (sourceVertex==null) {
            throw new IllegalArgumentException("不包含起始节点!");
        }
        if (targetVertex==null) {
            throw new IllegalArgumentException("不包含终端节点!");
        }
        if (isContainEdge(source, target)!=null||isContainEdge(target,source)!=null) {
            throw new IllegalArgumentException("重复添加该边！");
        }
        // 添加新的边
        edges.add(new UndirectedEdge<T>(source, target));
        int row = 0, column = 0;
        int counter = 0;
        int i=0;
        for (Vertex<T> vertex : vertices) {
            if (vertex.value.equals(source)) {
                vertex.adjacencyVertices.add(targetVertex);//更新邻接表
                row = i;
                counter++;
                if (counter == 2) {
                    setMatrixValue(row, column);// 设置邻接矩阵的值
                    setMatrixValue(column, row);// 设置邻接矩阵的值
                    break;
                }
            }else if (vertex.value.equals(target)) {
                vertex.adjacencyVertices.add(sourceVertex);//更新邻接表
                column = i;
                counter++;
                if (counter == 2) {
                    setMatrixValue(row, column);
                    setMatrixValue(column, row);
                    break;
                }
            }
            i++;
        }
    }
}

