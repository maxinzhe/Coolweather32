package com.example.xinzhe.coolweather3.model;

/**
 * Created by Xinzhe on 2015/10/8.
 */
public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;

    public String getProvinceCode() {
        return provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
}
