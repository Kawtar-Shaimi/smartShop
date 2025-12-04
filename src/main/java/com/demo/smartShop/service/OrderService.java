package com.demo.smartShop.service;

import com.demo.smartShop.dto.OrderDTO;
import com.demo.smartShop.dto.OrderItemDTO;
import com.demo.smartShop.entity.Client;
import com.demo.smartShop.entity.Order;
import com.demo.smartShop.entity.OrderItem;
import com.demo.smartShop.entity.Product;
import com.demo.smartShop.entity.enums.CustomerTier;
import com.demo.smartShop.entity.enums.OrderStatus;
import com.demo.smartShop.mapper.OrderMapper;
import com.demo.smartShop.repository.ClientRepository;
import com.demo.smartShop.repository.OrderRepository;
import com.demo.smartShop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final ClientService clientService;

    @org.springframework.beans.factory.annotation.Value("${smartshop.tva-rate:0.20}")
    private BigDecimal tvaRate;

    private static final Pattern PROMO_PATTERN = Pattern.compile("PROMO-[A-Z0-9]{4}");

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Client client = clientRepository.findById(orderDTO.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        Order order = new Order();
        order.setClient(client);
        order.setDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPromoCode(orderDTO.getPromoCode());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;

        // Validate Stock and Calculate Subtotal
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + itemDTO.getProductId()));

            if (product.getStock() < itemDTO.getQuantity()) {
                order.setStatus(OrderStatus.REJECTED);

            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setTotalLine(product.getPrice().multiply(new BigDecimal(itemDTO.getQuantity())));

            items.add(item);
            subTotal = subTotal.add(item.getTotalLine());
        }

        order.setItems(items);
        order.setSubTotal(subTotal);

        if (order.getStatus() == OrderStatus.REJECTED) {
            // Fill other fields with 0
            order.setDiscountAmount(BigDecimal.ZERO);
            order.setTaxAmount(BigDecimal.ZERO);
            order.setTotalAmount(BigDecimal.ZERO);
            order.setRemainingAmount(BigDecimal.ZERO);
            return orderMapper.toDTO(orderRepository.save(order));
        }

        // Calculate Discount
        BigDecimal discountPercentage = BigDecimal.ZERO;

        // Loyalty Discount
        CustomerTier tier = client.getTier();
        if (tier == CustomerTier.SILVER && subTotal.compareTo(new BigDecimal("500")) >= 0) {
            discountPercentage = discountPercentage.add(new BigDecimal("0.05"));
        } else if (tier == CustomerTier.GOLD && subTotal.compareTo(new BigDecimal("800")) >= 0) {
            discountPercentage = discountPercentage.add(new BigDecimal("0.10"));
        } else if (tier == CustomerTier.PLATINUM && subTotal.compareTo(new BigDecimal("1200")) >= 0) {
            discountPercentage = discountPercentage.add(new BigDecimal("0.15"));
        }

        // Promo Code
        if (orderDTO.getPromoCode() != null && PROMO_PATTERN.matcher(orderDTO.getPromoCode()).matches()) {
            discountPercentage = discountPercentage.add(new BigDecimal("0.05"));
        }

        BigDecimal discountAmount = subTotal.multiply(discountPercentage).setScale(2, RoundingMode.HALF_UP);
        BigDecimal amountAfterDiscount = subTotal.subtract(discountAmount);
        BigDecimal taxAmount = amountAfterDiscount.multiply(tvaRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = amountAfterDiscount.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        order.setDiscountAmount(discountAmount);
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(totalAmount);
        order.setRemainingAmount(totalAmount);

        return orderMapper.toDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderDTO confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order must be PENDING to be confirmed");
        }

        if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Order must be fully paid to be confirmed");
        }

        // Decrement Stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                order.setStatus(OrderStatus.REJECTED);
                return orderMapper.toDTO(orderRepository.save(order));
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(order);

        // Update Client Stats
        clientService.updateClientStats(order.getClient().getId(), order.getTotalAmount());

        return orderMapper.toDTO(savedOrder);
    }

    @Transactional
    public OrderDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be canceled");
        }

        order.setStatus(OrderStatus.CANCELED);
        return orderMapper.toDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderDTO rejectOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        // Admin can reject manually? "REJECTED : refusée (stock insuffisant)".
        // Usually system rejects. But Admin might want to reject for other reasons?
        // Requirement says "Transitions manuelles ... REJECTED" is NOT in the manual list for Admin.
        // Manual list: PENDING -> CONFIRMED, PENDING -> CANCELED.
        // So REJECTED is system only. But I'll leave it if needed or remove.
        // "Statuts finaux : CONFIRMED, REJECTED, CANCELED".
        // I'll assume only system rejects.

        return orderMapper.toDTO(order);
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(orderMapper::toDTO).collect(java.util.stream.Collectors.toList());
    }

    public List<OrderDTO> getOrdersByClientId(Long clientId) {
        return orderRepository.findByClientId(clientId).stream().map(orderMapper::toDTO).collect(java.util.stream.Collectors.toList());
    }

    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id).map(orderMapper::toDTO).orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }
}
