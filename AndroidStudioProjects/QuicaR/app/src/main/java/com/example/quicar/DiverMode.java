package com.example.quicar;

public class DiverMode {
    private boolean open = false;
    private Request request = null;

    protected void openMode(){
        open = true;
    }

    protected void closeMode(){
        open = false;
    }

    protected boolean isMode(){
        return open;
    }

    protected void acceptReq(Request newRequest){
        request = newRequest;
    }

    protected Request getReq(){
        return request;
    }
}
