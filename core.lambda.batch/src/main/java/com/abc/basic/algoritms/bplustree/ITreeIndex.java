
package com.abc.basic.algoritms.bplustree;

public interface ITreeIndex {

    void Recover(boolean CorrectErrors) throws Exception;


    void RemoveKey(String key) throws Exception;


    String FirstKey() throws Exception;


    String NextKey(String AfterThisKey) throws Exception;


    boolean ContainsKey(String key) throws Exception;


    Object Get(String key, Object defaultValue) throws Exception;


    void Set(String key, Object map) throws Exception;


    void Commit() throws Exception;

    /// <summary>
    /// Discard changes since the last commit and return to the state at the last commit point.
    /// </summary>
    void Abort() throws Exception;


    void SetFootPrintLimit(int limit) throws Exception;


    void Shutdown() throws Exception;

    /// <summary>
    /// Use the culture context for this tree to compare two Strings.
    /// </summary>
    /// <param name="left"></param>
    /// <param name="right"></param>
    /// <returns></returns>
    int Compare(String left, String right) throws Exception;
}
