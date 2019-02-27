package springboot.controller;

import com.alibaba.druid.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springboot.controller.viewobject.UserVO;
import springboot.dao.UserDOMapper;
import springboot.dataobject.UserDO;
import springboot.error.BusinessException;
import springboot.error.EmBusinessError;
import springboot.response.CommonReturnType;
import springboot.service.impl.UserServiceImpl;
import springboot.service.model.UserModel;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",origins = {"*"},allowedHeaders = {"*"})
public class UserController extends BaseController{

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserDOMapper userDOMapper;

    @RequestMapping(value = "/get",method = {RequestMethod.GET})
    @ResponseBody
    private CommonReturnType getUser(@RequestParam(name = "id")Integer id) throws BusinessException {
        UserModel userModel = userService.getUserById(id);
        UserVO userVO = convertUserVO(userModel);
        return CommonReturnType.create(userVO);
    }

    public UserVO convertUserVO(UserModel userModel) throws BusinessException {
        if(userModel==null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        UserVO userVO = new UserVO();

        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }

    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    private CommonReturnType getOtp(@RequestParam(name = "telphone")String telphone){
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt+=100000;
        String otpCode = String.valueOf(randomInt);

        httpServletRequest.getSession().setAttribute(telphone,otpCode);

        System.out.println("手机号码："+telphone+",验证码："+otpCode);

        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    private CommonReturnType register(@RequestParam(name = "telphone")String telphone,
                                      @RequestParam(name = "otpCode")String otpCode,
                                      @RequestParam(name = "name")String name,
                                      @RequestParam(name = "gender")Integer gender,
                                      @RequestParam(name = "age")Integer age,
                                      @RequestParam(name = "password")String password) throws BusinessException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String inSessionOtpCode = (String) httpServletRequest.getSession().getAttribute(telphone);
        if(!com.alibaba.druid.util.StringUtils.equals(inSessionOtpCode,otpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR,"验证码验证失败！");
        }
        //用户注册流程开始
        UserModel userModel = new UserModel();
        userModel.setTelphone(telphone);
        userModel.setName(name);
        userModel.setGender(gender);
        userModel.setAge(age);
        userModel.setRegisterMode("byphone");
        userModel.setEncryptPassword(this.EncodeByMD5(password));

        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "/login",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    private CommonReturnType login(@RequestParam(name = "telphone")String telphone,
                                   @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if(StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR,"用户名或密码不能为空");
        }
        UserModel userModel = userService.validateLogin(telphone,EncodeByMD5(password));

        httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        return CommonReturnType.create(null);
    }

    public String EncodeByMD5(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();

        String encryptString = base64Encoder.encode(md5.digest(password.getBytes("utf-8")));
        return encryptString;
    }

}
