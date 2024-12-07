package com.demo.smartShop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String productName, int requested, int available) {
        super(String.format("Stock insuffisant pour le produit '%s': demandé=%d, disponible=%d",
                productName, requested, available));
    }

    public InsufficientStockException(Map<String, StockInfo> insufficientProducts) {
        super(buildMessage(insufficientProducts));
    }

    private static String buildMessage(Map<String, StockInfo> insufficientProducts) {
        StringBuilder message = new StringBuilder("Stock insuffisant pour les produits suivants:\n");
        insufficientProducts
                .forEach((productName, stockInfo) -> message.append(String.format("- %s: demandé=%d, disponible=%d\n",
                        productName, stockInfo.getRequested(), stockInfo.getAvailable())));
        return message.toString();
    }

    @Getter
    @AllArgsConstructor
    public static class StockInfo {
        private final int requested;
        private final int available;
    }
}
