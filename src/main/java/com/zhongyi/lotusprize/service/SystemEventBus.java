package com.zhongyi.lotusprize.service;

import com.google.common.eventbus.EventBus;

public class SystemEventBus {

	private final static SystemEventBus _instance = new SystemEventBus();

	private final EventBus eventBus = new EventBus();

	private SystemEventBus() {
	}

	public void register(Object listener) {
		eventBus.register(listener);
	}

	public void post(Object notification) {
		eventBus.post(notification);
	}

	public static SystemEventBus instance() {
		return _instance;
	}
	

	public static class BcsAddFileEvent {

		private final String _localHttpPath;
		
		private String result;
		
		private Exception ex;

		public BcsAddFileEvent(String localHttpPath) {
			super();
			this._localHttpPath = localHttpPath;
		}

		public String localHttpPath() {
			return _localHttpPath;
		}

		public String getResult(){
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public Exception getEx() {
			return ex;
		}

		public void setEx(Exception ex) {
			this.ex = ex;
		}
		
		
		
		

	}
	

	
	

	public static class BcsRemoveFileEvent {

		private final String _httpFilePath;
		
		private String result;
		
		private Exception ex;


		public BcsRemoveFileEvent(String fileHttpPath) {
			super();
			this._httpFilePath = fileHttpPath;
		}

		public String fileHttpPath() {
			return _httpFilePath;
		}

		public String getResult(){
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public Exception getEx() {
			return ex;
		}

		public void setEx(Exception ex) {
			this.ex = ex;
		}
		
		
		

	}

}
