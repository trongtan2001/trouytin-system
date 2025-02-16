package com.roomster.roomsterbackend.service.impl.payment;

import com.roomster.roomsterbackend.service.IService.payment.IUserIpAddress;
import org.springframework.stereotype.Service;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@Service
public class UserIpAddress implements IUserIpAddress {
    @Override
    public String getIpAddress() throws SocketException {
        try (final DatagramSocket datagramSocket = new DatagramSocket()) {
            datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
            return datagramSocket.getLocalAddress().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}