package roc.gc.push.nio;

public interface PushCallback {

	void onSuccess();

	void onProgress(int progress);

	void onError(int error, String msg);
}
