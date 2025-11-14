//package com.example.tricolv2sb;
//
//import com.example.tricolv2sb.DTO.ReadPurchaseOrderDTO;
//import com.example.tricolv2sb.Entity.PurchaseOrder;
//import com.example.tricolv2sb.Mapper.PurchaseOrderMapper;
//import com.example.tricolv2sb.Service.ServiceInterfaces.PurchaseOrderInterface;
//
//import java.util.List;
//
//public class Main {
//    private static PurchaseOrderInterface purchaseOrderservice;
//    private static PurchaseOrderMapper orderMapper;
//
//    public static void main(String[] args) {
//
//        List<PurchaseOrder> orders = purchaseOrderservice.getAllPurchaseOrders()
//                .stream()
//                .map(ReadPurchaseOrderDTO-> orderMapper.toEntity());
//
//    }
//}
