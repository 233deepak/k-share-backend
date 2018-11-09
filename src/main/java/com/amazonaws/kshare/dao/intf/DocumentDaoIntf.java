package com.amazonaws.kshare.dao.intf;

import com.amazonaws.kshare.exception.CouldNotCreateTopicException;
import com.amazonaws.kshare.exception.TableDoesNotExistException;
import com.amazonaws.kshare.exception.TableExistsException;
import com.amazonaws.kshare.model.Document;

public interface DocumentDaoIntf {
	
	public void createDocumentTable() throws TableExistsException;

	public Document addDocument(Document doc) throws CouldNotCreateTopicException;
	
	Document getDocument(String documentId) throws TableDoesNotExistException;
}
