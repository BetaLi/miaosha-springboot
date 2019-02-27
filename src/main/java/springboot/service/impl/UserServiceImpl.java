package springboot.service.impl;

import com.alibaba.druid.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.dao.UserDOMapper;
import springboot.dao.UserPasswordDOMapper;
import springboot.dataobject.UserDO;
import springboot.dataobject.UserPasswordDO;
import springboot.error.BusinessException;
import springboot.error.EmBusinessError;
import springboot.service.UserService;
import springboot.service.model.UserModel;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDOMapper userDOMapper;

    @Autowired
    UserPasswordDOMapper userPasswordDOMapper;

    @Override
    public UserModel getUserById(Integer id) {
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if(userDO==null) return null;
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByPrimaryKey(id);
        UserModel userModel = assembleDO(userDO,userPasswordDO);
        return userModel;
    }

    public UserModel assembleDO(UserDO userDO, UserPasswordDO userPasswordDO){
        if(userDO==null) return null;
        if(userPasswordDO==null) return null;
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);
        userModel.setEncryptPassword(userPasswordDO.getEncryptPassword());

        return userModel;
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if(userModel==null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR,"用户注册参数有误");
        if(StringUtils.isEmpty(userModel.getName()) || userModel.getGender()==null ||userModel.getAge() ==null ||
           StringUtils.isEmpty(userModel.getTelphone()) || StringUtils.isEmpty(userModel.getEncryptPassword())){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR,"注册参数不能为空");
        }
        UserDO userDO = convertFromModel(userModel);
        try{
            userDOMapper.insertSelective(userDO);
        }catch(DuplicateKeyException ex){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR,"用户手机号码已注册");
        }
        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDO.setUserId(userDO.getId());
        userPasswordDOMapper.insertSelective(userPasswordDO);
        return;
    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel) throws BusinessException {
        if(userModel == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR);
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        BeanUtils.copyProperties(userModel,userPasswordDO);
        return userPasswordDO;
    }

    private UserDO convertFromModel(UserModel userModel) throws BusinessException {
        if(userModel == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR);
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }

    @Override
    public UserModel validateLogin(String telphone, String password) throws BusinessException {
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if(userDO == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR,"用户不存在");
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        if(!StringUtils.equals(userPasswordDO.getEncryptPassword(),password)){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserModel userModel = assembleDO(userDO,userPasswordDO);
        return userModel;
    }
}
