package kr.or.coder.frame.ria.mybatis;

import org.springframework.core.NamedThreadLocal;

public class LookupResultSetMetaDataHolder {
	private static final ThreadLocal<LookupResultSetMetaDataConfig> currentMybatisConfig =
			new NamedThreadLocal<LookupResultSetMetaDataConfig>("Mybatis lookup Configuration");
	
	static void resetLookupResultSetMetaDataConfig() {
		currentMybatisConfig.remove();
	}

	static void setLookupResultSetMetaDataConfig(LookupResultSetMetaDataConfig config) {
		if (config == null) {
			resetLookupResultSetMetaDataConfig();
		}
		else {
			currentMybatisConfig.set(config);
		}
	}

	static LookupResultSetMetaDataConfig getLookupResultSetMetaDataConfig() {
		return currentMybatisConfig.get();
	}
}
