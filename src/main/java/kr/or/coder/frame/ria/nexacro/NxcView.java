package kr.or.coder.frame.ria.nexacro;

import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

import com.nexacro17.xapi.data.PlatformData;
import com.nexacro17.xapi.tx.PlatformResponse;
import com.nexacro17.xapi.tx.PlatformType;

import kr.or.coder.frame.ria.util.RiaRequestUtil;


/**
 * Map 형태의 DTO 들을 Nexacro Data에 맞게 변경하는 View
 * 
 * @author 공통팀
 * @since 2020.11.18
 * @version 1.0
 * 
 * <pre>
 * 수정일       수정자              수정내용
 * ----------  --------    ---------------------------
 * 2020.11.18  	공통팀        		최초 생성
 * </pre>
 */
public class NxcView extends AbstractView {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
    private Properties sysProps;
    
    @Resource(name = "systemProps")
    public void setSystemProps(Properties sysProps) {
        this.sysProps = sysProps;
    }
	
	/**
	 * 뷰 수행
	 * @param Map map
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return 
	 * @throws Exception
	 */	  		 
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 생성된 데이터를 넘기기 위해 변환
		PlatformData platformData = (PlatformData)model.get(NexacroConstant.OUT_RESULT_DATA);
		
		PlatformResponse platformResponse = null;
		
		/* XPlatform 형식 처리 */
		if(RiaRequestUtil.isXplatformRequest(request)) {
			platformResponse = new PlatformResponse(response.getOutputStream(), PlatformType.CONTENT_TYPE_BINARY, PlatformType.DEFAULT_CHAR_SET);
		} else {
			platformResponse = new PlatformResponse(response.getOutputStream(), sysProps.getProperty("nexacro.content.type"), PlatformType.DEFAULT_CHAR_SET);
		}
        platformResponse.setData(platformData);
        platformResponse.sendData();
	}
}