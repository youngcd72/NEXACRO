package kr.or.coder.frame.ria.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nexacro17.xapi.data.DataSet;
import com.nexacro17.xapi.data.DataTypes;
import com.nexacro17.xapi.data.Variable;
import com.nexacro17.xapi.data.VariableList;
import com.nexacro17.xapi.data.datatype.DataType;

import kr.or.coder.frame.ria.data.RiaRstDataset.RiaRstMap;

/**
 * ria data 변환
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
public class ConvertRiaData {

	/**
	 * data type을 가져온다.
	 * 
	 * @param Object obj
	 * @return int
	 * @throws 
	 */	  		
	@SuppressWarnings("rawtypes")
	public static int getPlatformDataType(Object obj) {

		int dataType = DataTypes.STRING;

		if (obj== null) {
			return dataType;
		}

		Class clz = obj.getClass();
		String typeName = clz.getName();

		if (typeName.equals(String.class.getName())) {
			dataType = DataTypes.STRING;
		} else if (typeName.equals(Integer.class.getName())) {
			dataType = DataTypes.INT;
		} else if (typeName.equals(Boolean.class.getName())) {
			dataType = DataTypes.INT;
		} else if (typeName.equals(Long.class.getName())) {
			dataType = DataTypes.BIG_DECIMAL;
		} else if (typeName.equals(Double.class.getName())) {
			dataType = DataTypes.FLOAT;
		} else if (typeName.equals(Date.class.getName())) {
			dataType = DataTypes.DATE_TIME;
		} else if (typeName.equals(Byte[].class.getName())) {
			dataType = DataTypes.BLOB;
		}
		return dataType;
	}
	
	/**
	 * Nexacro variable을 map으로 변환
	 * 
	 * @param  VariableList
	 * @return Map
	 * @throws 
	 */
	public static void convertVariableToMap(VariableList variableList, Map<String, Object> map) {
		
        for(int i = 0; i < variableList.size(); i++) {

            Variable var = variableList.get(i);
            map.put(var.getName(), var.getObject());
        }
	}

	/**
	 * Nexacro dataset을 RiaDataset으로 변환
	 * 
	 * @param  VariableList
	 * @return Map
	 * @throws 
	 */
	public static RiaReqDataset convertDatasetToRiaDataset(DataSet ds) {

        RiaReqDataset dsMap = new RiaReqDataset(ds.getName());

        Map<String, Object> insDsMap = new HashMap<String, Object>();
        Map<String, Object> uptDsMap = new HashMap<String, Object>();
        Map<String, Object> delDsMap = new HashMap<String, Object>();
        Map<String, Object> readDsMap = new HashMap<String, Object>();

        /* dataset insert/update 설정 */
        for(int j = 0; j < ds.getRowCount(); j++) {
                
            for(int k = 0; k < ds.getColumnCount(); k++) {
                    
                if(DataSet.ROW_TYPE_INSERTED == ds.getRowType(j)) {
                    insDsMap.put(ds.getColumn(k).getName(), ds.getObject(j, k));
                    dsMap.addInsDsMap(insDsMap);
                } else if(DataSet.ROW_TYPE_UPDATED == ds.getRowType(j)) {
                    uptDsMap.put(ds.getColumn(k).getName(), ds.getObject(j, k));
                    dsMap.addUptDsMap(uptDsMap);
                } else {
                	readDsMap.put(ds.getColumn(k).getName(), ds.getObject(j, k));
                	dsMap.addReadDsMap(readDsMap);
                }
            }
        }

        /* dataset delete 설정 */
        for(int j = 0; j < ds.getRemovedRowCount(); j++) {

            for(int k = 0; k < ds.getColumnCount(); k++) {
                delDsMap.put(ds.getColumn(j).getName(), ds.getRemovedData(j, k));
            }
                dsMap.addDelDsMap(delDsMap);
        }
        return dsMap;
	}

	/**
	 * map을 Nexacro dataset으로 변환
	 * 
	 * @param  String
	 * @param  Map<String, Object>
	 * @return DataSet
	 * @throws 
	 */
	public static DataSet convertMapToDataset(String dsName, Map<String, Object> dsMap) {

		List<Map<String, Object>> dsMapList = new ArrayList<Map<String, Object>>();
		dsMapList.add(dsMap);
		
		return convertMapListToDataset(dsName, dsMapList);
	}

	/**
	 * map을 Nexacro dataset으로 변환
	 * 
	 * @param  String
	 * @param  List<Map<String, Object>>
	 * @return DataSet
	 * @throws 
	 */
	public static DataSet convertMapListToDataset(String dsName, List<Map<String, Object>> dsMapList) {

		DataSet ds = new DataSet(dsName);

		if(dsMapList != null && !dsMapList.isEmpty()) {

			// Header 생성
			Map<String, Object> frstRowMap = dsMapList.get(0);
			Set<String> headCols = frstRowMap.keySet();
			
			for(String headCol : headCols) {
				ds.addColumn(headCol, getPlatformDataType(frstRowMap.get(headCol)));
			}
			
			// Body 생성
			for(int row = 0; row < dsMapList.size(); row++) {
	
				Map<String, Object> dsMap = dsMapList.get(row);
				Set<String> colNms = dsMap.keySet();
	
				for(String colNm : colNms) {
	
					ds.set(row, colNm, dsMap.get(colNm));
				}
			}
		}
		return ds;
	}

	/**
	 * map을 Nexacro dataset으로 변환
	 * 
	 * @param  String
	 * @param  List<Map<String, Object>>
	 * @return DataSet
	 * @throws 
	 */
	public static DataSet convertRiaRstDatasetToDataset(String dsName, RiaRstDataset riaRstDs) {

		DataSet ds = new DataSet(dsName);

		if(riaRstDs != null) {

			// Header 생성
			List<String> colNmList = new ArrayList<String>();

			Map<String, DataType> map = riaRstDs.getMetaDataMap(); 
			Set<String> colNmSet = map.keySet();
			
			for(String colNm : colNmSet) {
				
				ds.addColumn(colNm, map.get(colNm));
				colNmList.add(colNm);
			}

			// Body 생성
			for(int row = 0; row < riaRstDs.getRiaRstMapList().size(); row++) {
	
				Map<String, Object> dsMap = riaRstDs.getRiaRstMapList().get(row);
				Set<String> colNms = dsMap.keySet();
	
				for(String colNm : colNms) {
	
					ds.set(row, colNm, dsMap.get(colNm));
				}
			}
		}
		return ds;
	}
	
	
	/**
	 * Object을 Nexacro variable로 변환
	 * 
	 * @param  String
	 * @param  Object
	 * @return DataSet
	 * @throws 
	 */
	public static Variable convertObjectToVariable(String varNm, Object value) {

		Variable var = new Variable(varNm);
		var.setType(getPlatformDataType(value));
		var.set(value);
		
		return var;
	}
}
