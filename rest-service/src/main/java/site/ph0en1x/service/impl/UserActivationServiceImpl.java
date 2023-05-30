package site.ph0en1x.service.impl;

import org.springframework.stereotype.Service;
import site.ph0en1x.dao.AppUserDAO;
import site.ph0en1x.service.UserActivationService;
import site.ph0en1x.utils.CryptoTool;

@Service
public class UserActivationServiceImpl implements UserActivationService {
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    public UserActivationServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }


    @Override
    public boolean activation(String cryptoUserId) {
        var userId = cryptoTool.idOf(cryptoUserId);
        var userOptional = appUserDAO.findById(userId);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            user.setIsActive(true);
            appUserDAO.save(user);
            return true;
        }
        return false;
    }
}
