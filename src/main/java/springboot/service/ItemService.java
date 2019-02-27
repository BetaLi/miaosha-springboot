package springboot.service;

import springboot.error.BusinessException;
import springboot.service.model.ItemModel;

import java.util.List;

public interface ItemService {
    ItemModel createItem(ItemModel itemModel) throws BusinessException;
    List<ItemModel> getListItem();
}
