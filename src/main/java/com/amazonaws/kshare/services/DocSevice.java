package com.amazonaws.kshare.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.amazonaws.kshare.Request;
import com.amazonaws.kshare.configuration.AppConfig;
import com.amazonaws.kshare.dao.intf.DocumentDaoIntf;
import com.amazonaws.kshare.exception.CouldNotCreateTopicException;
import com.amazonaws.kshare.exception.TableDoesNotExistException;
import com.amazonaws.kshare.exception.TableExistsException;
import com.amazonaws.kshare.model.Document;
import com.amazonaws.kshare.model.Page;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DocSevice implements ResourceMethodHandler<Document> {

	private DocumentDaoIntf documentDao;
	private ObjectMapper objectMapper;

	public DocSevice() {
		this.documentDao = AppConfig.getInstance().documentDao();
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public Document doGet(Request request) {
		if(request.getPathParameters()!=null) {
			try {
				String docId = request.getPathParameters().getString("doc_id");
				return documentDao.getDocument(docId);
			} catch (JSONException | TableDoesNotExistException e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	@Override
	public Page<Document> doPost(Request request) {
		return null;
	}

	@Override
	public Document doPut(Request request) {
		JSONObject requestBody = request.getBody();
		Document document = null;
		try {
			document = objectMapper.readValue(requestBody.toString(), Document.class);
			documentDao.addDocument(document);
		} catch (IOException | CouldNotCreateTopicException e) {
			e.printStackTrace();
		}
		return document;
	}

	@Override
	public void doOption(Request request) {

	}

	public void initializeDocumentTable() {
		try {
			documentDao.createDocumentTable();
			InputStream fileURL = this.getClass().getClassLoader().getResourceAsStream("mockDocument.json");
			List<Document> documents = objectMapper.readValue(fileURL,
					objectMapper.getTypeFactory().constructCollectionType(List.class, Document.class));
			for (int i = 0; i < 10; i++) {
				for(Document document :documents)
					documentDao.addDocument(document);
			}
		} catch (TableExistsException | IOException | CouldNotCreateTopicException e) {
			e.printStackTrace();
		}
	}
}
