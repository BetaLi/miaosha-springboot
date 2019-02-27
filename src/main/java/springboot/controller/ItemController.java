package springboot.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springboot.controller.viewobject.ItemVO;
import springboot.error.BusinessException;
import springboot.error.EmBusinessError;
import springboot.response.CommonReturnType;
import springboot.service.impl.ItemServiceImpl;
import springboot.service.model.ItemModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller("item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true",origins = {"*"})
public class ItemController extends BaseController {
    @Autowired
    ItemServiceImpl itemService;

    @RequestMapping(value = "/create",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    private CommonReturnType createItem(@RequestParam(name = "title")String title,
                                        @RequestParam(name = "description")String description,
                                        @RequestParam(name = "price")BigDecimal price,
                                        @RequestParam(name = "sales")Integer sales,
                                        @RequestParam(name = "imgUrl")String imgUrl,
                                        @RequestParam(name = "stock")Integer stock) throws BusinessException {
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setImgUrl(imgUrl);
        itemModel.setSales(sales);
        itemModel.setStock(stock);

        itemService.createItem(itemModel);

        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "/list",method = {RequestMethod.GET})
    @ResponseBody
    private CommonReturnType getListItem(){
        List<ItemModel> itemModelList = itemService.getListItem();
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = null;
            try {
                itemVO = convertVOFromModel(itemModel);
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            return itemVO;
        }).collect(Collectors.toList());

        return CommonReturnType.create(itemVOList);
    }

    private ItemVO convertVOFromModel(ItemModel itemModel) throws BusinessException {
        if(itemModel == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR);
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        return itemVO;
    }

}
