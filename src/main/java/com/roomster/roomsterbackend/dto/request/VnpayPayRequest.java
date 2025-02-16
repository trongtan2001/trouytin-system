package com.roomster.roomsterbackend.dto.request;

import com.roomster.roomsterbackend.util.helpers.HashHelper;
import com.roomster.roomsterbackend.util.payment.VnpayCompare;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

@Data
@NoArgsConstructor
public class VnpayPayRequest {

    private TreeMap<String, String> requestData = new TreeMap<>(new VnpayCompare());

    private BigDecimal vnpAmount;
    private String vnpCommand;
    private String vnpCreateDate;
    private String vnpCurrCode;
    private String vnpBankCode;
    private String vnpIpAddr;
    private String vnpLocale;
    private String vnpOrderInfo;
    private String vnpOrderType;
    private String vnpReturnUrl;
    private String vnpTmnCode;
    private String vnpExpireDate;
    private String vnpTxnRef;
    private String vnpVersion;
    private String vnpSecureHash;

    public VnpayPayRequest(String version, String tmnCode, LocalDateTime createDate, String ipAddress,
                           BigDecimal amount, String currCode, String orderType, String orderInfo,
                           String returnUrl, String txnRef) {
        this.vnpLocale = "vn";
        this.vnpIpAddr = ipAddress;
        this.vnpVersion = version;
        this.vnpCurrCode = currCode;
        this.vnpCreateDate = createDate.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        this.vnpTmnCode = tmnCode;
        this.vnpAmount = amount.multiply(new BigDecimal(100)); // Convert amount to cents
        this.vnpCommand = "pay";
        this.vnpOrderType = orderType;
        this.vnpOrderInfo = orderInfo;
        this.vnpReturnUrl = returnUrl;
        this.vnpTxnRef = txnRef;
    }

    public String getLink(String baseUrl, String secretKey) {

        this.makeRequestData();

        StringBuilder data = new StringBuilder();
        for (Map.Entry<String, String> entry : requestData.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                data.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                        .append("&");
            }
        }

        String result = baseUrl + "?" + data.toString();

        // Assuming HashHelper.HmacSHA512 returns the HMAC-SHA512 hash of the provided data
        String secureHash = HashHelper.hmacSHA512(secretKey, data.substring(0, data.length() - 1));

        return result + "vnp_SecureHash=" + secureHash;
    }

    public void makeRequestData() {
        if (vnpAmount != null) {
            requestData.put("vnp_Amount", vnpAmount.toString());
        }
        if (vnpCommand != null) {
            requestData.put("vnp_Command", vnpCommand);
        }
        if (vnpCreateDate != null) {
            requestData.put("vnp_CreateDate", vnpCreateDate);
        }
        if (vnpCurrCode != null) {
            requestData.put("vnp_CurrCode", vnpCurrCode);
        }
        if (vnpBankCode != null) {
            requestData.put("vnp_BankCode", vnpBankCode);
        }
        if (vnpIpAddr != null) {
            requestData.put("vnp_IpAddr", vnpIpAddr);
        }
        if (vnpLocale != null) {
            requestData.put("vnp_Locale", vnpLocale);
        }
        if (vnpOrderInfo != null) {
            requestData.put("vnp_OrderInfo", vnpOrderInfo);
        }
        if (vnpOrderType != null) {
            requestData.put("vnp_OrderType", vnpOrderType);
        }
        if (vnpReturnUrl != null) {
            requestData.put("vnp_ReturnUrl", vnpReturnUrl);
        }
        if (vnpTmnCode != null) {
            requestData.put("vnp_TmnCode", vnpTmnCode);
        }
        if (vnpExpireDate != null) {
            requestData.put("vnp_ExpireDate", vnpExpireDate);
        }
        if (vnpTxnRef != null) {
            requestData.put("vnp_TxnRef", vnpTxnRef);
        }
        if (vnpVersion != null) {
            requestData.put("vnp_Version", vnpVersion);
        }
    }
}
