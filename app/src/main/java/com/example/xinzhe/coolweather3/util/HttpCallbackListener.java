package com.example.xinzhe.coolweather3.util;

/**
 * Created by Xinzhe on 2015/10/9.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
