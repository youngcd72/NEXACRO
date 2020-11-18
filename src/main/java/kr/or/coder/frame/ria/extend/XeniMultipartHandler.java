package kr.or.coder.frame.ria.extend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.nexacro17.xapi.data.DataSet;
import com.nexacro17.xapi.data.PlatformData;
import com.nexacro17.xapi.tx.PlatformException;
import com.nexacro17.xapi.tx.impl.PlatformXmlDataDeserializer;
import com.nexacro17.xeni.extend.XeniMultipartProcBase;
import com.nexacro17.xeni.extend.XeniMultipartReqData;
import com.nexacro17.xeni.util.Constants;

/**
 * <pre>
 * XENI에서 Spring의 MultipartRequest를 처리하기 위한 구현체
 * Spring의 MultipartResolver가 등록 되어 있을 경우에 xeni.properties을 이용하여 등록하여 사용한다.
 *      xeni.multipart.proc=com.nexacro.spring.servlet.XeniMultipartHandler 이름으로 등록 가능하다.
 * </pre>
 *
 * @author Park SeongMin
 * @since 08.24.2015
 * @version 1.0
 * @see XeniMultipartProcBase
 */
public class XeniMultipartHandler implements XeniMultipartProcBase  {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Override
    public XeniMultipartReqData getImportData(HttpServletRequest servletRequest) throws Exception {

        XeniMultipartReqData requestData = new XeniMultipartReqData();
        
        if(!(servletRequest instanceof MultipartHttpServletRequest)) {
            throw new IllegalArgumentException("Request is not a MultipartHttpServletRequest");
        }
        
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) servletRequest;
        
        // ssv stream
        PlatformData platformData = findPlatformData(multipartRequest);
        requestData.setPlatformData(platformData);
        
        // files..
        String sName = null;
        InputStream insFile = null;
        
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        Set<String> keySet = fileMap.keySet();
        for(String name: keySet) {
            
            MultipartFile multipartFile = fileMap.get(name);
            // fileName..
            sName = multipartFile.getOriginalFilename();
            sName = sName.replaceAll("\\\\", "/");
            int nIdx = sName.lastIndexOf('/');
            if (nIdx >= 0)
                sName = sName.substring(nIdx + 1); 
            
            InputStream in = multipartFile.getInputStream();
            insFile = new ByteArrayInputStream(IOUtils.toByteArray(in));
         
            in.close();
            
            if (logger.isDebugEnabled()) {
                logger.debug("File field " + name + " with file name " + sName + " detected.");
            }
            
        }
        
        requestData.setFileName(sName);
        requestData.setFileStream(insFile);
        
        return requestData;
    }

    /**
     * Multipart 에서 PlatformData 추출
     * @param multipartRequest
     * @return
     * @throws IOException
     * @throws PlatformException
     */
    private PlatformData findPlatformData(MultipartHttpServletRequest multipartRequest) throws IOException, PlatformException {
        
    	/**
    	 * MultiPart 로 전송된 데이터 중 엑셀관련 정보 데이터만 처리.
    	 * ds_command << 클라이언트에서 전송된 nexacroplatform 데이터의 Parameter Name
    	 */
    	String parameterName = "ds_command";
    	String parameter = multipartRequest.getParameter(parameterName);
    	
    	if(parameter == null || "".equals(parameter)) {
    		throw new PlatformException("ds_command is Null !");
    	}
    	
        PlatformXmlDataDeserializer dataDes = new PlatformXmlDataDeserializer();
        PlatformData platformData = dataDes.readData(new StringReader(parameter), null, "UTF-8");
        if(platformData == null) {
            //return null;
        	throw new PlatformException("PlatformData is Null !");
        }
        
        DataSet dsCmd = platformData.getDataSet(Constants.DATASET_COMMAND);
        if(dsCmd == null) {
            return platformData;
        }
        
        // multipart는 import만 처리 된다. 그 외는 예외를 던진다.
        String command = dsCmd.getString(0, Constants.COMMAND_COMMAND);
        if (!Constants.COMMAND_IMPORT.equalsIgnoreCase(command)) {
            throw new PlatformException("multipart request is supported only "+Constants.COMMAND_IMPORT);
        }
        
        // server에 존재하는 파일은 사용하지 않는다.
        String sMode = dsCmd.getString(0, Constants.COMMAND_FILEMODE);
        if (!"local".equalsIgnoreCase(sMode)) {
            throw new PlatformException("supported only local mode.");
        }
        
        return platformData;
    }
}
