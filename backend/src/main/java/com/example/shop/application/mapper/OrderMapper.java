package com.example.shop.application.mapper;

import com.example.shop.application.dto.response.OrderItemResponse;
import com.example.shop.application.dto.response.OrderResponse;
import com.example.shop.application.dto.response.PaymentMethodResponse;
import com.example.shop.domain.entity.Order;
import com.example.shop.domain.entity.OrderItem;
import com.example.shop.domain.entity.PaymentMethod;
import com.example.shop.domain.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = { AddressMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(source = "order.user.id", target = "userId")
    @Mapping(source = "order.user.fullName", target = "customerName")
    @Mapping(source = "order.user.email", target = "customerEmail")
    @Mapping(source = "order.paymentMethod.id", target = "paymentMethodId")
    @Mapping(source = "order.paymentMethod.name", target = "paymentMethodName")
    @Mapping(source = "order.address", target = "shippingAddress")
    OrderResponse toOrderResponse(Order order, List<OrderItemResponse> items);

    @Mapping(source = "productVariant.id", target = "productVariantId")
    @Mapping(source = "productVariant.sku", target = "sku")
    @Mapping(source = "productVariant.product.images", target = "imageUrl", qualifiedByName = "orderMapImageUrl")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    PaymentMethodResponse toPaymentMethodResponse(PaymentMethod paymentMethod);

    @Named("orderMapImageUrl")
    default String orderMapImageUrl(List<ProductImage> images) {
        if (images == null || images.isEmpty())
            return null;
        return images.stream()
                .filter(ProductImage::getIsThumbnail)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(images.get(0).getImageUrl());
    }
}
