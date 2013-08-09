package com.ceenee.sync;

public interface OnSyncListener {
	public void onReadyReceiveBook();
	public void onReceivedBook(String songbook);
	public void onSyncFail(Exception e);
	public void onFinishSyncing();
	public void onProcessBook();
}
