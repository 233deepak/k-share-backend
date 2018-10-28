package com.aws.codestar.projecttemplates.services;

import com.aws.codestar.projecttemplates.common.Request;

public interface ResourceMethodHandler<T> {

	void doGet(Request request);

	T doPost(Request request);

	T doPut(Request request);

	void doOption(Request request);

}
