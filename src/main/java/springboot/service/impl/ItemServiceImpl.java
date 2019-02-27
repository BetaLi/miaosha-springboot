package springboot.service.impl;

import com.alibaba.druid.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.dao.ItemDOMapper;
import springboot.dao.ItemStockDOMapper;
import springboot.dataobject.ItemDO;
import springboot.dataobject.ItemStockDO;
import springboot.error.BusinessException;
import springboot.error.EmBusinessError;
import springboot.service.ItemService;
import springboot.service.model.ItemModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemDOMapper itemDOMapper;

    @Autowired
    ItemStockDOMapper itemStockDOMapper;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        if(itemModel == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR);
        if(StringUtils.isEmpty(itemModel.getTitle()) || StringUtils.isEmpty(itemModel.getDescription())
                ||StringUtils.isEmpty(itemModel.getImgUrl()) || itemModel.getPrice()==null || itemModel.getSales() == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR,"缺少商品参数");
        }
        ItemDO itemDO = convertFromModel(itemModel);
        itemDOMapper.insertSelective(itemDO);

        ItemStockDO itemStockDO = convertStockFromModel(itemModel);
        itemStockDO.setItemId(itemDO.getId());

        itemStockDOMapper.insertSelective(itemStockDO);

        return this.getItemById(itemDO.getId());
    }

    @Override
    public List<ItemModel> getListItem() {
        List<ItemDO> itemDOList = itemDOMapper.selectListItem();
        List<ItemModel> itemModelList =  itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = null;
            try {
                itemModel = this.convertFromDataObject(itemDO,itemStockDO);
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            return itemModel;
        }).collect(Collectors.toList());

        return itemModelList;
    }

    private ItemModel getItemById(Integer id) throws BusinessException {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if(itemDO == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR);

        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
        if(itemStockDO == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR);

        ItemModel itemModel = convertFromDataObject(itemDO,itemStockDO);

        return itemModel;
    }

    private ItemModel convertFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO) throws BusinessException {
        if(itemDO == null || itemStockDO == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR);
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }

    private ItemDO convertFromModel(ItemModel itemModel) throws BusinessException {
        if(itemModel == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR,"创建商品时属性不能为空");
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        return itemDO;
    }

    private ItemStockDO convertStockFromModel(ItemModel itemModel) throws BusinessException {
        if(itemModel == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATE_ERROR,"创建商品时属性不能为空");
        ItemStockDO itemStockDO = new ItemStockDO();
        BeanUtils.copyProperties(itemModel,itemStockDO);
        return itemStockDO;
    }
}
