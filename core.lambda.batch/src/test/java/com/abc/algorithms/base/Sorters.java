package com.abc.algorithms.base;
import org.springframework.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by admin on 2017/4/9.
 */
public class Sorters {
    private static final Logger logger = LoggerFactory.getLogger(Sorters.class);

    /**
     *插入排序
     *插入排序，时间复杂度为O(n*n) 此算法对n要排序的元素进行n-1次扫描，在每次扫描中，算法都将下一个待排序的元素插入到
     * 该元素左边的子数组的合适位置上，使得这一子数组是排序的
     * @param array
     */
    public static void insertSort(Integer[] array){
        Assert.noNullElements(array,"输入数组不能为空");
        for(int j=0;j<array.length;j++){
            Integer key=array[j];
            Integer i=j-1;
            while(i>=0 && array[i]>key){
                array[i+1]=array[i];//move position
                i=i-1;
            }
            array[i+1]=key;
        }

    }

    /**
     * 分治模型模型
     *  分解：分解待排序的n个元素的序列为各具备n/2个元素的两个序列
     *  解决：使用并归递归的排序两个子序列
     *  合并：合并两个已经排好序的子序列以产生已排序的答案
     *
     * 归并排序，时间复杂度为O(nlgn) 此算法是利用分治法来对序列进行排序，首先，它不断地分割序列直到各个子序列都
     * 成为单元序列，然后，它又不断的成对的合并个子序列直到恢复到整个序列。
     *
     * @param array
     */
    public static void mergeForSort(Integer[] array,int p,int r){
        Assert.noNullElements(array,"输入数组不能为空");
        if(p<r){
            int q=(p+r)/2;
            mergeForSort(array,p,q);
            mergeForSort(array,q+1,r);
            merge(array,p,q,r);
        }
    }

    /**
     *
     * @param iData
     * @param p 起点
     * @param q 中点
     * @param r 末点
     */
    private static void merge(Integer[] iData, int p, int q,int r) {
        int n1=q-p+1;//左边个数
        int n2=r-q;//右边个数
        Integer left[] =new Integer[n1+1];
        Integer right[]=new Integer[n2+1];
        //左边数组
        for(int i=0;i<n1;i++){
            left[i]=iData[p+i];
        }
        //右边数组
        for(int j=0;j<n2;j++){
            right[j]=iData[q+j];//
        }
        left[n1]=Integer.MAX_VALUE;
        right[n2]=Integer.MAX_VALUE;

        int lPos=0;//左边位置
        int rPos=0;//右边位置
        //以起点p开始，以终端r为结束，遍历数组
        for(int k=p;k<r;k++){
            //如果左边的小于右边，则设置iData
            if(left[lPos]<=right[rPos]){
                iData[k]=left[lPos];
                lPos++;
            }else if(iData[k]==right[rPos]){//移动右端数组的位置
                rPos++;
            }
        }

    }
    public static void main(String[] args){
        Integer[] arr={4,6,1,2,7};
        insertSort(arr);
        //sortByInsert(arr);
        mergeForSort(arr,0,arr.length);
        System.out.println("Sorted \t");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + "\t");
        }
        System.out.println("Over");
    }
}
