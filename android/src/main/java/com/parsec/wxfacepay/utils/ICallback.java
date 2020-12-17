package com.parsec.wxfacepay.utils;

import java.util.Map;

public interface ICallback{
    void callback(Map<String, Object> params);
}
