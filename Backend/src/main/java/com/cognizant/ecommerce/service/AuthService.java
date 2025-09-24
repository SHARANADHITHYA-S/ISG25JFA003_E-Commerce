package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dao.CartItemRepository;
import com.cognizant.ecommerce.dao.OrderRepository;
import com.cognizant.ecommerce.dao.PaymentMethodRepository;
import com.cognizant.ecommerce.dao.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("authService")
public class AuthService {

    public boolean isSelfOrAdmin(Long requestedUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is admin
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Check if user is accessing their own resource
        Object details = auth.getDetails();
        boolean isSelf = details instanceof Long actualUserId && actualUserId.equals(requestedUserId);

        return isSelf || isAdmin;
    }

    @Autowired
    private OrderRepository orderRepository;

    public boolean canAccessOrder(Long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Object details = auth.getDetails();
        if (!(details instanceof Long actualUserId)) return isAdmin;

        Long ownerId = orderRepository.findById(orderId)
                .map(order -> order.getUser().getId())
                .orElse(null);

        return isAdmin || (ownerId != null && ownerId.equals(actualUserId));
    }
    @Autowired
    private CartItemRepository cartItemRepository;

    public boolean canAccessCartItem(Long cartItemId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Object details = auth.getDetails();
        if (!(details instanceof Long actualUserId)) return isAdmin;

        Long ownerId = cartItemRepository.findById(cartItemId)
                .map(item -> item.getCart().getUser().getId())
                .orElse(null);

        return isAdmin || (ownerId != null && ownerId.equals(actualUserId));
    }

    @Autowired
    private PaymentRepository paymentRepository;

    public boolean canAccessPayment(Long paymentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Object details = auth.getDetails();
        if (!(details instanceof Long actualUserId)) return isAdmin;

        Long ownerId = paymentRepository.findById(paymentId)
                .map(payment -> payment.getOrder().getUser().getId())
                .orElse(null);

        return isAdmin || (ownerId != null && ownerId.equals(actualUserId));
    }

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public boolean canAccessPaymentMethod(Long paymentMethodId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Object details = auth.getDetails();
        if (!(details instanceof Long actualUserId)) return isAdmin;

        Long ownerId = paymentMethodRepository.findById(paymentMethodId)
                .map(pm -> pm.getUser().getId())
                .orElse(null);

        return isAdmin || (ownerId != null && ownerId.equals(actualUserId));
    }



}

