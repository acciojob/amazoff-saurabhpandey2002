package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {
    Map<String,Order> orderDb=new HashMap<>();
    Map<String,DeliveryPartner> partnerDb=new HashMap<>();
    Map<String,String> orderToPartnerDb=new HashMap<>();
    Map<String,HashSet<String>> partnerToOrderDb=new HashMap<>();

    public void addOrder(Order order){
        String key=order.getId();
        orderDb.put(key,order);
    }
    public void addPartner(String partnerId){
        DeliveryPartner partner=new DeliveryPartner(partnerId);
        partnerDb.put(partnerId,partner);
    }
    public void addOrderPartnerPair(String orderId,String partnerId){
        orderToPartnerDb.put(orderId,partnerId);
        HashSet<String> orders=new HashSet<>();
        if(partnerToOrderDb.containsKey(partnerId)){
            orders=partnerToOrderDb.get(partnerId);
        }
        orders.add(orderId);
        partnerToOrderDb.put(partnerId,orders);
    }
    public Order getOrderById(String orderId){
        return orderDb.get(orderId);
    }
    public DeliveryPartner getPartnerById(String partnerId){
        return partnerDb.get(partnerId);
    }
    public int getOrderCountByPartnerId(String partnerId) {
        if (partnerToOrderDb.containsKey(partnerId))
            return partnerToOrderDb.get(partnerId).size();
        return 0;
    }
    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orders=new ArrayList<>();
        if(partnerToOrderDb.containsKey(partnerId)){
            HashSet<String> orders1=partnerToOrderDb.get(partnerId);
            Iterator<String> it= orders1.iterator();
            while(it.hasNext()){
                orders.add(it.next());
            }
        }
        return orders;
    }
    public List<String> getAllOrders(){
        List<String> orders=new ArrayList<>();
        for(String s: orderDb.keySet()){
            orders.add(s);
        }
        return orders;
    }
    public int getCountOfUnassignedOrders(){
        int count=0;
        for(String order:orderDb.keySet()){
            if(!orderToPartnerDb.containsKey(order))
                count++;
        }
        return count;
    }
    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){
        int count=0;
        if(partnerToOrderDb.containsKey(partnerId)){
            int hr=Integer.parseInt(time.substring(0,2));
            int min=Integer.parseInt(time.substring(2));
            int mainTime=(hr*60)+min;
            HashSet<String> orders=partnerToOrderDb.get(partnerId);
            Iterator<String> it= orders.iterator();
            while(it.hasNext()){
                Order order=orderDb.get(it.next());
                if(mainTime<order.getDeliveryTime())
                    count++;
            }
        }
        return count;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        int lastDeliveryTime=0;
        if(partnerToOrderDb.containsKey(partnerId)){
            HashSet<String> orders=partnerToOrderDb.get(partnerId);
            Iterator<String> it= orders.iterator();
            while(it.hasNext()){
                Order order=orderDb.get(it.next());
                lastDeliveryTime=Math.max(lastDeliveryTime, order.getDeliveryTime());
            }
        }
        int hr=lastDeliveryTime/60;
        int min=lastDeliveryTime%60;
        String hr1="", min1="";
        int hrDigit=0, minDigit=0;
        while(hr>0 || minDigit>0){
            if(hr!=0){
                hr/=10;
                hrDigit++;
            }
            if(min!=0){
                min/=10;
                minDigit++;
            }
        }
        if(hr==0) hr1+="00";
        else if(hrDigit==1) hr1+="0"+Integer.toString(hr);
        else hr1+=Integer.toString(hr);
        if(min==0) min1+="00";
        else if(minDigit==1) min1+="0"+Integer.toString(min);
        else min1+=Integer.toString(min);
        String finalTime="";
        finalTime+=hr1+":"+min1;
        return finalTime;
    }
    public void deletePartnerById(String partnerId){
        partnerDb.remove(partnerId);
        partnerToOrderDb.remove(partnerId);
        for(String order:orderToPartnerDb.keySet()){
            String partner=orderToPartnerDb.get((order));
            if(partner.equals(partnerId))
                orderToPartnerDb.remove(order);
        }
    }
    public void deleteOrderById(String orderId){
        orderDb.remove(orderId);
        orderToPartnerDb.remove(orderId);
        for(String p:partnerToOrderDb.keySet()){
            HashSet<String> orders=partnerToOrderDb.get(p);
            if(orders.contains(orderId))
                orders.remove(orderId);
        }
    }

}