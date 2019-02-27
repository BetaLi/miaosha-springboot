package springboot.service;

import springboot.error.BusinessException;
import springboot.service.model.UserModel;

public interface UserService {
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BusinessException;
    UserModel validateLogin(String telphone, String password) throws BusinessException;
}
