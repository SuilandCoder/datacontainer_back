package njnu.opengms.container.service;

import com.alibaba.fastjson.JSONObject;
import njnu.opengms.container.component.GeoserverConfig;
import njnu.opengms.container.component.PathConfig;
import njnu.opengms.container.enums.DataResourceTypeEnum;
import njnu.opengms.container.enums.ResultEnum;
import njnu.opengms.container.exception.MyException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * @ClassName GeoServerService
 * @Description todo
 * @Author sun_liber
 * @Date 2019/4/8
 * @Version 1.0.0
 */
@Service
public class GeoserverService {

    @Autowired
    GeoserverConfig geoserverConfig;

    @Autowired
    PathConfig pathConfig;

    @Autowired
    RestTemplate restTemplate;

    public JSONObject getLayers() {
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/layers.json";
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuth(null), JSONObject.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        return responseEntity.getBody();
    }

    public HttpEntity setAuth(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(geoserverConfig.getUsername(), geoserverConfig.getPassword());
        if (body == null || body.equals("")) {
            return new HttpEntity<>(null, headers);
        } else {
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new HttpEntity<>(body, headers);
        }

    }

    public JSONObject getStores() {
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/workspaces.json";
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET, setAuth(null), JSONObject.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        return responseEntity.getBody();
    }

    /**
     * 根据id去指定文件夹找到对应的shapefile，发布到geoserver中
     *
     * @param id
     */
    public String createShapeFile(String id) {
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/workspaces/datacontainer/datastores/shapefileList/external.shp?update=overwrite";
        File dir = new File(pathConfig.getShapefiles());
        Collection<File> fileCollection = FileUtils.listFiles(dir, FileFilterUtils.and(new SuffixFileFilter(".shp"), new PrefixFileFilter(id)), null);
        File real_file = fileCollection.iterator().next();
        //注意这里是PUT请求
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, setAuth(geoserverConfig.getShapefiles() + File.separator
                + real_file.getName()), String.class);
        if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
            //注意这里geoserver返回的HttpStatus是201
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        return real_file.getName();
    }

    public String createGeotiff(String id) {
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/workspaces/datacontainer/coveragestores/" + id + "/external.geotiff";
        File dir = new File(pathConfig.getGeotiffes());
        Collection<File> fileCollection = FileUtils.listFiles(dir, FileFilterUtils.and(new SuffixFileFilter(".tif"), new PrefixFileFilter(id)), null);
        File real_file = fileCollection.iterator().next();
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, setAuth(geoserverConfig.getGeotiffes() + File.separator + real_file.getName()), String.class);
        if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        return real_file.getName();
    }

    public void delete(String id, DataResourceTypeEnum type) throws IOException {
        if (type.equals(DataResourceTypeEnum.SHAPEFILE)) {
            deleteShapeFile(id);
        } else if (type.equals(DataResourceTypeEnum.GEOTIFF)) {
            deleteGeotiff(id);
        } else {
            throw new MyException(ResultEnum.NOTSUPPORT_GEOSERVER_ERROR);
        }
    }

    public void deleteShapeFile(String id) throws IOException {
        File dir = new File(pathConfig.getShapefiles());
        Collection<File> fileCollection = FileUtils.listFiles(dir, FileFilterUtils.and(new SuffixFileFilter(".shp"), new PrefixFileFilter(id)), null);
        File real_file = fileCollection.iterator().next();
        String storeName = FilenameUtils.getBaseName(real_file.getName());
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/workspaces/datacontainer/datastores/shapefileList/featuretypes/" + storeName + "?recurse=true";
        //注意这里是PUT请求
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, setAuth(null), String.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        fileCollection = FileUtils.listFiles(dir, new PrefixFileFilter(id), null);
        for (File fileForDelete : fileCollection) {
            FileUtils.deleteQuietly(fileForDelete);
        }
    }

    public void deleteGeotiff(String id) throws IOException {
        String url = geoserverConfig.getBasicURL() + "/geoserver/rest/workspaces/datacontainer/coveragestores/" + id + "?recurse=true";
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, setAuth(null), String.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        File dir = new File(pathConfig.getGeotiffes());
        Collection<File> fileCollection = FileUtils.listFiles(dir, new PrefixFileFilter(id), null);
        for (File fileForDelte : fileCollection) {
            FileUtils.deleteQuietly(fileForDelte);
        }
    }


}
