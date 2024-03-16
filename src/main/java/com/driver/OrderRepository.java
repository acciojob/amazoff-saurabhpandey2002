package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository
{
    HashMap<String,Order> orders = new HashMap<>();
    HashMap<String,DeliveryPartner> deliveryPartners = new HashMap<>();
    HashMap<String, List<Order>> ordersReceived = new HashMap<>();

    HashMap<String, Order> unassignedOrders = new HashMap<>();

    public void addOrder(Order order)
    {
        String orderId = order.getId();
        orders.put(orderId, order);
        unassignedOrders.put(orderId, order);
    }

    public void addPartner(String partnerId)
    {
        DeliveryPartner dp = new DeliveryPartner(partnerId);
        deliveryPartners.put(partnerId, dp);
    }

    public void addOrderPartnerPair(String orderId, String partnerId){


        List<Order> ordersTillNow = ordersReceived.getOrDefault(partnerId,new ArrayList<>());

        Order newOrder = orders.get(orderId);

        ordersTillNow.add(newOrder);

        ordersReceived.put(partnerId, ordersTillNow);

        DeliveryPartner dp = deliveryPartners.get(partnerId);

        dp.setNumberOfOrders(dp.getNumberOfOrders() +  1);

        unassignedOrders.remove(orderId);
    }

    public Order getOrderById(String orderId) {

        Order order = null;
        //order should be returned with an orderId.
        order = orders.get(orderId);
        return order;
    }

    public DeliveryPartner getPartnerById(String partnerId)
    {
        DeliveryPartner deliveryPartner = deliveryPartners.get(partnerId);
        return deliveryPartner;
    }

    public Integer getOrderCountByPartnerId(String partnerId)
    {
        Integer orderCount = 0;
        List<Order> ordersTillNow = ordersReceived.get(partnerId);
        orderCount = ordersTillNow.size();
        return orderCount;
    }


    public List<Order> getOrdersByPartnerId(String partnerId) {
        List<Order> orderList = ordersReceived.get(partnerId);
        return orderList;
    }

    public List<String> getAllOrders() {
        List<String> allOrdersList = new ArrayList<>();
        for(String order : orders.keySet())
            allOrdersList.add(order);
        return allOrdersList;
    }

    public Integer getAssignedOrdersCount() {
        Integer count = 0;
        for(String dp : ordersReceived.keySet())
        {
            count += ordersReceived.get(dp).size();
        }
        return count;
    }

    public Integer getCountOfUnassignedOrders() {
        return unassignedOrders.size();
    }

    public void deletePartnerById(String partnerId) {
        List<Order> orderList = getOrdersByPartnerId(partnerId);
        for(Order order : orderList)
        {
            unassignedOrders.put(order.getId(),order);
        }
        DeliveryPartner dp = deliveryPartners.get(partnerId);
        dp.setNumberOfOrders(0);
        deliveryPartners.remove(partnerId);
    }

    public boolean containsOrder(List<Order> orderList,Order order)
    {
        for(Order o : orderList)
        {
            if(o == order)
                return true;
        }
        return false;
    }

    public void deleteOrderById(String orderId) {
        Order order = orders.get(orderId);
        List<Order> orderList = null;
        String partner = "";
        for(String pId : ordersReceived.keySet())
        {
            if(containsOrder(ordersReceived.get(pId),order))
            {
                orderList = ordersReceived.get(pId);
                partner = pId;
                break;
            }
        }
        orderList.remove(order);
        ordersReceived.put(partner,orderList);
        DeliveryPartner dp = deliveryPartners.get(partner);
        dp.setNumberOfOrders(dp.getNumberOfOrders() - 1);
        orders.remove(orderId);

    }
}