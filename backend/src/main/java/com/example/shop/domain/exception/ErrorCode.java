package com.example.shop.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(1001, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(1002, "Không tìm thấy sản phẩm", HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL(1003, "Email đã tồn tại", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(1005, "Sai tên đăng nhập/mật khẩu", HttpStatus.UNAUTHORIZED),
    INSUFFICIENT_STOCK(1006, "Tồn kho không đủ", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1007, "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND),
    FORBIDDEN(1008, "Không có quyền truy cập", HttpStatus.FORBIDDEN),
    UNAUTHORIZED(1009, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    VOUCHER_NOT_FOUND(1010, "Không tìm thấy mã giảm giá", HttpStatus.NOT_FOUND),
    VOUCHER_EXPIRED(1033, "Mã giảm giá đã hết hạn", HttpStatus.BAD_REQUEST),
    VOUCHER_NOT_STARTED(1034, "Mã giảm giá chưa đến ngày sử dụng", HttpStatus.BAD_REQUEST),
    VOUCHER_LIMIT_EXCEEDED(1035, "Mã giảm giá đã hết lượt sử dụng", HttpStatus.BAD_REQUEST),
    VOUCHER_USER_LIMIT_EXCEEDED(1036, "Bạn đã hết lượt sử dụng mã giảm giá này", HttpStatus.BAD_REQUEST),
    VOUCHER_MIN_ORDER_VALUE(1037, "Đơn hàng chưa đạt giá trị tối thiểu để dùng mã này", HttpStatus.BAD_REQUEST),
    VOUCHER_INACTIVE(1038, "Mã giảm giá đang bị tạm khóa", HttpStatus.BAD_REQUEST),
    ORDER_NOT_COMPLETED(1011, "Chỉ được đánh giá khi đơn hàng đã hoàn thành", HttpStatus.BAD_REQUEST),
    ALREADY_REVIEWED(1012, "Sản phẩm này đã được đánh giá", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(1013, "Không tìm thấy giỏ hàng", HttpStatus.NOT_FOUND),
    CART_EMPTY(1014, "Giỏ hàng trống", HttpStatus.BAD_REQUEST),
    PAYMENT_METHOD_NOT_FOUND(1015, "Không tìm thấy phương thức thanh toán", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_FOUND(1016, "Không tìm thấy địa chỉ", HttpStatus.NOT_FOUND),
    INVALID_ORDER_STATUS(1017, "Trạng thái đơn hàng không hợp lệ", HttpStatus.BAD_REQUEST),
    PRODUCT_VARIANT_NOT_FOUND(1018, "Không tìm thấy phân loại sản phẩm", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND(1019, "Không tìm thấy sản phẩm trong giỏ", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1020, "Không tìm thấy danh mục", HttpStatus.NOT_FOUND),
    INVALID_CATEGORY_PARENT(1021, "Danh mục cha không hợp lệ", HttpStatus.BAD_REQUEST),
    CATEGORY_HAS_CHILDREN(1022, "Không thể xóa danh mục có chứa danh mục con", HttpStatus.BAD_REQUEST),
    DUPLICATE_USERNAME(1023, "Tên đăng nhập đã tồn tại", HttpStatus.CONFLICT),
    ACCOUNT_DISABLED(1024, "Tài khoản đã bị vô hiệu hóa", HttpStatus.FORBIDDEN),
    BLOG_NOT_FOUND(1025, "Không tìm thấy bài viết", HttpStatus.NOT_FOUND),
    BLOG_CATEGORY_NOT_FOUND(1026, "Không tìm thấy danh mục blog", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND(1027, "Không tìm thấy thông báo", HttpStatus.NOT_FOUND),
    TOKEN_EXPIRED(1028, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1029, "Refresh token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED),
    TOKEN_BLACKLISTED(1030, "Token đã bị thu hồi, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD(1031, "Mật khẩu hiện tại không chính xác", HttpStatus.BAD_REQUEST),
    CONFIRM_PASSWORD_NOT_MATCH(1032, "Mật khẩu xác nhận không khớp", HttpStatus.BAD_REQUEST),
    ACCOUNT_LINKING_REQUIRED(1039, "Email này đã được đăng ký. Vui lòng nhập mật khẩu để liên kết với tài khoản Google.", HttpStatus.ACCEPTED),
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
