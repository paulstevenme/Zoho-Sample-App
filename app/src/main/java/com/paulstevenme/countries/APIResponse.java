package com.paulstevenme.countries;

import org.json.JSONArray;

import java.util.List;

public class APIResponse {

    private String name;
    private String flag;
    private String region;
    private int population;
    private String capital;
    private String area;
    private List currencies;
    private List callingCodes;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public List getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List currencies) {
        this.currencies = currencies;
    }

    public List getCallingCodes() {
        return callingCodes;
    }

    public void setCallingCodes(List callingCodes) {
        this.callingCodes = callingCodes;
    }








}
