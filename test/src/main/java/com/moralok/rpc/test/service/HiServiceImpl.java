package com.moralok.rpc.test.service;

public class HiServiceImpl implements HiService {
    @Override
    public String sayHi(String name) {
        return "Hi..." + name;
    }
}
