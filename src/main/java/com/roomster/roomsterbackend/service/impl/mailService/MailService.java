package com.roomster.roomsterbackend.service.impl.mailService;

import com.roomster.roomsterbackend.entity.*;
import com.roomster.roomsterbackend.repository.BankMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.roomster.roomsterbackend.repository.RoomRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BankMethodRepository bankMethodRepository;

    public void sendSimpleEmail(TenantEntity tenant, String subject, String text, String name, OrderEntity order) {
        try {
            Long roomId = order.getRoomId();
            Optional<InforRoomEntity> room = roomRepository.findById(roomId);
            InforRoomEntity roomRs = room.get();
            BigDecimal priceWate = roomRs.getWaterPrice().multiply(order.getWater());
            BigDecimal priceElectric = roomRs.getElectricityPrice().multiply(order.getElectricity());
            BigDecimal priceService = BigDecimal.ZERO;

            for (RoomServiceEntity roomServicePrice : roomRs.getServices()) {
                priceService = priceService.add(roomServicePrice.getServiceHouse().getServicePrice());
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserEntity principal = (UserEntity) authentication.getPrincipal();
            List<BankMethodEntity> bankMethodEntityList = bankMethodRepository.findAllByUserId(principal.getId());


            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(tenant.getEmail());
            helper.setSubject(subject);

            // Read HTML content from the file
            TemplateEngine templateEngine = new TemplateEngine();
            Context context = new Context();

            context.setVariable("name", name);
            context.setVariable("phone", tenant.getPhoneNumber());
            context.setVariable("numberRoom", String.valueOf(roomRs.getNumberRoom()));
            context.setVariable("total", formatVnd(order.getTotal()));
            context.setVariable("totalPayment", formatVnd(order.getTotalPayment()));
            context.setVariable("rest", formatVnd(order.getTotal().subtract(order.getTotalPayment())));
            context.setVariable("priceWate", formatVnd(priceWate));
            context.setVariable("priceElectric", formatVnd(priceElectric));
            context.setVariable("priceService", formatVnd(priceService));
            context.setVariable("bankMethods", bankMethodEntityList);

            String templateContent = templateEngine.process(readHtmlContent("email_template.html"), context);
            helper.setText(templateContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatVnd(BigDecimal amount) {
        // Create a Locale for Vietnam
        Locale locale = new Locale("vi", "VN");

        // Get the currency instance for VND
        Currency vndCurrency = Currency.getInstance(locale);

        // Create a NumberFormat for currency format in VND
        NumberFormat vndFormat = NumberFormat.getCurrencyInstance(locale);
        vndFormat.setCurrency(vndCurrency);

        // Format the BigDecimal value as a VND string and remove decimals and currency symbol
        return vndFormat.format(amount).split("\\s")[0];
    }

    private String readHtmlContent(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        return Files.readString(path);
    }

}