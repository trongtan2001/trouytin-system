package com.roomster.roomsterbackend.dto.response;

import com.roomster.roomsterbackend.util.helpers.HashHelper;
import com.roomster.roomsterbackend.util.payment.VnpayCompare;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

@Data
public class VnpayPayResponse {

    private TreeMap<String, String> responseData = new TreeMap<>(new VnpayCompare());

    private String vnp_TmnCode = "";
    private String vnp_BankCode = "";
    private String vnp_BankTranNo = "";
    private String vnp_CardType = "";
    private String vnp_OrderInfo = "";
    private String vnp_TransactionNo = "";
    private String vnp_TransactionStatus = "";
    private String vnp_TxnRef = "";
    private String vnp_SecureHashType = "";
    private String vnp_SecureHash = "";
    private BigDecimal vnp_Amount;
    private String vnp_ResponseCode = "";
    private String vnp_PayDate = "";


    /**
     * check signature is valid or not
     *
     * @param  secretKey  key of merchant
     * @return         the post-incremented value
     */
    public boolean isValidSignature(String secretKey) {
        makeResponseData();
        StringBuilder data = new StringBuilder();

        for (Map.Entry<String, String> entry : responseData.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                try {
                    data.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace(); // Handle the exception based on your application's requirements
                }
            }
        }
        String checkSum = HashHelper.hmacSHA512(secretKey, data.substring(0, data.length() - 1));
        return checkSum.equalsIgnoreCase(this.vnp_SecureHash);
    }

    public void makeResponseData() {
        if (vnp_Amount != null) {
            responseData.put("vnp_Amount", String.valueOf(vnp_Amount));
        }
        if (vnp_TmnCode != null && !vnp_TmnCode.isEmpty()) {
            responseData.put("vnp_TmnCode", vnp_TmnCode);
        }
        if (vnp_BankCode != null && !vnp_BankCode.isEmpty()) {
            responseData.put("vnp_BankCode", vnp_BankCode);
        }
        if (vnp_BankTranNo != null && !vnp_BankTranNo.isEmpty()) {
            responseData.put("vnp_BankTranNo", vnp_BankTranNo);
        }
        if (vnp_CardType != null && !vnp_CardType.isEmpty()) {
            responseData.put("vnp_CardType", vnp_CardType);
        }
        if (vnp_OrderInfo != null && !vnp_OrderInfo.isEmpty()) {
            responseData.put("vnp_OrderInfo", vnp_OrderInfo);
        }
        if (vnp_TransactionNo != null && !vnp_TransactionNo.isEmpty()) {
            responseData.put("vnp_TransactionNo", vnp_TransactionNo);
        }
        if (vnp_TransactionStatus != null && !vnp_TransactionStatus.isEmpty()) {
            responseData.put("vnp_TransactionStatus", vnp_TransactionStatus);
        }
        if (vnp_TxnRef != null && !vnp_TxnRef.isEmpty()) {
            responseData.put("vnp_TxnRef", vnp_TxnRef);
        }
        if (vnp_PayDate != null && !vnp_PayDate.isEmpty()) {
            responseData.put("vnp_PayDate", vnp_PayDate);
        }
        if (vnp_ResponseCode != null && !vnp_ResponseCode.isEmpty()) {
            responseData.put("vnp_ResponseCode", vnp_ResponseCode);
        }
    }
}
