﻿/*
 ===============================================================================
 ==  Grid관련 공통함수들은 여기에 작성한다.
 ===============================================================================
 ● gfnGridSort      : 그리드의 Sort를 처리한다.
 ● gfnclearSortMark : Seleted Column을 제외한 Sort Mark 제거
 ● gfnDsCheckValid  : DataSet 내의 데이터 무결성을 검사하는 함수
 ● gfnItemCheck     : DataSet 내의 데이터 무결성을 검사하는 함수
 */

// 헤더 클릭시 정렬
this.flv_constAscMark = "↓";
this.flv_constDescMark = "↑";
this.flv_constSortFlag = false;

/*******************************************************************************
 * Function Name: gfnGridSort
 * Description  : 그리드의 Sort를 처리한다.
 * Arguments    : object Grid, GridClickEventInfo e
 * Return       : None
 ******************************************************************************/
this.gfnGridSort = function (obj:Grid, e:GridClickEventInfo)
{
	// 컬럼의 정렬방식을 'head'의 text에 "↑,↓"여부로 판단.
	// 이미지로 대체 가능.

	var strType = obj.getCellProperty("head", e.cell, "displaytype");
	if (strType == "checkbox") 
	{
		return;
	}

	var bindDs = eval(obj.binddataset);
	if (bindDs.rowcount == 0) 
	{
		return false;
	}

	var BodyColId = (obj.getCellProperty("body", e.col, "text")).toString().split(":");
	for (var i = 0; i < obj.getCellCount("head"); i++) 
	{
		if (obj.getCellText(-1, i) == "undefined") 
		{
			continue;
		}

		var strHeadText = obj.getCellText(-1, i);

		if (i == e.cell) 
		{
			if (strHeadText.substr(strHeadText.length - 1) == this.flv_constAscMark) 
			{
				obj.setCellProperty("head", i, "text", strHeadText.substr(0, strHeadText.length - 1) + this.flv_constDescMark);
				bindDs.set_keystring(("S:-" + BodyColId[1]));
			}
			else if (strHeadText.substr(strHeadText.length - 1) == this.flv_constDescMark) 
			{
				obj.setCellProperty("head", i, "text", strHeadText.substr(0, strHeadText.length - 1) + this.flv_constAscMark);
				bindDs.set_keystring(("S:+" + BodyColId[1]));
			}
			else 
			{
				obj.setCellProperty("head", i, "text", strHeadText + this.flv_constAscMark);
				bindDs.set_keystring(("S:+" + BodyColId[1]));
			}
		}
		else 
		{
			// 정렬표시 삭제
			if (strHeadText.substr(strHeadText.length - 1) == this.flv_constAscMark || strHeadText.substr(strHeadText.length - 1) == this.flv_constDescMark) 
			{
				obj.setCellProperty("head", i, "text", strHeadText.substr(0, strHeadText.length - 1));
			}
		}
	}
}

/*******************************************************************************
 * Function Name: gfnClearSortMark
 * Description  : Seleted Column을 제외한 Sort Mark 제거
 * Arguments    : grdObj: Grid, nCell: cell index
 * Return       : None
 ******************************************************************************/
this.gfnClearSortMark = function (grdObj, nCell)
{
	var nColCnt = grdObj.getCellCount("head");
	var sRepText;

	for (var ii = 0; ii < nColCnt; ii++) 
	{
		if (nCell != null && nCell == ii) 
		{
			continue;
		}
		// 선택한 Cell을 제외하고 처리
		if (grdObj.getCellProperty("head", ii, "text") == null) 
		{
			continue;
		}

		sRepText = grdObj.getCellProperty("head", ii, "text").split(this.flv_constAscMark).join("").split(this.flv_constDescMark).join("");
		grdObj.setCellProperty("head", ii, "text", sRepText);
	}
}

/********************************************************************************
 * Function Name	: gfnSetGridCheckAll										*
 * Desc				: Grid Head중 check box가 있을 경우, check box 클릭		*
 *					  이벤트 발생시 전체 row에 대한 check/uncheck 설정 함수	*
 * Parameter		: obj = Grid												*
 *					  e = GridClickEventInfo									*
 * Return 			: 															*
 ********************************************************************************/
this.gfnSetGridCheckAll = function (obj:Grid, e:GridClickEventInfo)
{
	if (obj.readonly == true) 
	{
		return;
	}

	var strVal;
	var strChkCol;
	var dsObj;

	dsObj = eval(obj.binddataset);
	strChkCol = this.gfn_Nvl(obj.getCellProperty("body", e.col, "text"), "");
	strChkCol = strChkCol.split("bind:").join("");

	var strType = obj.getCellProperty("head", e.cell, "displaytype");
	if (strType != "checkbox") 
	{
		return;
	}

	// Head셋팅
	strVal = obj.getCellProperty("head", e.cell, "text");
	if (this.gfn_isNull(strVal)) 
	{
		strVal = "0";
	}

	if (strVal == "0") 
	{
		obj.setCellProperty("head", e.cell, "text", '1');
		strVal = "1";
	}
	else 
	{
		obj.setCellProperty("head", e.cell, "text", '0');
		strVal = "0";
	}

	// Body셋팅
	dsObj.set_enableevent(false);
	for (var i = 0; i < dsObj.rowcount; i++) 
	{
		dsObj.setColumn(i, strChkCol, strVal);
	}
	dsObj.set_enableevent(true);
}
