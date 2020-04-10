package com.bizzdesk.group.notification.service.mailgun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotax.framework.library.entity.helpers.AccountCreationEmailHelper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailGunService {

    @Value("${mailgun.api.key}")
    private String mailGunAPIKey;

    @Value("${mailgun.domain}")
    private String mailGunDomain;

    @Value("${mailgun.from.email}")
    private String mailGunFromEmail;

    @Value("${mailgun.from.email.alias}")
    private String mailGunFromEmailAlias;

    private ObjectMapper objectMapper;

    private freemarker.template.Configuration freeMarkerConfiguration;

    @Autowired
    public MailGunService(freemarker.template.Configuration freeMarkerConfiguration, ObjectMapper objectMapper) {
        this.freeMarkerConfiguration = freeMarkerConfiguration;
        this.objectMapper = objectMapper;
    }

    private Configuration getConfiguration() {
        return new Configuration()
                .domain(mailGunDomain)
                .apiKey(mailGunAPIKey)
                .from(mailGunFromEmailAlias, mailGunFromEmail);
    }

    @ServiceActivator(inputChannel = "gotax-email-verification")
    public void sendVerificationMail(Message<String> message) throws IOException, TemplateException {
        AccountCreationEmailHelper accountCreationEmailHelper = objectMapper.readValue(message.getPayload(), AccountCreationEmailHelper.class);
        Template template  = freeMarkerConfiguration.getTemplate("email-verification.ftlh","UTF-8");
        Map<String, String> verificationMailMap = new HashMap<>();
        verificationMailMap.put("firstName", accountCreationEmailHelper.getFirstName());
        verificationMailMap.put("lastName", accountCreationEmailHelper.getLastName());
        verificationMailMap.put("userId", accountCreationEmailHelper.getUserId());
        verificationMailMap.put("verificationCode", String.valueOf(accountCreationEmailHelper.getVerificationCode()));
        String emailBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, verificationMailMap);
        Mail.using(getConfiguration())
                .to(accountCreationEmailHelper.getEmailAddress())
                .subject("GoTax User Verification Email")
                .html(emailBody)
                .build()
                .send();
    }
}
