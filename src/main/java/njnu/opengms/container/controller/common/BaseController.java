package njnu.opengms.container.controller.common;


import njnu.opengms.container.service.common.BaseService;

/**
 * @param <E>   实体
 * @param <AD>  AddDTO
 * @param <UD>  UpdateDTO
 * @param <FD>  FindDTO
 * @param <VO>  VisualizationObject
 * @param <UID> ID
 * @param <S>   Service层对象
 *              注意这里针对实体的一般性的CRUD，若需求不一样可自行定制
 *
 * @InterfaceName BaseController
 * @Description todo
 * @Author sun_liber
 * @Date 2018/9/8
 * @Version 1.0.0
 */
public interface BaseController<E, AD, UD, FD, VO, UID, S extends BaseService<E, AD, UD, FD, VO, UID>> extends
        CreateController<AD, S>,
        DeleteController<UID, S>,
        QueryController<E, FD, VO, UID, S>,
        UpdateController<UID, UD, S> {
}
