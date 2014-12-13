package org.bigfenbushi.routeandloadbalance;

import java.util.Date;

public class Server {
    public String ip;
    public int weight;
    public int effectiveWeight;
    public int currentWeight;
    public boolean down = false;
    public Date checkedDate;
    public Server(String ip, int weight) {
        super();
        this.ip = ip;
        this.weight = weight;
        this.effectiveWeight = this.weight;
        this.currentWeight = 0;
        if(this.weight < 0){
            this.down = true;
        }else{
            this.down = false;
        }
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
    public int getEffectiveWeight() {
        return effectiveWeight;
    }
    public void setEffectiveWeight(int effectiveWeight) {
        this.effectiveWeight = effectiveWeight;
    }
    public int getCurrentWeight() {
        return currentWeight;
    }
    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }
    public boolean isDown() {
        return down;
    }
    public void setDown(boolean down) {
        this.down = down;
    }
    public Date getCheckedDate() {
        return checkedDate;
    }
    public void setCheckedDate(Date checkedDate) {
        this.checkedDate = checkedDate;
    }
    
}
