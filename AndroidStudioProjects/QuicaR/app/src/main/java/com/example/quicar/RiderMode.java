package com.example.quicar;

public class RiderMode {
    private boolean open = true;
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

    protected void makeRequest(Location start, Location destination){
        request = new Request();
        //request.setStart(start);
        //request.setDestination(destination);
    }
}
