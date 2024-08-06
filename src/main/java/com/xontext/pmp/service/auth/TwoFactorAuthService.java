package com.xontext.pmp.service.auth;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
@Slf4j
public class TwoFactorAuthService {
    public String generateNewSecret(){
        return new DefaultSecretGenerator().generate();
    }

    public String generateQrCodeImageUri(String secret){
        QrData data = new QrData.Builder()
                .label("Preference Management Platform")
                .secret(secret)
                .issuer("Xontext")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = new byte[0];
        try{
            imageData = generator.generate(data);
        } catch (QrGenerationException e){
            log.error("Error creating QR-Code");
            throw new RuntimeException(e);
        }
        return getDataUriForImage(imageData, generator.getImageMimeType());
    }

//    public boolean isOtpValid(String secret, String code){
//        TimeProvider timeProvider = new SystemTimeProvider();
//        CodeGenerator codeGenerator = new DefaultCodeGenerator();
//        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
//        return verifier.isValidCode(secret, code);
//    }
    public boolean isOtpValid(String secret, String code) {
        TimeProvider timeProvider = new SystemTimeProvider();
        long timeIndex = timeProvider.getTime() / 30; // Assuming 30-second time steps

        DefaultCodeGenerator codeGenerator = new DefaultCodeGenerator();
        try {
            String generatedCode = codeGenerator.generate(secret, timeIndex);
            return generatedCode.equals(code);
        } catch (CodeGenerationException e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return false;
        }
    }

    public boolean isOtpNotValid(String secret, String code){
        return !this.isOtpValid(secret, code);
    }
}
