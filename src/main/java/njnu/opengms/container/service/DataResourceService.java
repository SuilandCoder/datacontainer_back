package njnu.opengms.container.service;

import njnu.opengms.container.dto.dataresource.AddDataResourceDTO;
import njnu.opengms.container.dto.dataresource.FindDataResourceDTO;
import njnu.opengms.container.dto.dataresource.UpdateDataResourceDTO;
import njnu.opengms.container.enums.ResultEnum;
import njnu.opengms.container.exception.MyException;
import njnu.opengms.container.pojo.DataResource;
import njnu.opengms.container.repository.DataResourceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @ClassName DataResourceService
 * @Description todo
 * @Author sun_liber
 * @Date 2019/2/13
 * @Version 1.0.0
 */
@Service
public class DataResourceService {

    @Autowired
    DataResourceRepository dataResourceRepository;


    public void save(String id, UpdateDataResourceDTO updateDataResourceDTO) {
        DataResource dataResource = dataResourceRepository.findById(id).orElseGet(() -> {
            System.out.println("有人乱查数据库！！");
            throw new MyException(ResultEnum.NO_OBJECT);
        });
        BeanUtils.copyProperties(updateDataResourceDTO, dataResource);
        dataResourceRepository.save(dataResource);

    }

    public DataResource add(AddDataResourceDTO addDataResourceDTO) {
        DataResource dataResource = new DataResource();
        BeanUtils.copyProperties(addDataResourceDTO, dataResource);
        dataResource.setCreateDate(new Date());
        return dataResourceRepository.insert(dataResource);
    }

    public DataResource getById(String id) {
        return dataResourceRepository.findById(id).orElseGet(() -> {
            System.out.println("有人乱查数据库！！");
            throw new MyException(ResultEnum.NO_OBJECT);
        });
    }

    public void delete(String id) {
        dataResourceRepository.deleteById(id);
    }

    /**
     * TODO 目前只考虑分页，不存在说复杂查询的说法
     *
     * @param findDataResourceDTO
     *
     * @return
     */
    public Page<DataResource> list(FindDataResourceDTO findDataResourceDTO) {
        PageRequest pageRequest = PageRequest.of(findDataResourceDTO.getPage(), findDataResourceDTO.getPageSize());
        return dataResourceRepository.findAll(pageRequest);
    }

    public long count() {
        return dataResourceRepository.count();
    }

    public List<DataResource> listByAuthor(String author) {
        return dataResourceRepository.findByAuthor(author);
    }

    public List<DataResource> listByDataItemId(String dataItemId) {
        return dataResourceRepository.findByDataItemId(dataItemId);
    }

    public List<DataResource> listByMdlId(String mdlId) {
        return dataResourceRepository.findByMdlId(mdlId);
    }

    public List<DataResource> listByDataItemName(String dataItemFileName) {
        return dataResourceRepository.findByFileNameContains(dataItemFileName);
    }
}
