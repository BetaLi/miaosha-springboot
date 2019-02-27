package springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot.dao.UserDOMapper;
import springboot.dataobject.UserDO;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {"springboot"})
@RestController
@MapperScan("springboot.dao")
public class App 
{
    @Autowired
    private UserDOMapper userDOMapper;

    @RequestMapping("/")
    private String home(){
       UserDO userDO =  userDOMapper.selectByPrimaryKey(1);
       return userDO.getGender().toString();
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        SpringApplication.run(App.class,args);
    }
}
