package com.alibaba.csp.sentinel.adapter.hsf.provider;

import com.alibaba.csp.sentinel.adapter.hsf.HelloService;

public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name, Integer n, double d) throws Exception {
        //try {
        //    Thread.sleep(1000 * 10);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
        //if(true){
        //    throw new Exception("sayHello Exception!!");
        //}
        return "Sentinel test Hello service, " + n + " " + d + " " + name;
    }
}
