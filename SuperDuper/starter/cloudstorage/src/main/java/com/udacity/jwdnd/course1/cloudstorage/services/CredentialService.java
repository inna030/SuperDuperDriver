package com.udacity.jwdnd.course1.cloudstorage.services;
import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;


@Service
public class CredentialService {
    private CredentialMapper credentialMapper;
    private EncryptionService encryptionService;
//not matter update or create, everytime encrypt it before next method

    public CredentialService(CredentialMapper credentialMapper, EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }
    public int createCredential(Credential credential) {
//        SecureRandom random = new SecureRandom();
//        byte[] key = new byte[16];
//        random.nextBytes(key);
//        String encodedKey = Base64.getEncoder().encodeToString(key);
//        String encryptedPassword = encryptionService.encryptValue(credential.getPassword(), encodedKey);
        return credentialMapper.insert(new Credential(null,credential.getUrl(), credential.getUsername(),credential.getKey(), credential.getPassword(),credential.getUserId()));
    }

    public List<Credential> getCredentialsForCurrentUser(Integer userId) {
        return credentialMapper.getCredentials(userId);
    }


    public int updateCredential(Credential credential) {
        encryptPassword(credential);
        return credentialMapper.update(credential);
    }
    public void updateCredentialWithKey(Credential credential){
        // to update key
        String keyFromDb = credentialMapper.getKey(credential.getCredentialId());
        credential.setKey(keyFromDb);
    }

    public void encryptPassword(Credential credential) {
        // to set an encoded key
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        credential.setKey(encodedKey);

        // to encode the url's password & encode it
        String encryptedPassword = encryptionService.encryptValue(credential.getPassword(), encodedKey);
        credential.setPassword(encryptedPassword);
    }
    public int deleteCredential(Credential credential) {
        return credentialMapper.delete(credential.getCredentialId());
    }
}
