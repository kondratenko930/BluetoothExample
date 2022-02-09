package com.example.bluetoothexample.api;

public enum Protocol {
	HTTP (0),
	HTTPS (1);
	 private final int index;   
	 Protocol(int index) {
	        this.index = index;
	    }

	    public int index() { 
	        return index; 
	    }
}
