package com.abc.basic.algoritms.external;

import java.io.IOException;

public interface RecordStore {
	Record readNextRecord()throws IOException;
	void destroy();

}
