
package com.abc.basic.algoritms.bplustree;


public class BplusTree implements IStringTree {


    public ITreeIndex tree;

    public BplusTree(ITreeIndex tree) {
        this.tree = tree;
    }


    public static BplusTree Initialize(String treefileName, String blockfileName, int KeyLength, int CultureId,
                                       int nodesize, int buffersize)
            throws Exception {
        BplusTreeBytes tree = BplusTreeBytes.Initialize(treefileName, blockfileName, KeyLength, CultureId, nodesize, buffersize);
        return new BplusTree(tree);
    }

    public static BplusTree Initialize(String treefileName, String blockfileName, int KeyLength, int CultureId)
            throws Exception {
        BplusTreeBytes tree = BplusTreeBytes.Initialize(treefileName, blockfileName, KeyLength, CultureId);
        return new BplusTree(tree);
    }

    public static BplusTree Initialize(String treefileName, String blockfileName, int KeyLength)
            throws Exception {
        BplusTreeBytes tree = BplusTreeBytes.Initialize(treefileName, blockfileName, KeyLength);
        return new BplusTree(tree);
    }

    public static BplusTree Initialize(java.io.RandomAccessFile treefile, java.io.RandomAccessFile blockfile, int KeyLength, int CultureId,
                                       int nodesize, int buffersize) throws Exception {
        BplusTreeBytes tree = BplusTreeBytes.Initialize(treefile, blockfile, KeyLength, CultureId, nodesize, buffersize);
        return new BplusTree(tree);
    }

    public static BplusTree Initialize(java.io.RandomAccessFile treefile, java.io.RandomAccessFile blockfile, int KeyLength, int CultureId)
            throws Exception {
        BplusTreeBytes tree = BplusTreeBytes.Initialize(treefile, blockfile, KeyLength, CultureId);
        return new BplusTree(tree);
    }

    public static BplusTree Initialize(java.io.RandomAccessFile treefile, java.io.RandomAccessFile blockfile, int KeyLength)
            throws Exception {
        BplusTreeBytes tree = BplusTreeBytes.Initialize(treefile, blockfile, KeyLength);
        return new BplusTree(tree);
    }

    public static BplusTree ReOpen(java.io.RandomAccessFile treefile, java.io.RandomAccessFile blockfile) throws Exception {
        BplusTreeBytes tree = BplusTreeBytes.ReOpen(treefile, blockfile);
        return new BplusTree(tree);
    }

    public static BplusTree ReOpen(String treefileName, String blockfileName) throws Exception {
        BplusTreeBytes tree = BplusTreeBytes.ReOpen(treefileName, blockfileName);
        return new BplusTree(tree);
    }

    public static BplusTree ReadOnly(String treefileName, String blockfileName) throws Exception {
        BplusTreeBytes tree = BplusTreeBytes.ReadOnly(treefileName, blockfileName);
        return new BplusTree(tree);
    }

    //#region ITreeIndex Members

    public void Recover(boolean CorrectErrors) throws Exception {
        this.tree.Recover(CorrectErrors);
    }

    public void RemoveKey(String key) throws Exception {
        this.tree.RemoveKey(key);
    }

    public String FirstKey() throws Exception {
        return this.tree.FirstKey();
    }

    public String NextKey(String AfterThisKey) throws Exception {
        return this.tree.NextKey(AfterThisKey);
    }

    public boolean ContainsKey(String key) throws Exception {
        return this.tree.ContainsKey(key);
    }

    public Object Get(String key, Object defaultValue) throws Exception {
        Object test = this.tree.Get(key, null);
        if (test != null) {
            return BytesToString((byte[]) test);
        }
        return defaultValue;
    }

    public void Set(String key, Object map) throws Exception {

        String theString = (String) map;
        byte[] bytes = StringToBytes(theString);
        //this.tree[key] = bytes;
        this.tree.Set(key, bytes);
    }

    public void Commit() throws Exception {
        this.tree.Commit();
    }

    public void Abort() throws Exception {
        this.tree.Abort();
    }


    public void SetFootPrintLimit(int limit) throws Exception {
        this.tree.SetFootPrintLimit(limit);
    }

    public void Shutdown() throws Exception {
        this.tree.Shutdown();
    }

    public int Compare(String left, String right) throws Exception {
        return this.tree.Compare(left, right);
    }

    //#endregion
    public String get(String key) throws Exception {
        Object theGet = this.tree.Get(key, null);
        if (theGet != null) {
            byte[] bytes = (byte[]) theGet;
            return BytesToString(bytes);
        }
        //System.Diagnostics.Debug.WriteLine(this.toHtml());
        throw new BplusTreeKeyMissing("key not found " + key);
    }

    public void set(String key, String value) throws Exception {
        byte[] bytes = StringToBytes(value);
        //this.tree[key] = bytes;
        this.tree.Set(key, bytes);
    }

    public static String BytesToString(byte[] bytes) throws Exception {
        return new String(bytes, 0, bytes.length, "UTF-8");
    }

    public static byte[] StringToBytes(String theString) throws Exception {
        return theString.getBytes("UTF-8");
    }
}
