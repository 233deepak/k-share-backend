package com.amazonaws.kshare.services;

import com.amazonaws.kshare.Request;
import com.amazonaws.kshare.model.Page;

public interface ResourceMethodHandler<T> {

	void doGet(Request request);

	Page<T> doPost(Request request);

	T doPut(Request request);

	void doOption(Request request);

}
