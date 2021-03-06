package kr.or.coder.nexacro.cmmn.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import kr.or.coder.frame.dao.BaseDAO;

@Repository("codeDAO")
public class CodeDAO extends BaseDAO {

	public List<Map<String, Object>> getCodeList(String grpCode) {
		
		return selectList("", grpCode);
	}
}
