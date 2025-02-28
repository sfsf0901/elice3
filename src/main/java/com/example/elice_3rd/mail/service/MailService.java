package com.example.elice_3rd.mail.service;

import com.example.elice_3rd.common.exception.NoSuchDataException;
import com.example.elice_3rd.mail.dto.MailDto;
import com.example.elice_3rd.mail.entity.VerificationCode;
import com.example.elice_3rd.mail.repository.VerificationCodeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    @Value("${spring.mail.username}")
    private String EMAIL_SENDER;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final VerificationCodeRepository verificationCodeRepository;

    public void sendEmail(MailDto mailDto){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("subject", "[우리동네병원] 메일 인증");
            context.setVariable("content", mailDto.getContent());
            context.setVariable("code", mailDto.getCode());

            String base64Image = getBase64EncodedImage("/static/images/hospital.png");
            context.setVariable("image", base64Image);

            String htmlContent = templateEngine.process("email-template", context);
            helper.setTo(mailDto.getAddress());
            helper.setSubject("[우리동네병원] 메일 인증");
            helper.setText(htmlContent, true);
            helper.setFrom(EMAIL_SENDER);
            mailSender.send(message);
            verificationCodeRepository.save(VerificationCode.builder()
                    .code(mailDto.getCode())
                    .isVerify(false).build());
            log.info("템플릿 이메일 전송");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Boolean verify(String code){
        Boolean result = verificationCodeRepository.findById(code).isPresent();
        VerificationCode verificationCode = verificationCodeRepository.findById(code).orElseThrow(() ->
                new NoSuchDataException("인증 실패: 인증 코드가 일치하지 않습니다."));
        verificationCode.verify();
        verificationCodeRepository.save(verificationCode);
        return result;
    }

    public Boolean isVerifySuccess(String code){
        VerificationCode verificationCode = verificationCodeRepository.findById(code).orElseThrow(() ->
                new NoSuchDataException("인증 실패: 인증 코드가 일치하지 않습니다."));
        return verificationCode.getIsVerify();
    }

    private String getBase64EncodedImage(String imagePath) throws IOException {
        Resource resource = new ClassPathResource(imagePath);
        byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
        return Base64.getEncoder().encodeToString(bytes);
    }
}
