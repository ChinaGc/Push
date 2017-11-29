package com.gc.lib.callback;

/**
 * 
 * @ClassName: RequestCallBack
 * @Description: 请求回调
 * @author guocan
 * @date 2017-9-14 下午5:22:52
 * 
 */
public interface RequestCallBack {
    void onSuccess();

    void onProgress(int progress);

    void onError(int error, String msg);
}
